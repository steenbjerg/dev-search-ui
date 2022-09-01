package dk.stonemountain.business.ui.search.backend;

import java.util.List;

public record FieldHitDTO(
    String fieldName,
    List<String> highlights) {
}
