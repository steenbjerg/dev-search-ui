package dk.stonemountain.business.ui;

public class Version {
	private Version() {
		// prevent instantiation
	}
	
	public static final String APP_VERSION = "##VERSION##";
	public static final String APP_BUILD_TIME = "##BUILD_TIME##";
	public static final String APP_GIT_SHA = "##SHA##";
	public static final String APP_PACKAGE = "doc-search-ui";
	public static final String APP_OS = "##OS##";
}

