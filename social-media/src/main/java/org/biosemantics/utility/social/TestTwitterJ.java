package org.biosemantics.utility.social;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.biosemantics.utility.social.Post.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.FilterQuery;
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

public class TestTwitterJ implements SocialSearch {

	protected static final Network NETWORK = Post.Network.TWITTER;
	protected static final String TWITTER_COM = "http://twitter.com";

//	public static void main(String[] args) {
//		List<String> keywords = SearchConfig.getInstance().getKeywords();
//		try {
//			FileOutputStream out = new FileOutputStream(new File("/tmp/twitter.out"));
//			new TestTwitterJ().search(keywords, new PostStore(out), null);
//			new TestTwitterJ().getSocialStream().stream(keywords, new PostStore(out));
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			out.close();
//		}
//		out.println("ok");
//	}

	public SocialStream getSocialStream() {
		return new SocialStream() {
			public void stream(final List<String> keywords, final PostStore postStore) {

				TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
				twitterStream.setOAuthConsumer(Accounts.TWITTER_CONSUMER_KEY.getValue(),
						Accounts.TWITTER_CONSUMER_SECRET.getValue());
				twitterStream.setOAuthAccessToken(loadAccessToken());

				StatusListener listener = new StatusListener() {
					@Override
					public void onStatus(Status status) {

						try {
							postStore.store(post(keywords, status));
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

				twitterStream.addListener(listener);
				FilterQuery filterQuery = new FilterQuery(0, null, new String[] { query(keywords) });
				twitterStream.filter(filterQuery);
			}
		};
	}

	private String query(List<String> keywords) {
		return StringUtils.join(keywords, " ");
	}

	private Post post(List<String> keywords, Status status) {
		String id = Long.toString(status.getId());
		String url = String.format("%s/%s/status/%s", TWITTER_COM, status.getUser().getName(), status.getId());
		String content = status.getText();
		Date published = status.getCreatedAt();
		List<String> referredUrls = null;
		if (status.getURLEntities() != null) {
			referredUrls = new LinkedList<>();
			for (URLEntity referredUrl : status.getURLEntities()) {
				referredUrls.add(referredUrl.getExpandedURL() != null ? referredUrl.getExpandedURL() : referredUrl.getDisplayURL());
			}
		}
		return new Post(keywords, id, NETWORK, null, url, content, published, referredUrls);
	}


	public void search(List<String> keywords, PostStore postStore, Date since) throws IOException {
		TwitterFactory factory = new TwitterFactory();
		AccessToken accessToken = loadAccessToken();
		Twitter twitter = factory.getInstance();
		twitter.setOAuthConsumer(Accounts.TWITTER_CONSUMER_KEY.getValue(), Accounts.TWITTER_CONSUMER_SECRET.getValue());
		twitter.setOAuthAccessToken(accessToken);
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
				for (Status status : tweets)
					postStore.store(post(keywords, status));
				query = search.nextQuery();
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	private static AccessToken loadAccessToken() {
		String token = Accounts.TWITTER_TOKEN_KEY.getValue();
		String tokenSecret = Accounts.TWITTER_TOKEN_SECRET.getValue();
		return new AccessToken(token, tokenSecret);
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