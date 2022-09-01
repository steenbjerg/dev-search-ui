package dk.stonemountain.business.ui.installer;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.stonemountain.business.ui.Version;
import dk.stonemountain.business.ui.installer.backend.VersionInformation;
import dk.stonemountain.business.ui.util.jaxrs.JsonbHelper;

public class Downloader {
    private final static Logger log = LoggerFactory.getLogger(Downloader.class);
    
    private final String backendUrl;
    
    public Downloader(String url) {
        backendUrl = url;
    }

    public Optional<VersionInformation> checkInstalledVersion() {
        Optional<VersionInformation> result = Optional.empty();

        String url = backendUrl + "/" + Version.APP_PACKAGE + "/" + Version.APP_OS.replace("##OS##", "Linux") + "/" + Version.APP_GIT_SHA.replace("##SHA##", "unknown") + "/version-information";
		log.info("checking for new version from url {}", url);
		
		try {
			HttpClient client = HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(15))
				.build();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(url))
					.timeout(Duration.ofSeconds(30))
					.GET()
					.build();

			log.trace("Connecting to {}", url);
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			var statusCode = response.statusCode();
			log.trace("latest version check gave status code {}", statusCode);
			if (statusCode == 200) {
				var body = response.body();
				result = Optional.of(JsonbHelper.fromJson(body, VersionInformation.class));
				log.trace("Downloader has fetched version nummer: {}", result);
			} else {
				log.error("Failed to get version from url {}. Status code {}", url, statusCode);
			}
		} catch (Exception e) {
			log.error("Version check failed from url {}: {}", url, e.getMessage());
		}
        return result;
	}

	public Optional<InputStream> getNewVersion(String sha) {
        Optional<InputStream> result = Optional.empty();
        String url = backendUrl + "/" + sha + "/file";

        log.trace("Downloading version with sha {} form url {}", sha, url);

		HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/octet-stream")
                .GET()
                .build();

		try {
			HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
			var statusCode = response.statusCode();
			if (statusCode == 200) {
				result = Optional.of(response.body());
			} else {
				log.error("Failed to get new version from url {}. Status code {}", url, statusCode);
			}
		} catch (Exception e) {
            log.error("Failed to get new version from url {}.", url, e);
		}
        return result;
	}
}
