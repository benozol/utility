package org.biosemantics.utility.social;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.biosemantics.utility.social.Post.Network;
import org.biosemantics.utility.social.Post.UrlWithTitle;

public class Post {
	public static class UrlWithTitle {
		private String url;
		private String title;
		public UrlWithTitle(String url) {
			this.url = url;
		}
		public UrlWithTitle(String url, String title) {
			this.url = url;
			this.title = title;
		}
		public String getUrl() {
			return url;
		}
		public String getTitle() {
			return title;
		}
	}
	public static enum Network {
		FACEBOOK, TWITTER, GOOGLE_PLUS;
		@Override
		public String toString() {
			switch (this) {
			case FACEBOOK: return "facebook";
			case TWITTER: return "twitter";
			case GOOGLE_PLUS: return "googleplus";
			}
			throw new Error("Post.Network.toString");
		}
	}
	private List<String> keywords;
	private String id;
	private Network network;
	private String title;
	private String url;
	private String content;
	private Date published;
	private List<String> referredUrls;
	public Post(List<String> keywords, String id, Network network, String title, String url,
			String content, Date published, List<String> referredUrls) {
		this.keywords = keywords;
		this.id = id;
		this.network = network;
		this.title = title;
		this.url = url;
		this.content = content;
		this.published = published;
		this.referredUrls = referredUrls; 
	}
	public List<String> getKeywords() {
		return keywords;
	}
	public String getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}
	public String getUrl() {
		return url;
	}
	public String getContent() {
		return content;
	}
	public List<String> getReferredUrls() {
		return referredUrls;
	}
	public Network getNetwork() {
		return network;
	}
	public Date getPublished() {
		return published;
	}
}
