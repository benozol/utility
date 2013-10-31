package org.biosemantics.utility.social;

import java.io.IOException;
import java.util.Properties;

public enum Accounts {

	
	GOOGLEPLUS_API_KEY("googleplus_api_key"),
	
	FACEBOOK_ACCESS_TOKEN("facebook_token"),
	FACEBOOK_TOKEN,
	FACEBOOK_TOKEN_SECRET,
	
	TWITTER_CONSUMER_KEY("twitter_consumer_key"),
	TWITTER_CONSUMER_SECRET("twitter_consumer_secret"),
	TWITTER_TOKEN_KEY("twitter_token_key"),
	TWITTER_TOKEN_SECRET("twitter_token_secret"),
	TWITTER_APPLICATION_ID("twitter_application_id");
	
	private static final String ACCESS_PROPERTIES = "/access.properties";

	private String value;
	private Properties prop = new Properties();
	{
		try {
			prop.load(getClass().getResourceAsStream(ACCESS_PROPERTIES));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Accounts(String key) {
		this.value = prop.getProperty(key);
	}
	private Accounts() {
		this.value = null;
	}
	public String getValue() {
		return value;
	}

}
