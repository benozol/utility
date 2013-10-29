package org.biosemantics.utility.social;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	
	private List<String> keywords;
	
	private SearchConfig() {
		File keywordsFile = Utils.getResourceFile(this.getClass(), "/keywords");
		try {
			Path path = Paths.get(keywordsFile.getPath());
			List<String> lines = Files.readAllLines(path, Charset.defaultCharset());
			keywords = lines;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Couldn't read keywords file", e);
		}
		System.out.println("Keywords: " + StringUtils.join(keywords, ", "));
	}

	public List<String> getKeywords() {
		return keywords;
	}

}
