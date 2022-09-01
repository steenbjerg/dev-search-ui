package dk.stonemountain.business.ui.search.backend;

import java.time.ZonedDateTime;

import jakarta.json.bind.annotation.JsonbProperty;

public class SiteDTO {
    @JsonbProperty("start-url")
    public String startUrl;
    @JsonbProperty("main-url")
    public String mainUrl;
    @JsonbProperty("inclusion-url")
    public String inclusionUrl;
    public String name;
    @JsonbProperty("display-name")
    public String displayName;
    @JsonbProperty("last-successful-crawl")
    public ZonedDateTime lastSuccessfulCrawl;
    @JsonbProperty("last-successful-crawl-duration")
    public String lastSuccessfulCrawlDuration;
    public String icon;
}
