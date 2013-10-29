package org.biosemantics.utility.social;

import java.io.File;
import java.net.URL;

public class Utils {

	
	public static File getResourceFile(Class<?> cls, String name) {
		URL resource = cls.getResource(name);
		if (resource == null)
			throw new RuntimeException("No such resource: " + name);
		return new File(resource.getFile());
	}
}
