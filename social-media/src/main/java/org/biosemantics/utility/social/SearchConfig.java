package org.biosemantics.utility.social;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class SearchConfig {
	
	private static final String KEYWORDS = "/keywords";
	private static SearchConfig instance = null;
	
	public static SearchConfig getInstance() {
		if (instance == null) {
			instance = new SearchConfig();
		}
		return instance;
	}
	
	private List<List<String>> keywordLists;
	
	private SearchConfig() {
		BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(KEYWORDS)));
		keywordLists = new LinkedList<>();
		try {
			for (String line = in.readLine(); line != null; line = in.readLine()) {
				if (line.length() > 0 && line.charAt(0) != '#') {
					List<String> keywords = Arrays.asList(StringUtils.split(line, " "));
					keywordLists.add(keywords);
				}
			}
		} catch (IOException e) {}
	}

	public List<List<String>> getKeywordLists() {
		return keywordLists;
	}

}
