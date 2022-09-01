package dk.stonemountain.business.ui.events.dto;

import jakarta.json.bind.annotation.JsonbCreator;

public record SearchResultDTO(String title, String text, String url) {
    @JsonbCreator
    public static SearchResultDTO create(String title, String text, String url) {
        return new SearchResultDTO(title, text, url);
    }
}