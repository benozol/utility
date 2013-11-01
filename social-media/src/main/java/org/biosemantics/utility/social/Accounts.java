package org.biosemantics.utility.social;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public enum Accounts {

	
	GOOGLEPLUS_API_KEY("googleplus_api_key"),
	
	FACEBOOK_ACCESS_TOKEN("facebook_token"),
	FACEBOOK_TOKEN(""),
	FACEBOOK_TOKEN_SECRET(""),
	
	TWITTER_CONSUMER_KEY("twitter_consumer_key"),
	TWITTER_CONSUMER_SECRET("twitter_consumer_secret"),
	TWITTER_TOKEN_KEY("twitter_token_key"),
	TWITTER_TOKEN_SECRET("twitter_token_secret"),
	TWITTER_APPLICATION_ID("twitter_application_id");
	
	private static final String ACCESS_PROPERTIES_ENVVAR = "ACCESS_PROPERTIES";

	private static final String ACCESS_PROPERTIES = "/access.properties";

	private String key;
	private static Properties prop = new Properties();
	static {
		try {
			
			String accessPropertiesEnvVar = System.getenv(ACCESS_PROPERTIES_ENVVAR);
			if (accessPropertiesEnvVar != null) {
				System.out.println(String.format("Using %s as access.properties", accessPropertiesEnvVar));
				InputStream stream = new FileInputStream(new File(accessPropertiesEnvVar));
				prop.load(stream);
				stream.close();
			} else
				prop.load(Accounts.class.getResourceAsStream(ACCESS_PROPERTIES));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Accounts(String key) {
		this.key = key;
	}
	public String getValue() {
		return prop.getProperty(key);
	}

}
