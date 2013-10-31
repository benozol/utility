package org.biosemantics.utility.social;

import java.io.IOException;
import java.util.List;

public interface SocialStream {
	void stream(List<String> keywords, final PostStore postStore) throws IOException;
}
