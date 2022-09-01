package dk.stonemountain.business.ui;

import java.net.http.HttpClient;
import java.time.Duration;
import java.net.http.HttpClient.Version;

public enum Backend {
	LOCAL("Developer Workstation", "http://localhost:8080", "https://installables.stonemountain.dk/services/installation-packages", true),
	TEST("Test", "https://stonemountain.dk/test", "https://installables.stonemountain.dk/services/installation-packages", true),
	PRODUCTION("Production", "https://information.stonemountain.dk", "https://installables.stonemountain.dk/services/installation-packages", true);
	
	private final String name;
	private final String bffServiceUrl;
	private final String installationPackagesUrl;
	private final boolean enabled;
	
	private Backend(String name, String url, String installationPackagesUrl, boolean enabled) {
		this.name = name;
		this.bffServiceUrl = url;
		this.installationPackagesUrl = installationPackagesUrl;
		this.enabled = enabled;
	}

	public String getName() {
		return name;
	}

	public String getBffServiceUrl() {
		return bffServiceUrl;
	}
	
	public String getInstallationPackagesUrl() {
		return installationPackagesUrl;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public HttpClient.Builder getHttpClientBuilder() {
        return HttpClient.newBuilder()
       		.connectTimeout(Duration.ofSeconds(5))
       		.version(Version.HTTP_1_1);
	}
}
