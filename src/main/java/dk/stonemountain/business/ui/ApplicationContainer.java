package dk.stonemountain.business.ui;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.stonemountain.business.ui.events.EventsReceiverService;
import dk.stonemountain.business.ui.events.HttpSseClient;
import dk.stonemountain.business.ui.installer.PackageInstaller;
import dk.stonemountain.business.ui.installer.VersionInformation;
import dk.stonemountain.business.ui.search.SearchResult;
import dk.stonemountain.business.ui.search.Site;
import dk.stonemountain.business.ui.search.SiteMapper;
import dk.stonemountain.business.ui.search.backend.SiteDTO;
import dk.stonemountain.business.ui.util.jaxrs.JsonbHelper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ApplicationContainer {	
	private static final Logger log = LoggerFactory.getLogger(ApplicationContainer.class);	
	private static ApplicationContainer instance = new ApplicationContainer();

	private final UserPreferences userPreferences = new UserPreferences();
	private final BooleanProperty updatedVersionReady = new SimpleBooleanProperty(); 
	private final VersionInformation version = new VersionInformation();
	private final ObjectProperty<Path> newSwVersion = new SimpleObjectProperty<>(); 
	private final PackageInstaller installer = new PackageInstaller();
	private final Backend currentBackend = Backend.LOCAL;
	private final EventsReceiverService eventsReceiver = new EventsReceiverService(this::dispatchEvent, this::handleLostConnection);
	// private final ObservableList<SearchResult> searchResults = FXCollections.observableArrayList();
	private Property<ObservableList<SearchResult>> searchResultProperty = new SimpleObjectProperty<>(FXCollections.observableArrayList());
	private Property<ObservableList<Site>> sitesProperty = new SimpleObjectProperty<>(FXCollections.observableArrayList());

	public static ApplicationContainer getInstance() {
		return instance;
	}
	
	private ApplicationContainer() {
		// to prevent instantiation)
	}
	
	public void start() {
		log.info("Starting application container");
		installer.startInstaller();
		eventsReceiver.start();
	}

	public Backend getCurrentBackend() {
		return currentBackend;
	}

	public UserPreferences getUserPreferences() {
		return userPreferences;
	}

	public PackageInstaller getInstaller() {
		return installer;
	}

	public VersionInformation getVersion() {
		return version;
	}

	public ObjectProperty<Path> getNewSwVersion() {
		return newSwVersion;
	}
	
	public BooleanProperty updatedVersionReadyProperty() {
		return updatedVersionReady;
	}

	public void updatedVersionReady(VersionInformation info, Path file) {
		updatedVersionReady.set(true);
		newSwVersion.set(file);

		version.setNewSha(info.getNewSha());
		version.setNewVersion(info.getNewVersion());
		version.setNewReleaseNote(info.getNewReleaseNote());
		version.setNewerVersionAvailable(info.getNewerVersionAvailable());
		version.setNewReleaseTime(info.getNewReleaseTime());
		version.setMustBeUpdated(info.getMustBeUpdated());
	}
	
	public Property<ObservableList<SearchResult>> searchResultsProperty() {
		return searchResultProperty;
	}

	// public ObservableList<SearchResult> getSearchResults() {
	// 	return searchResults;
	// }

	public Property<ObservableList<Site>> sitesProperty() {
		return sitesProperty;
	}

	private void dispatchEvent(HttpSseClient.Event event) {
		log.info("Event received: {}", event);
		if("message".equals(event.event)) {
			var dto = JsonbHelper.fromJson(event.data, SiteDTO.class);
			var site = SiteMapper.map(dto);
			sitesProperty.getValue().add(site);
		}
	}

	private void handleLostConnection(LocalDateTime lastActivity) {
		log.info("Event Connection lost at {}", lastActivity);
		eventsReceiver.start();
	}

	public void updateSites(List<Site> sites) {
		sitesProperty.setValue(FXCollections.observableArrayList(sites));
	}
}
