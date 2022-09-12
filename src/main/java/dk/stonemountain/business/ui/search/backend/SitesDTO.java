package dk.stonemountain.business.ui.search.backend;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.json.bind.annotation.JsonbProperty;

public class SitesDTO {
    @JsonbProperty("no-of-sites")
    public long noOfSites;
    @JsonbProperty("retrieval-time")
    public ZonedDateTime retrievalTime;
    @JsonbProperty("sites")
    public List<SiteDTO> sites = new ArrayList<>();
}