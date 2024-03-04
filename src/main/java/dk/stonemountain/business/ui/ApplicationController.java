package dk.stonemountain.business.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.stonemountain.business.ui.about.AboutDialog;
import dk.stonemountain.business.ui.about.InfoDialog;
import dk.stonemountain.business.ui.about.IssueDialog;
import dk.stonemountain.business.ui.installer.UpdateDialog;
import dk.stonemountain.business.ui.search.SearchResult;
import dk.stonemountain.business.ui.search.SearchResultCell;
import dk.stonemountain.business.ui.search.SearchService;
import dk.stonemountain.business.ui.search.Site;
import dk.stonemountain.business.ui.search.SitesService;
import dk.stonemountain.business.ui.util.gui.ClientRuntime;
import dk.stonemountain.business.ui.util.gui.DialogHelper;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class ApplicationController {
	private static final String SYSTEM_FAILURE_TITLE = "System Failure";
	private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	@FXML private Label time;
	@FXML private BorderPane applicationPane;
	@FXML private Button updateButton;
	@FXML private ListView<SearchResult> searchResultList;
	@FXML private ComboBox<Site> siteSelector;
	@FXML private TextField searchText;
	
	final SitesService sitesService = new SitesService();
	final SearchService searchService = new SearchService();
	final Timeline timeline = new Timeline();

	@FXML
	void doQuit(ActionEvent event) {
		DocSearchApplication.getApplication().quit();
	}

	@FXML
	void doAbout(ActionEvent event) {
		log.trace("Showing about dialog");
		new AboutDialog(applicationPane.getScene().getWindow()).showAndWait();
	}

	@FXML
	void doReportIssue(ActionEvent event) {
		log.trace("Showing issue dialog");
		new IssueDialog(applicationPane.getScene().getWindow()).showAndWait();
	}
	
	@FXML
    void installNewVersion(ActionEvent event) {
		log.info("Installing new version: {}", ApplicationContainer.getInstance().getVersion());
		new UpdateDialog(updateButton.getScene().getWindow()).showAndWait().ifPresent(mustBeUpdated -> {
			ApplicationContainer.getInstance().getInstaller().install(ApplicationContainer.getInstance().getNewSwVersion().get());
		});
    }

	@FXML
	void initialize() {
		log.debug("initializing");
		
		log.trace("Application Pane: {}", applicationPane);
		log.trace("Application Pane center content: {}", applicationPane.getCenter());
		// ImageView imgView = (ImageView) applicationPane.getCenter();
		// applicationPane.setCenter(IconHelper.patchIconPath(imgView));
		
		// Installer stuff
		updateButton.disableProperty().bind(Bindings.createBooleanBinding(() -> !ApplicationContainer.getInstance().getVersion().getNewerVersionAvailable().booleanValue(), ApplicationContainer.getInstance().getVersion().newerVersionAvailableProperty()));
		updateButton.textProperty().bind(Bindings.createStringBinding(() -> ApplicationContainer.getInstance().getVersion().getNewerVersionAvailable().booleanValue() ? "Update to " + ApplicationContainer.getInstance().getVersion().getNewVersion() : "No Updates", ApplicationContainer.getInstance().getVersion().newerVersionAvailableProperty(), ApplicationContainer.getInstance().getVersion().newVersionProperty()));
		ApplicationContainer.getInstance().getVersion().mustBeUpdatedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && newValue.booleanValue()) {
				installNewVersion(null);
			}
		});

		// Content
		searchResultList.itemsProperty().bind(ApplicationContainer.getInstance().searchResultsProperty());
		searchResultList.setCellFactory(p -> new SearchResultCell());
		searchService.resultsUpdated.addListener((c, o, n) -> {
			log.info("SearchResultList changed: {}", searchResultList.getItems().size());
			if (!searchResultList.getItems().isEmpty()) {
				searchResultList.getSelectionModel().select(searchResultList.getItems().get(0));
				searchResultList.requestFocus();
			}
		});

		// Start the update of the time field
		setTime();
		timeline.setCycleCount(Animation.INDEFINITE);
//		timeline.setAutoReverse(true);
		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(5), e -> setTime()));
		timeline.play();

		// Start fetching sites
		siteSelector.itemsProperty().bind(ApplicationContainer.getInstance().sitesProperty());
		siteSelector.itemsProperty().addListener((o, old, newValue) -> {
			log.info("o: {} old: {} newvalue: {}", o, old, newValue);
			log.info("selected: {}", siteSelector.getSelectionModel().getSelectedIndex());
			if (siteSelector.getSelectionModel().getSelectedIndex() == -1 && !o.getValue().isEmpty()) {
				siteSelector.getSelectionModel().select(o.getValue().get(0));
			}
		});
		sitesService.bindConsumer(s -> ApplicationContainer.getInstance().updateSites(s));
		sitesService.activate();
		siteSelector.setConverter(new StringConverter<Site>() {
			public String toString(Site s) { return s != null ? s.displayName.get() : null; }
			public Site fromString(String string) { return null; }
		});

		searchService.searchSite.bind(siteSelector.getSelectionModel().selectedItemProperty());
		searchService.searchQuery.bind(searchText.textProperty());
		searchResultList.itemsProperty().bind(searchService.results);

		searchText.setText("\"fault tolerance\"");
	}

	private void setTime() {
		time.setText(LocalDateTime.now().format(formatter));
	}

	public void failureOccurred(Throwable e) {
		log.error(SYSTEM_FAILURE_TITLE, e);
		DialogHelper.showErrorDialog(time.getScene().getWindow(), SYSTEM_FAILURE_TITLE, "Failed to fetch clusters", e.getMessage(), "Could not show error dialog");
		Platform.exit();
	}

	@FXML
    void doSearch(ActionEvent event) {
		log.error("Search for '{}' in site {}", searchText.getText(), siteSelector.getValue());
		searchService.activate();
	}

	@FXML
    void doShowInfo(ActionEvent event) {
		log.error("show info");
		var infoDialog = new InfoDialog(time.getScene().getWindow(), "http://localhost:8080/searchInfo.html", "Search Information","How to search");
		infoDialog.showAndWait();
    }

	@FXML
    public void doLaunch(KeyEvent e) {
        log.info("Key Pressed: {}", e);
		if (e.getCode() == KeyCode.ENTER) {
			SearchResult selectedItem = searchResultList.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {
				try {
					DocSearchApplication.getApplication().getHostServices().showDocument(selectedItem.url.get());
				} catch (RuntimeException ex) {
					log.error("Failed to launch url: {}", selectedItem.url.get(), ex);
				}
			}
		}
    }
}
