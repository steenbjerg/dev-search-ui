package dk.stonemountain.business.ui.search.backend;

import java.util.List;

import jakarta.json.bind.annotation.JsonbProperty;

public record SearchResultDTO(
    String id,
    @JsonbProperty("site-id")
    String siteId,
    String url,
    String title,
    List<FieldHitDTO> highlights) {
}