package org.biosemantics.utility.social;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSocialSearch {

	private SocialSearch[] socialSearches = new SocialSearch[] {
		new TestGooglePlus(),
		new TestRestFb(),
		new TestTwitterJ()
	};

	static Properties outputProperties = new Properties();
	static {
		try {
			InputStream stream = TestTwitterJ.class.getResourceAsStream("/output.properties");
			outputProperties.load(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private final class RunnableStream implements Runnable {
		private final List<String> keywords;
		private final PostStore postStore;
		private final SocialStream socialStream;

		RunnableStream(List<String> keywords, PostStore postStore,
				SocialStream socialStream) {
			this.keywords = keywords;
			this.postStore = postStore;
			this.socialStream = socialStream;
		}

		@Override
		public void run() {
			try {
				socialStream.stream(keywords, postStore);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void search(String pathname) throws IOException {
		Writer writer = new FileWriter(new File(pathname));
		try {
			PostStore postStore = new PostStore(writer);
			for (SocialSearch socialSearch : socialSearches) {
				List<List<String>> keywordLists = SearchConfig.getInstance().getKeywordLists();
				for (List<String> keywords : keywordLists) {
					logger.info("Search {} to {}", socialSearch.getNetwork() + " on " + StringUtils.join(keywords, ", "), pathname);
					socialSearch.search(keywords, postStore, null);
				}
			}
		}
		finally {
			writer.close();
			logger.info("END");
		}
	}

	@SuppressWarnings("unused")
	private void stream(String pathname) throws IOException {
		Writer writer = new FileWriter(new File(pathname));
		try {
			PostStore postStore = new PostStore(writer);
			for (SocialSearch socialSearch : socialSearches) {
				List<List<String>> keywordLists = SearchConfig.getInstance().getKeywordLists();
				for (List<String> keywords : keywordLists) {
					SocialStream socialStream = socialSearch.getSocialStream();
					if (socialStream != null) {
						logger.info("Stream {} to {}", socialSearch.getNetwork() + " on " + StringUtils.join(keywords, ", "), pathname);
						new Thread(new RunnableStream(keywords, postStore, socialStream)).start();
					}
				}
			};
		} finally {
			logger.info("WAITING");
		}
	}
	
	private static final Logger logger = LoggerFactory.getLogger(TestSocialSearch.class);
	
	public static void main(String[] args) {
		try {
			new TestSocialSearch().search(outputProperties.getProperty("social_search"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
