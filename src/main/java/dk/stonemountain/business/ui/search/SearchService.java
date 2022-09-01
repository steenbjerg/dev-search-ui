package dk.stonemountain.business.ui.search;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.stonemountain.business.ui.search.SearchService.Status.Outcome;
import dk.stonemountain.business.ui.search.backend.DocSearchDao;
import dk.stonemountain.business.ui.search.backend.SearchResultDTO;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;

public class SearchService extends Service<List<SearchResult>> {
	private static Logger log = LoggerFactory.getLogger(SearchService.class);

	public ObjectProperty<Site> searchSite = new SimpleObjectProperty<>();
	public StringProperty searchQuery = new SimpleStringProperty();
	public ObjectProperty<ObservableList<SearchResult>> results = new SimpleObjectProperty<>();
	public ObjectProperty<LocalDateTime> resultsUpdated = new SimpleObjectProperty<>();
	public ObjectProperty<Status> status = new SimpleObjectProperty<>();

	public static class Status {
		public enum Outcome {
			SUCCESSFUL, FAILURE;
		}

		public Outcome result;
		public String message; 

		public Status(Outcome result, String message) {
			this.result = result;
			this.message = message;
		}
	}

	private DocSearchDao dao = new DocSearchDao();
	
	public SearchService() {
		log.trace("Constructed");
		setOnSucceeded(this::handleEvent);
		setOnFailed(this::failed);
		setOnCancelled(this::log);
		setOnReady(this::log);
		setOnRunning(this::log);
		setOnScheduled(this::log);
	}

	@Override
	protected Task<List<SearchResult>> createTask() {
		log.debug("creating task");
		return new SearchTask(dao);
	}
	
	protected void failed(WorkerStateEvent event) {
		Throwable e = getException();
		if (e != null) {
			log.error("Service failed", e);
			results.set(FXCollections.observableArrayList());
			status.set(new Status(Outcome.FAILURE, e.getMessage()));
		}
	}
	
	public void activate() {
		log.trace("activating");
		status.set(new Status(Outcome.SUCCESSFUL, ""));
		restart();
	}

	@SuppressWarnings("unchecked")
	private void handleEvent(WorkerStateEvent event) {
		List<SearchResult> data = (List<SearchResult>) event.getSource().getValue();
		log.trace("SearchResults returning to consumer : {}", data);
		status.set(new Status(Outcome.SUCCESSFUL, ""));
		results.set(FXCollections.observableArrayList(data));
		resultsUpdated.set(LocalDateTime.now());
	}

	private void log(WorkerStateEvent event) {
		log.trace("StateChange: {}", event.getEventType().getName(), getException());
	}
	
	private class SearchTask extends Task<List<SearchResult>> {
		private static final Logger log = LoggerFactory.getLogger(SearchTask.class);
		private final DocSearchDao dao;
		private final String site;
		private final String query;
		
		public SearchTask(DocSearchDao dao) {
			site = searchSite.get().name.get();
			query = searchQuery.get();
			log.trace("constructed with site {} and query {}", site, query);
			this.dao = dao;
		}
		
		@Override
		protected List<SearchResult> call() throws Exception {
			log.debug("Starting to search in site {} with query {}: ", site, query);
			CompletableFuture<List<SearchResultDTO>> future = dao.search(site, query);
			List<SearchResultDTO> dtos = future.get();
			log.trace("Fetched search results: {}", dtos);
			List<SearchResult> result = dtos.stream()
				.map(SearchResultMapper::map)
				.toList();
			log.trace("Search Results fetched: {}", result);
			return result;
		}	
	}
}