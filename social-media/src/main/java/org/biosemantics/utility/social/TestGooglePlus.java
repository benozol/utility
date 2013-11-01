package org.biosemantics.utility.social;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.biosemantics.utility.social.Post.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.Plus.Activities.Search;
import com.google.api.services.plus.PlusRequestInitializer;
import com.google.api.services.plus.model.Activity;
import com.google.api.services.plus.model.Activity.PlusObject;
import com.google.api.services.plus.model.Activity.PlusObject.Attachments;
import com.google.api.services.plus.model.ActivityFeed;
import com.google.api.services.plus.model.Place.Position;

public class TestGooglePlus implements SocialSearch {

	private static final String APPLICATION_NAME = "My App";
	public static final Network NETWORK = Post.Network.GOOGLE_PLUS; 

//	public static void main(String[] args) {
//		List<String> keywords = SearchConfig.getInstance().getKeywords();
//		try {
//			FileOutputStream out = new FileOutputStream(new File("/tmp/google-plus.out"));
//			new TestGooglePlus().search(keywords, new PostStore(out), null);
//			out.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		out.println("ok");
//	}

	@Override
	public Network getNetwork() {
		return NETWORK;
	}
	
	public SocialStream getSocialStream() {
		return null;
	}

	public void search(List<String> keywords, PostStore postStore, Date since) {
	    final HttpTransport httpTransport = new ApacheHttpTransport();
	    final JsonFactory jsonFactory = new JacksonFactory();
		final PlusRequestInitializer initializer = new PlusRequestInitializer(Accounts.GOOGLEPLUS_API_KEY.getValue());
		final Plus plus =
			new Plus.Builder(httpTransport, jsonFactory, null)
			.setApplicationName(APPLICATION_NAME)
	        .setGoogleClientRequestInitializer(initializer)
	        .build();
		final String query = StringUtils.join(keywords, " ");
		try {
			Search search = plus.activities().search(query);
			search.setOrderBy("recent");
			ActivityFeed feed = search.execute();;
			List<Activity> activities = feed.getItems();
			while (activities != null && activities.size() > 0) {
				logger.info("There are {} activities", activities.size());
				for (Activity activity : activities) {
					Post post = post(keywords, activity);
					if (since != null && post.getPublished().before(since))
						activities = null;
					else 
						postStore.store(post);
				}
				if (activities != null && feed.getNextPageToken() != null) {
					search.setPageToken(feed.getNextPageToken());
					feed = search.execute();
					activities = feed.getItems();
				} else
					activities = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Post post(List<String> keywords, Activity activity) {
		String id = activity.getId();
		String title = activity.getTitle();
		String url = activity.getUrl();
		String content = null;
		List<String> referredUrls = null;
		PlusObject object = activity.getObject();
		if (object != null) {
			content = object.getContent();
			if (object.getAttachments() != null) {
				referredUrls = new LinkedList<>();
				for (Attachments attachments : object.getAttachments())
					referredUrls.add(attachments.getUrl());
			}
		}
		Date published = new Date(activity.getPublished().getValue());
		String queryName = StringUtils.join(keywords, " ");
		String location = null;
		if (activity.getLocation() != null) {
			Position position = activity.getLocation().getPosition();
			location = String.format("%f/%f", position.getLongitude(), position.getLatitude());
		}
		Post post = new Post(queryName, id, NETWORK, title, url, location, content, published, referredUrls);
		return post;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(TestGooglePlus.class);

}

//if (object != null) {
//	printProp("content", object.getContent());
//	printProp("original content", object.getOriginalContent());
//	printProp("URL", object.getUrl());
//	Plusoners plusoners = object.getPlusoners();
//	if (plusoners != null)
//		printProp("plusoners", plusoners.getTotalItems());
//	Replies replies = object.getReplies();
//	if (replies != null)
//		printProp("replies", replies.getTotalItems());
//	Resharers resharers = object.getResharers();
//	if (resharers != null)
//		printProp("reshares", resharers.getTotalItems());
//	List<Attachments> listAttachments = object.getAttachments();
//	if (listAttachments != null)
//		for (Attachments attachments : listAttachments)
//			printProp("attachment url", attachments.getUrl()); 
//}
//printProp("crosspost source", activity.getCrosspostSource());
//printProp("published", activity.getPublished());
//printProp("url", activity.getUrl());
//printProp("place", activity.getPlaceName());
//printProp("activity", activity.getGeocode());http://www.fiercevaccines.com/story/india-gives-114-kids-wrong-vaccine-polio-mix/2013-09-19
//printProp("address", activity.getAddress());
//printProp("annotation", activity.getAnnotation());
//printProp("geocode", activity.getGeocode());