package dk.stonemountain.business.ui.search;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.stonemountain.business.ui.DocSearchApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebView;

public class SearchResultCell extends ListCell<SearchResult> {
    private static final Logger log = LoggerFactory.getLogger(SearchResultCell.class);

    @FXML private WebView hitText;
    @FXML private Label link;
    @FXML private Label title;
    @FXML private Button launchButton;

    private final Node node;
    private Optional<SearchResult> item = Optional.empty();

    public SearchResultCell() {
        super();
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setController(this);
			loader.setLocation(this.getClass().getResource("/fxml/search-result-cell.fxml"));
			node = loader.load(this.getClass().getResourceAsStream("/fxml/search-result-cell.fxml"));
		} catch (IOException e) {
			throw new RuntimeException("Failed to load fxml", e);
		}
    }

    @FXML
    public void initialize() {
        setFocusTraversable(true);
        hitText.getEngine().setUserStyleSheetLocation(getClass().getResource("/htmlstyle.css").toString());
        setOnKeyPressed(e -> {
            log.info("Key Typed: {}", e);
            if (e.getCode() == KeyCode.ENTER) { doLaunch(); }
        });
        focusedProperty().addListener(o -> {
            log.info("focus {} : {}", link.getText(), o);
        });
        selectedProperty().addListener(o -> {
            log.info("selected {} : {}", link.getText(), o);
        });
    }

    @Override
    protected void updateItem(SearchResult item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            this.item = Optional.empty();
            setGraphic(null);
        } else {
            this.item = Optional.of(item);
            title.setText(item.title.get());
            link.setText(item.url.get());            
            hitText.getEngine().loadContent(item.text.get(), "text/html");
            setGraphic(node);
        }
    }

    @FXML
    public void doLaunch(ActionEvent event) {
        doLaunch();
    }

    @FXML
    public void doLaunch(KeyEvent e) {
        log.info("Key Pressed: {}", e);
        if (e.getCode() == KeyCode.ENTER) { 
            doLaunch();
        }
    }

    private void doLaunch() {
        try {
            DocSearchApplication.getApplication().getHostServices().showDocument(link.getText());
        } catch (RuntimeException e) {
            log.error("Failed to launch url: {}", link.getText(), e);
        }
    }
}