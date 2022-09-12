package dk.stonemountain.business.ui.search.backend;

import java.time.ZonedDateTime;
import java.util.List;

import jakarta.json.bind.annotation.JsonbProperty;

public record SitesDTO (
    @JsonbProperty("no-of-sites")
    long noOfSites,
    @JsonbProperty("retrieval-time")
    ZonedDateTime retrievalTime,
    @JsonbProperty("sites")
    List<SiteDTO> sites) {
}