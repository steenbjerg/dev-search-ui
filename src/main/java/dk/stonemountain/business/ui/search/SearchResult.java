package dk.stonemountain.business.ui.search;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SearchResult {
    public StringProperty title = new SimpleStringProperty();
    public StringProperty url = new SimpleStringProperty();
    public StringProperty text = new SimpleStringProperty();
    public StringProperty siteId = new SimpleStringProperty();
    public StringProperty id = new SimpleStringProperty();


    @Override
    public String toString() {
        return "SearchResult [id=" + id + ", siteId=" + siteId + ", text=" + text + ", title=" + title + ", url=" + url + "]";
    }
    
}
