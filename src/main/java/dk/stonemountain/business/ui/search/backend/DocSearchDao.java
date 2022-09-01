package dk.stonemountain.business.ui.search.backend;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.stonemountain.business.ui.ApplicationContainer;
import dk.stonemountain.business.ui.util.jaxrs.JsonbHelper;

public class DocSearchDao {
    private static final Logger logger = LoggerFactory.getLogger(DocSearchDao.class);

    public CompletableFuture<List<SiteDTO>> getSites() {
		String url = ApplicationContainer.getInstance().getCurrentBackend().getBffServiceUrl();
		url = url + "/sites";
		logger.debug("Invoking url: {}", url);
		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create(url))
			.header("Accept", "application/json")
			.build();
		
		HttpClient client = ApplicationContainer.getInstance().getCurrentBackend().getHttpClientBuilder().build();
		
		return client.sendAsync(request, BodyHandlers.ofString())
			.thenApply(HttpResponse::body)
			.thenApply(b -> JsonbHelper.fromJson(b, new ArrayList<SiteDTO>(){}.getClass().getGenericSuperclass())); // NOSONAR
	}

    public CompletableFuture<List<SearchResultDTO>> search(String s, String query) {
		String url = ApplicationContainer.getInstance().getCurrentBackend().getBffServiceUrl();
		url = url + "/sites/{siteName}/pages/full-text-search?query={query}".replace("{siteName}", s).replace("{query}", URLEncoder.encode(query, StandardCharsets.UTF_8));
		logger.debug("Invoking url: {}", url);
		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create(url))
			.header("Accept", "application/json")
			.build();
		
		HttpClient client = ApplicationContainer.getInstance().getCurrentBackend().getHttpClientBuilder().build();
		
		return client.sendAsync(request, BodyHandlers.ofString())
			.thenApply(HttpResponse::body)
			.thenApply(b -> JsonbHelper.fromJson(b, new ArrayList<SearchResultDTO>(){}.getClass().getGenericSuperclass())); // NOSONAR
	}
}
