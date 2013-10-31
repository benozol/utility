package org.biosemantics.utility.social;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface SocialSearch {

	void search(List<String> keywords, PostStore postStore, Date since) throws IOException;
	SocialStream getSocialStream();
	Post.Network getNetwork();

}