package dk.stonemountain.business.ui.installer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.stonemountain.business.ui.ApplicationContainer;
import dk.stonemountain.business.ui.Backend;
import dk.stonemountain.business.ui.Version;
import dk.stonemountain.business.ui.util.time.TimeConverter;
import javafx.application.Platform;

public class PackageInstaller {
	private static final Logger log = LoggerFactory.getLogger(PackageInstaller.class);
	private static final Duration DURATION_BETWEEN_CHECKS = Duration.ofSeconds(30);

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, r -> {
		Thread t = Executors.defaultThreadFactory().newThread(r);
		t.setDaemon(true);
		return t;
	});
	private LocalDateTime lastSuccessfullCheck = null;
	private Optional<String> versionDownloaded = Optional.empty();

	public void startInstaller() {
		scheduler.scheduleAtFixedRate(this::checkInstalledVersion, 10, 60, TimeUnit.SECONDS);
		log.info("Installer has started");
	}

	public void checkInstalledVersion() {
		if (lastSuccessfullCheck != null && Duration.between(lastSuccessfullCheck, LocalDateTime.now())
				.compareTo(DURATION_BETWEEN_CHECKS) <= 0) {
			return;
		}
		;

		Backend backend = ApplicationContainer.getInstance().getCurrentBackend();
		Downloader downloader = new Downloader(backend.getInstallationPackagesUrl());

		downloader.checkInstalledVersion().ifPresent(info -> {
			log.trace("New version info fetched:{}", info);
			VersionInformation swInfo = map(info);
			if (!Version.APP_VERSION.equalsIgnoreCase(info.recommendedVersion()) && info.mustBeUpdated() 
				&& (versionDownloaded.isEmpty() || !versionDownloaded.get().equalsIgnoreCase(info.recommendedVersion()))) {

				downloader.getNewVersion(info.recommendedSha()).ifPresent(sw -> {
					log.trace("New software ready for download");
					try (InputStream is = sw) {
						Path file = Files.createTempFile("docsearch-", "");
						Files.copy(is, file, StandardCopyOption.REPLACE_EXISTING);
						log.debug("File {} ready for installation", file);
						versionDownloaded = Optional.of(info.recommendedVersion());
						log.trace("New software downloaded: {}", file);
						Platform.runLater(() -> ApplicationContainer.getInstance().updatedVersionReady(swInfo, file));
					} catch (IOException e) {
						log.error("Failed to retrieve new sw version", e);
					}
				});
			}
		});
	}

	private VersionInformation map(dk.stonemountain.business.ui.installer.backend.VersionInformation info) {
		VersionInformation v = new VersionInformation();
		v.setMustBeUpdated(!info.currentIsWorking());
		v.setNewerVersionAvailable(info.mustBeUpdated());
		v.setNewSha(info.recommendedSha());
		v.setNewVersion(info.recommendedVersion());
		v.setNewReleaseNote(info.recommendedReleaseNote());
		v.setNewReleaseTime(TimeConverter.toLocalDateTime(info.recommendedReleaseTime()));
		return v;
	}

	public InputStream getNewVersion(String sha) {
		Backend backend = ApplicationContainer.getInstance().getCurrentBackend();

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(backend.getBffServiceUrl()))
				.GET()
				.build();

		try {
			HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
			var statusCode = response.statusCode();
			if (statusCode == 200) {
				return response.body();
			} else {
				throw new RuntimeException("Failed to get version. Status code " + statusCode);
			}
		} catch (Exception e) {
			throw new RuntimeException("Communication failure", e);
		}
	}

	public void install(Path newCommand) {
		ProcessHandle.current().info().command().ifPresent(cmd -> {
			try {
				Path existingCmd = Paths.get(cmd);
				Path copiedCmd = Paths.get(cmd + "_old");
				log.info("Installing {} to {} via {}", newCommand, existingCmd, copiedCmd);

				Files.move(existingCmd, copiedCmd, StandardCopyOption.REPLACE_EXISTING);
				Files.move(newCommand, existingCmd, StandardCopyOption.REPLACE_EXISTING);
				existingCmd.toFile().setExecutable(true, true);
				copiedCmd.toFile().delete();
				newCommand.toFile().delete();

				new ProcessBuilder(existingCmd.toFile().getAbsolutePath()).start();
  				System.exit(0);
			} catch (Exception e) {
				log.error("Failed to install and restart process", e);
			}
		});
	}
}
