package dk.stonemountain.business.ui.search;

import java.time.ZonedDateTime;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Site {
    public StringProperty name = new SimpleStringProperty();
    public StringProperty mainUrl = new SimpleStringProperty();
    public StringProperty startUrl = new SimpleStringProperty();
    public StringProperty inclusionUrl = new SimpleStringProperty();
    public StringProperty displayName = new SimpleStringProperty();
    public ObjectProperty<ZonedDateTime> lastSuccessfulCrawl = new SimpleObjectProperty<>();
    public StringProperty lastSuccessfulCrawlDuration = new SimpleStringProperty();
    public StringProperty icon = new SimpleStringProperty();

    @Override
    public String toString() {
        return "Site [displayName=" + displayName + ", icon=" + icon + ", inclusionUrl=" + inclusionUrl
                + ", lastSuccessfulCrawl=" + lastSuccessfulCrawl + ", lastSuccessfulCrawlDuration="
                + lastSuccessfulCrawlDuration + ", mainUrl=" + mainUrl + ", name=" + name + ", startUrl=" + startUrl
                + "]";
    }
}
