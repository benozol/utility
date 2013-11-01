package org.biosemantics.utility.social;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.biosemantics.utility.social.Post.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;

public class TestRestFb implements SocialSearch {

	private static final String FACEBOOK_COM = "http://facebook.com/";
	public static final String KEYWORD_SEPARATOR = " ";
	private static final Post.Network NETWORK = Post.Network.FACEBOOK;

	@Override
	public Network getNetwork() {
		return NETWORK;
	}
	
	public static void main(String[] args) {
		List<String> keywords = SearchConfig.getInstance().getKeywordLists().get(0);
		try {
			Writer writer = new FileWriter(new File("/tmp/facebook.out"));
			try {
				new TestRestFb().search(keywords, new PostStore(writer), null);
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public SocialStream getSocialStream() {
		return null;
	}

	public void search(List<String> keywords, PostStore postStore, Date since) throws IOException {
		FacebookClient client = new DefaultFacebookClient(Accounts.FACEBOOK_ACCESS_TOKEN.getValue());
		String q = StringUtils.join(keywords, KEYWORD_SEPARATOR);
		List<Parameter> parameters = Arrays.asList(
				Parameter.with("q", q),
				Parameter.with("type", "post")
		);
		if (since != null)
			parameters.add(Parameter.with("since", new SimpleDateFormat("YYYY-MM-dd").format(since)));
		Connection<com.restfb.types.Post> fetch = client.fetchConnection("search", com.restfb.types.Post.class, parameters.toArray(new Parameter[]{}));
		List<com.restfb.types.Post> posts = fetch.getData();
		while (fetch != null) {
			logger.info("There are {} posts", posts.size());
			for (com.restfb.types.Post fbPost : posts) {
				postStore.store(post(keywords, fbPost));
			}
			String nextPage = fetch.getNextPageUrl();
			if (nextPage != null) {
				fetch = client.fetchConnectionPage(nextPage, com.restfb.types.Post.class);
				posts = fetch.getData();
			} else
				fetch = null;
		}
	}

	private Post post(List<String> keywords, com.restfb.types.Post fbPost) {
		String id = fbPost.getId();
		String title = null;
		String url = FACEBOOK_COM + fbPost.getId();
		String content = fbPost.getMessage();
		Date published = fbPost.getCreatedTime();
		
		List<String> referredUrls = null;
		if (fbPost.getLink() != null) 
			referredUrls = Arrays.asList(fbPost.getLink());
		String queryName = StringUtils.join(keywords, " ");
		return new Post(queryName, id, NETWORK, title, url, content, published, referredUrls);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(TestRestFb.class);

}
//out.println(fbPost.getId());
//out.println(" - user: " + fbPost.getFrom().getName());
//out.println(" - message: " + fbPost.getMessage().replace("\n".,  " ").replace("\r", " "));
//out.println(" - id: " + fbPost.getId());
//out.println(" - created: " + fbPost.getCreatedTime());
//if (fbPost.getPlace() != null)
//	out.println(" - location: " + fbPost.getPlace().getLocationAsString());
//if (fbPost.getLink() != null)
//	out.println(" - link: " + fbPost.getLink());
//if (fbPost.getDescription() != null)
//	out.println(" - description: " + fbPost.getDescription());
//Likes likes = fbPost.getLikes();
//if (likes != null && likes.getCount() != null && likes.getCount() > 0)
//	System.out.println(" - likes: " + fbPost.getLikesCount());
//Comments comments = fbPost.getComments();
//if (comments != null && comments.getCount() != null && comments.getCount() > 0)
//	System.out.println(" - comments: " + comments.getCount());
