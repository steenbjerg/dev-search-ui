package dk.stonemountain.business.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	private static Logger log = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		log.info("Starting up: command = {}, command line = {}", ProcessHandle.current().info().command(), ProcessHandle.current().info().commandLine());
		DocSearchApplication.main(new String[0]);
	}
}