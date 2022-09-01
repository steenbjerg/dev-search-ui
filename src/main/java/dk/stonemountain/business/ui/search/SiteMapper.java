package dk.stonemountain.business.ui.search;

import dk.stonemountain.business.ui.search.backend.SiteDTO;

public class SiteMapper {
    private SiteMapper() {}
    
    public static Site map(SiteDTO s) {
        Site site = new Site();
        
        site.name.set(s.name);
        site.displayName.set(s.displayName);
        site.mainUrl.set(s.mainUrl);
        site.startUrl.set(s.startUrl);
        site.inclusionUrl.set(s.inclusionUrl);
        site.lastSuccessfulCrawl.set(s.lastSuccessfulCrawl);
        site.lastSuccessfulCrawlDuration.set(s.lastSuccessfulCrawlDuration);
        site.icon.set(s.icon);

        return site;
    }
}
