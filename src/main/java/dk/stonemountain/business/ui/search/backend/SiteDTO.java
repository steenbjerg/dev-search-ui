package dk.stonemountain.business.ui.search.backend;

import java.time.ZonedDateTime;

import jakarta.json.bind.annotation.JsonbProperty;

public record  SiteDTO(
    @JsonbProperty("start-url")
    String startUrl,
    @JsonbProperty("main-url")
    String mainUrl,
    @JsonbProperty("inclusion-url")
    String inclusionUrl,
    String name,
    @JsonbProperty("display-name")
    String displayName,
    @JsonbProperty("last-successful-crawl")
    ZonedDateTime lastSuccessfulCrawl,
    @JsonbProperty("last-successful-crawl-duration")
    String lastSuccessfulCrawlDuration,
    String icon) {
}
