package dk.stonemountain.business.ui.util.gui;

import dk.stonemountain.business.ui.Version;

public class ClientRuntime {
	private ClientRuntime() {
		// preventing instantiation
	}
	
	public static String getApplicationVersion() {	
		return Version.APP_VERSION;
	}
	
	public static String getApplicationGitSha() {	
		return Version.APP_GIT_SHA;
	}
	
	public static String getApplicationBuildTime() {	
		return Version.APP_BUILD_TIME;
	}
	
	public static String getApplicationLogFolder() {
		String tempDir = System.getProperty("java.io.tmpdir");
		if (tempDir == null) {
			return "Not found";
		} else {
			tempDir = tempDir.trim().replace('\\', '/');
			if (!tempDir.endsWith("/")) {
				tempDir = tempDir + "/";
			}
			return tempDir + "logs/docsearch";
		}
	}

	public static String getApplicationLog() {
		return getApplicationLogFolder() + "/application.log";
	}
}
