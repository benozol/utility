package org.biosemantics.utility.social;

import static java.lang.System.out;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.Post;
import com.restfb.types.Post.Comments;
import com.restfb.types.Post.Likes;

public class TestRestFb {

	public static final String KEYWORD_SEPARATOR = " ";

	public static void main(String[] args) {
		new TestRestFb().test();
	}

	public void test() {
		FacebookClient client = new DefaultFacebookClient(FacebookAccount.ACCESS_TOKEN.getValue());
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (String keyword : SearchConfig.getInstance().getKeywords()) {
			builder.append(keyword);
			if (!first) {
				builder.append(KEYWORD_SEPARATOR);
				first = false;
			}
		}
		String q = StringUtils.join(SearchConfig.getInstance().getKeywords(), KEYWORD_SEPARATOR);
		Parameter[] parameters = new Parameter[] {
				Parameter.with("q", q),
				Parameter.with("type", "post"),
//				Parameter.with("since", "2 day ago"),
				Parameter.with("until", "now"),
				Parameter.with("limit", "4900")
		};
		List<Post> posts = client.fetchConnection("search", Post.class, parameters).getData();
		out.println("There are " + posts.size() + " posts");
		for (Post post : posts) {
			out.println(post.getId());
			out.println(" - user: " + post.getFrom().getName());
			out.println(" - message: " + post.getMessage().replace("\n",  " ").replace("\r", " "));
			out.println(" - id: " + post.getId());
			out.println(" - created: " + post.getCreatedTime());
			if (post.getPlace() != null)
				out.println(" - location: " + post.getPlace().getLocationAsString());
			if (post.getLink() != null)
				out.println(" - link: " + post.getLink());
			if (post.getDescription() != null)
				out.println(" - description: " + post.getDescription());
			Likes likes = post.getLikes();
			if (likes != null && likes.getCount() != null && likes.getCount() > 0)
				System.out.println(" - likes: " + post.getLikesCount());
			Comments comments = post.getComments();
			if (comments != null && comments.getCount() != null && comments.getCount() > 0)
				System.out.println(" - comments: " + comments.getCount());
		}
	}
}
