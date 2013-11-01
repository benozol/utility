package org.biosemantics.utility.social;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import au.com.bytecode.opencsv.CSVWriter;

public class PostStore {
	
	private static final String REFERRED_URLS_COLUMN = "referred-urls";
	private static final String URL_COLUMN = "url";
	private static final String DATE_COLUMN = "date";
	private static final String CONTENT_COLUMN = "content";
	private static final String TITLE_COLUMN = "title";
	private static final String ID_COLUMN = "id";
	private static final String NETWORK_COLUMN = "network";
	private static final String QUERY_COLUMN = "query";
	private static final List<String> FIELDS = Arrays.asList(ID_COLUMN, NETWORK_COLUMN, QUERY_COLUMN, TITLE_COLUMN, CONTENT_COLUMN, DATE_COLUMN, URL_COLUMN, REFERRED_URLS_COLUMN);

	private CSVWriter csvWriter;

	public PostStore(Writer out) throws IOException {
		csvWriter = new CSVWriter(out);
		csvWriter.writeNext(FIELDS.toArray(new String[]{}));
		csvWriter.flush();
	}
	
	private Map<String, String> postToMap(Post post) {
		Map<String, String> map = new HashMap<>();
		map.put(ID_COLUMN,  post.getId());
		map.put(NETWORK_COLUMN,  post.getNetwork().toString());
		map.put(QUERY_COLUMN, post.getQueryName());
		map.put(TITLE_COLUMN, cleanup(StringUtils.defaultString(post.getTitle(), "")));
		map.put(CONTENT_COLUMN, cleanup(post.getContent()));
		map.put(DATE_COLUMN, DateFormat.getInstance().format(post.getPublished()));
		map.put(URL_COLUMN, post.getUrl());
		return map;
	}
	
	private String cleanup(String string) {
		return StringUtils.defaultString(string, "").replace("\n", " ");
	}
	
	public synchronized void store(Post post) throws IOException {
		List<String> row = new LinkedList<>();
		Map<String, String> map = postToMap(post);
		for (String key : FIELDS)
			if (key.equals(REFERRED_URLS_COLUMN)) {
				if (post.getReferredUrls() != null)
					for (String url : post.getReferredUrls())
						row.add(url);
			} else
				row.add(map.get(key));
		csvWriter.writeNext(row.toArray(new String[]{}));
		csvWriter.flush();
	}
}
