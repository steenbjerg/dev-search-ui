package dk.stonemountain.business.ui.search.backend;

import java.util.List;

import jakarta.json.bind.annotation.JsonbProperty;

public record SearchResultDTO(
    Long id,
    @JsonbProperty("site-id")
    Long siteId,
    String url,
    String title,
    List<String> highlights) {
 }