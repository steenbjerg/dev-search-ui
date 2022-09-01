package dk.stonemountain.business.ui.events;

import java.net.http.HttpClient.Builder;
import java.net.http.HttpClient.Version;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.stonemountain.business.ui.ApplicationContainer;
import javafx.application.Platform;

public class EventsReceiverService {
	private Consumer<HttpSseClient.Event> eventConsumer;
	private Consumer<LocalDateTime> lostConnectionConsumer;
	private ReceiveEventsTask currentReceiverTask = null;
	private AtomicBoolean running = new AtomicBoolean(false);

	public EventsReceiverService(Consumer<HttpSseClient.Event> eventConsumer, Consumer<LocalDateTime> lostConnectionConsumer) {
		this.eventConsumer = eventConsumer;
		this.lostConnectionConsumer = lostConnectionConsumer;
	}

	private final ExecutorService exec = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r);
        t.setName("Event Receiver");
        t.setDaemon(true);
        return t;
    });
	
	public void start() {
		currentReceiverTask = new ReceiveEventsTask(eventConsumer, this::hasStopped);
		running.set(true);
		exec.execute(currentReceiverTask);
	}
	
	public void stop() {
		running.set(false);
		if (currentReceiverTask != null) {
			currentReceiverTask.stop();
			currentReceiverTask = null;
		}
	}

	public void hasStopped(LocalDateTime lastActivity) {
		if (running.get() && lostConnectionConsumer != null) {
			Platform.runLater(() -> lostConnectionConsumer.accept(LocalDateTime.now()));
		}
	}

	private static class ReceiveEventsTask implements Runnable {
		private static final Logger log = LoggerFactory.getLogger(ReceiveEventsTask.class);
		private final HttpSseClient client;
		private final Consumer<HttpSseClient.Event> eventsConsumer;
		private final Consumer<LocalDateTime> lostConnectionConsumer;

		public ReceiveEventsTask(Consumer<HttpSseClient.Event> consumer, Consumer<LocalDateTime> lostConnectionConsumer) {
			this.eventsConsumer = consumer;
			this.lostConnectionConsumer = lostConnectionConsumer;

			Builder httpClientBuilder = ApplicationContainer.getInstance().getCurrentBackend()
					.getHttpClientBuilder()
					.version(Version.HTTP_2);
			String sseUrl = ApplicationContainer.getInstance().getCurrentBackend().getBffServiceUrl() + "/events/sse";
			client = new HttpSseClient(httpClientBuilder.build(), sseUrl);
		}
		
		@Override
		public void run() {
			log.debug("Starting to receive events");
			client
				.setEventConsumer(this::handleEvent)
				.start()
				.join();
			log.debug("No longer receiving events");
			if (lostConnectionConsumer != null) {
				Platform.runLater(() -> lostConnectionConsumer.accept(client.lastConnectionActivity));
			}
		}

		public void stop() {
			client.stop();
		}

		protected void handleEvent(HttpSseClient.Event e) {
			log.trace("Event received: {}", e);
			Platform.runLater(() -> eventsConsumer.accept(e));
		}
	}
}