package org.biosemantics.utility.social;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class SearchConfig {
	
	private static SearchConfig instance = null;
	
	public static SearchConfig getInstance() {
		if (instance == null) {
			instance = new SearchConfig();
		}
		return instance;
	}
	
	private List<List<String>> keywordLists;
	
	private SearchConfig() {
		File keywordsFile = Utils.getResourceFile(this.getClass(), "/keywords");
		try {
			Path path = Paths.get(keywordsFile.getPath());
			keywordLists = new LinkedList<>();
			for (String line : Files.readAllLines(path, Charset.defaultCharset())) {
				if (line.length() > 0 && line.charAt(0) != '#') {
					List<String> keywords = Arrays.asList(StringUtils.split(line, " "));
					keywordLists.add(keywords); 
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Couldn't read keywords file", e);
		}
	}

	public List<List<String>> getKeywordLists() {
		return keywordLists;
	}

}
