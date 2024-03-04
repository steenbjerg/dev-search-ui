package dk.stonemountain.business.ui.search;

import java.util.stream.Collectors;

import dk.stonemountain.business.ui.search.backend.SearchResultDTO;

public class SearchResultMapper {
    private static final String PREFIX_HTLM =
    """
    html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>How to Search</title>
        <style type="text/css">

            ul {
                list-style-type: none
            }

            li {
                display: grid;
                grid-template-columns: 20px auto;
                justify-content: start;
                align-items: center;
            }

            li::before {
                content: "\00BB";
            }
        </style>
    </head>

    <body>
    """;

    private static final String SUFFIX_HTML = "</body></html>";

    private SearchResultMapper() {}

    public static SearchResult map(SearchResultDTO s) {
        SearchResult sr = new SearchResult();
        
        sr.url.set(s.url());
        sr.siteId.set(s.siteId().toString());
        sr.id.set(s.id().toString());
        sr.title.set(s.title());

          
        String text = s.highlights().stream()
            .map(t -> "<li>" + t.trim() + "</li>")
            .collect(Collectors.joining("\n"));

        text = "<ul>\n" + text + "\n</ul>"; 
        sr.text.set(text);

        return sr;
    }

}
