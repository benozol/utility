package org.biosemantics.utility.social;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.biosemantics.utility.social.Post.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.FilterQuery;
import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.URLEntity;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthSupport;

public class TestTwitterJ implements SocialSearch {
	
	static interface TwitterSocialStream extends SocialStream {
		void stream(List<String> keywords, double[][] locations, PostStore postStore);
		void stream(String filterName, FilterQuery filterQuery, PostStore postStore);
	}

	protected static final Network NETWORK = Post.Network.TWITTER;
	protected static final String TWITTER_COM = "http://twitter.com";

	public static class ByKeywords {
		public static void main(String[] args) {
			try {
				List<String> keywords;
				String filename;
				TestTwitterJ testTwitterJ;
				if (args.length == 2) {
					filename = args[0];
					keywords = Arrays.asList(StringUtils.split(args[1], " "));
					testTwitterJ = new TestTwitterJ();
				} else
					throw new SecurityException("Arguments: <filename> <keywords>");
				Writer writer = new FileWriter(filename);
				testTwitterJ.getTwitterSocialStream().stream(keywords, new PostStore(writer));
			} catch (IOException e) {
			}
		}
	}

	public static class ByLocations {
		public static void main(String[] args) {
			try {
				String filename;
				TestTwitterJ testTwitterJ;
				double[][] locations;
				if (args.length == 2) {
					filename = args[0];
					String[] locationStrings = StringUtils.split(args[1], ",");
					double minLongitude = Double.parseDouble(locationStrings[0]);
					double minLatitude = Double.parseDouble(locationStrings[1]);;
					double maxLongitude = Double.parseDouble(locationStrings[2]);
					double maxLatitude = Double.parseDouble(locationStrings[3]);;
					locations = new double[][] {
							{ minLongitude , minLatitude } , { maxLongitude , maxLatitude }
					};
					testTwitterJ = new TestTwitterJ();
				} else {
					System.err.println("Arguments: <filename> <keywords>");
					throw new SecurityException();
				}
				Writer writer = new FileWriter(filename);
				String locationString = String.format("%f,%f,%f,%f", locations[0][0], locations[0][1], locations[1][0], locations[1][1]);
				System.out.println(String.format("Streaming within coordinates %s", locationString));
				FilterQuery filterQuery = new FilterQuery();
				filterQuery.locations(locations);
				testTwitterJ.stream(locationString, filterQuery, new PostStore(writer));
			} catch (IOException e) {
			}
		}
	}


	private String consumerKey;
	private String consumerSecret;
	private String tokenKey;
	private String tokenSecret;

