package dk.stonemountain.business.ui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserPreferences {
	private static final Logger logger = LoggerFactory.getLogger(UserPreferences.class);
	private static final String FILE_LOCATION_PREF = "fileLocation";

	public Optional<Path> getFileLocation() {
		Preferences prefs = Preferences.userNodeForPackage(DocSearchApplication.class);
		String filePath = prefs.get(FILE_LOCATION_PREF, null);

	    if (filePath != null) {
	    	Path path = Paths.get(filePath);
	    	Path dirPath = Files.isDirectory(path) ? path : path.getParent();
	    	logger.debug("Looking up path: {}", dirPath);
	        return Optional.of(dirPath);
	    } else {
	        return Optional.empty();
	    }
	}
	
	public void putFileLocation(Path path) {
		Preferences prefs = Preferences.userNodeForPackage(DocSearchApplication.class);
	    if (path != null) {
	    	Path dirPath = Files.isDirectory(path) ? path : path.getParent();
	    	String filePath = dirPath.toString();
	        prefs.put(FILE_LOCATION_PREF, filePath);
	    	logger.debug("Storing path: {}", filePath);
	    } else {
	        prefs.remove(FILE_LOCATION_PREF);
	    	logger.debug("Removing path");
	    }
	}

}
