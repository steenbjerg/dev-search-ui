package dk.stonemountain.business.ui.search;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.stonemountain.business.ui.search.backend.DocSearchDao;
import dk.stonemountain.business.ui.search.backend.SitesDTO;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;

public class SitesService extends Service<List<Site>> {
	private static Logger log = LoggerFactory.getLogger(SitesService.class);

	@FunctionalInterface
	public interface Consumer {
		void handle(List<Site> sites);
	}

	@FunctionalInterface
	public interface FailureHandler {
		void handle(Throwable e);
	}

	private List<Consumer> consumers = new ArrayList<>();
	private List<FailureHandler> failureHandlers = new ArrayList<>();
	private DocSearchDao dao = new DocSearchDao();
	
	public SitesService() {
		log.trace("Constructed");
		setOnSucceeded(this::handleEvent);
		setOnFailed(this::failed);
		setOnCancelled(this::log);
		setOnReady(this::log);
		setOnRunning(this::log);
		setOnScheduled(this::log);
	}

	@Override
	protected Task<List<Site>> createTask() {
		log.debug("creating task");
		return new SitesTask(dao);
	}
	
	protected void failed(WorkerStateEvent event) {
		Throwable e = getException();
		if (e != null) {
			log.error("Service failed", e);
			failureHandlers.stream().forEach(h -> h.handle(e));
		}
	}

	public void bindFailureHandler(FailureHandler handler) {
		failureHandlers.add(handler);
	}

	public void bindConsumer(Consumer consumer) {
		consumers.add(consumer);
	}
	
	public void activate() {
		log.trace("activating");
		restart();
	}

	@SuppressWarnings("unchecked")
	private void handleEvent(WorkerStateEvent event) {
		List<Site> data = (List<Site>) event.getSource().getValue();
		log.trace("Sites returning to consumer : {}", data);
		consumers.stream().forEach(c -> c.handle(data));
	}

	private void log(WorkerStateEvent event) {
		log.trace("StateChange: {}", event.getEventType().getName(), getException());
	}
	
	private static class SitesTask extends Task<List<Site>> {
		private static final Logger log = LoggerFactory.getLogger(SitesTask.class);
		private final DocSearchDao dao;
		
		public SitesTask(DocSearchDao dao) {
			log.trace("constructed");
			this.dao = dao;
		}
		
		@Override
		protected List<Site> call() throws Exception {
			log.trace("Starting to fetch sites");
			// CompletableFuture<List<SiteDTO>> siteDTOFuture = dao.getSites();
			// List<SiteDTO> siteDTOs = siteDTOFuture.get();
			SitesDTO sitesDTO = dao.getSites();
			log.trace("Fetched sites: {}", sitesDTO);
			if (sitesDTO == null || sitesDTO.sites() == null) {
				throw new RuntimeException("No sites returned");
			}
			List<Site> result = sitesDTO.sites().stream()
				.map(SiteMapper::map)
				.toList();
			log.trace("Sites fetched: {}", result);
			return result;
		}
	

	}
}