	public TestTwitterJ() {
		this.consumerKey = Accounts.TWITTER_CONSUMER_KEY.getValue();
		this.consumerSecret = Accounts.TWITTER_CONSUMER_SECRET.getValue();
		this.tokenKey = Accounts.TWITTER_TOKEN_KEY.getValue();
		this.tokenSecret = Accounts.TWITTER_TOKEN_SECRET.getValue();
	}

	
	public TestTwitterJ(String consumerKey, String consumerSecret, String tokenKey, String tokenSecret) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.tokenKey = tokenKey;
		this.tokenSecret = tokenSecret;
	}
	
	private void initOAuth(OAuthSupport oauth) {
		System.out.println("INIT OAUTH");
		oauth.setOAuthConsumer(consumerKey, consumerSecret);
		AccessToken accessToken = new AccessToken(tokenKey, tokenSecret);
		oauth.setOAuthAccessToken(accessToken);
	}
	
	public SocialStream getSocialStream() {
		return getTwitterSocialStream();
	}
	
	public void stream(final String filterName, final FilterQuery filterQuery, final PostStore postStore) {
		StatusListener listener = new StatusListener() {
			@Override
			public void onStatus(Status status) {

				try {
					postStore.store(post(filterName, status));
				} catch (IOException e) {
					logger.error("Couldn't store post {}", status.getId());
					e.printStackTrace();
				}
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				logger.error("Got a status deletion notice id: {}", statusDeletionNotice.getStatusId());
			}

			@Override
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				logger.error("Got track limitation notice: {}", numberOfLimitedStatuses);
			}

			@Override
			public void onScrubGeo(long userId, long upToStatusId) {
				logger.error("Got scrub_geo event userId:{} upToStatusId:{}", new Object[] { userId, upToStatusId });
			}

			@Override
			public void onStallWarning(StallWarning warning) {
				logger.error("Got stall warning: {}", warning);
			}

			@Override
			public void onException(Exception ex) {
				logger.error("", ex);
			}
		};

		TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
		initOAuth(twitterStream);
		twitterStream.addListener(listener);
		twitterStream.filter(filterQuery);
	}

	public SocialStream getTwitterSocialStream() {
		return new SocialStream() {
//
//			@Override
//			public void stream(List<String> keywords, double[][] locations, PostStore postStore) {
//				FilterQuery filterQuery = new FilterQuery(0, null, new String[] { query(keywords) }, locations);
//				String filterName = StringUtils.join(keywords, " ") + " @ " + StringUtils.join(locations, ",");
//				stream(filterName, filterQuery, postStore);
//			}
			
			public void stream(List<String> keywords, PostStore postStore) {
				FilterQuery filterQuery = new FilterQuery(0, null, new String[] { query(keywords) });
				String filterName = StringUtils.join(keywords, " ");
				TestTwitterJ.this.stream(filterName, filterQuery, postStore);
			}
		};
	}

	private String query(List<String> keywords) {
		return StringUtils.join(keywords, " ");
	}

	private Post post(String filterName, Status status) {
		String id = Long.toString(status.getId());
		String url = String.format("%s/%s/status/%s", TWITTER_COM, status.getUser().getName(), status.getId());
		String content = status.getText();
		Date published = status.getCreatedAt();
		List<String> referredUrls = null;
		String location = status.getGeoLocation().toString();
		if (status.getURLEntities() != null) {
			referredUrls = new LinkedList<>();
			for (URLEntity referredUrl : status.getURLEntities()) {
				referredUrls.add(referredUrl.getExpandedURL() != null ? referredUrl.getExpandedURL() : referredUrl.getDisplayURL());
			}
		}
		return new Post(filterName, id, NETWORK, null, url, location, content, published, referredUrls);
	}


	public void search(List<String> keywords, PostStore postStore, Date since) throws IOException {
		Twitter twitter = new TwitterFactory().getInstance();
		initOAuth(twitter);
		Query query = new Query(query(keywords));
		if (since != null)
			query.setSince(new SimpleDateFormat("YYYY-MM-dd").format(since));
		query.setLang("en");
		query.setCount(100);
		try {
			while (query != null) {
				QueryResult search = twitter.search(query);
				List<Status> tweets = search.getTweets();
				logger.info("There are {} tweets", tweets.size());
				String filterName = StringUtils.join(keywords, " ");
				for (Status status : tweets)
					postStore.store(post(filterName, status));
				query = search.nextQuery();
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(TestTwitterJ.class);

	@Override
	public Network getNetwork() {
		return NETWORK;
	}
}


//List<String> output = new ArrayList<String>();
//try {
//
//	output.add("" + status.isRetweet());
//	output.add("" + status.getRetweetCount());
//	output.add("" + status.isTruncated());
//	output.add("" + status.isFavorited());
//	if (status.getCreatedAt() != null) {
//		output.add(status.getCreatedAt().toGMTString());
//	} else {
//		output.add("NA");
//	}
//	if (StringUtils.isBlank(status.getInReplyToScreenName())) {
//		output.add("NA");
//	} else {
//		output.add(status.getInReplyToScreenName());
//	}
//	if (status.getGeoLocation() != null) {
//		output.add("" + status.getGeoLocation().getLatitude());
//		output.add("" + status.getGeoLocation().getLongitude());
//	} else {
//		output.add("NA");
//		output.add("NA");
//	}
//	output.add("" + status.getText());
//	if (status.getHashtagEntities() != null) {
//		for (HashtagEntity hashtagEntity : status.getHashtagEntities()) {
//			output.add(hashtagEntity.getText());
//		}
//	}
//	csvWriter.writeNext(output.toArray(new String[output.size()]));
//	csvWriter.flush();
//} catch (Exception e) {
//	logger.error("exception parsing status: ", e);
//}
//System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());