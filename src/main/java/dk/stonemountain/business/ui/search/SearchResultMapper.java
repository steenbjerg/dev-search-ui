package dk.stonemountain.business.ui.search;

import java.util.stream.Collectors;

import dk.stonemountain.business.ui.search.backend.SearchResultDTO;

public class SearchResultMapper {

    private SearchResultMapper() {}

    public static SearchResult map(SearchResultDTO s) {
        SearchResult sr = new SearchResult();
        
        sr.url.set(s.url());
        sr.siteId.set(s.siteId());
        sr.id.set(s.id());
        sr.title.set(s.title());

        String text = s.highlights().stream()
            .flatMap(h -> h.highlights().stream())
            .map(t -> "<p>" + t.trim() + "</p>")
            .collect(Collectors.joining("\n"));
        sr.text.set(text);

        return sr;
    }

}
