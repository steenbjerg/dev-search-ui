package dk.stonemountain.business.ui.events;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpSseClient {
	private static final Logger logger = LoggerFactory.getLogger(HttpSseClient.class);
	private static final int MAX_ALLOWED_DISCONNECTION_TIME_MINUTES = 5;

	public class Event {
		public String id;
		public String event;
		public String data;

		public Event(String id, String event, String data) {
			this.id = id;
			this.event = event;
			this.data = data;
		}

		@Override
		public String toString() {
			return "Event [id=" + id + ", event=" + event + ", data=" + data + "]";
		}
	}

	protected final HttpClient client;
	protected final URI uri;
	protected String lastEventID = "";
	protected long retryTimeoutMs = 60000;
	protected CompletableFuture<HttpResponse<Void>> running = null;
	protected Consumer<Event> consumer;
	protected LocalDateTime lastConnectionActivity = null;

	public HttpSseClient(HttpClient client, String url) {
		this.client = client;
		this.uri = URI.create(url);
	}
	
	public HttpSseClient setEventConsumer(Consumer<Event> consumer) {
		this.consumer = consumer;
		return this;
	}
	
	public HttpSseClient setLastEventId(String id) {
		lastEventID = id;
		return this;
	}
	
	public HttpSseClient start() {
		String hostName;
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e1) {
			hostName = "Unknown";
		}
		logger.trace("Starting SSE listener to {} from {} with last event id = {}", uri, hostName, lastEventID);

		Builder requestBuilder = HttpRequest.newBuilder(uri)
				.GET()
				.setHeader("Accept", "text/event-stream")
				.setHeader("Cache-Control", "no-cache")
				.setHeader("Client-Hostname", hostName);

		if (lastEventID != null && !lastEventID.isBlank()) {
			requestBuilder.setHeader("Last-Event-ID", lastEventID);
		}

		HttpRequest request = requestBuilder.build();
		running = client.sendAsync(request, BodyHandlers.ofByteArrayConsumer(new Consumer<Optional<byte[]>>() {
			StringBuilder sb = new StringBuilder();

			@Override
			public void accept(Optional<byte[]> text) {
				lastConnectionActivity = LocalDateTime.now();
				if (text.isPresent()) {
					String textString = new String(text.get(), StandardCharsets.UTF_8);
					sb.append(textString);
					logger.trace("Adding text: {}", textString);

					int index;
					while ((index = sb.indexOf("\n\n")) >= 0) {
						String[] lines = sb.substring(0, index).split("\n");
						sb.delete(0, index + 2); // delete block including "\n\n"
						handleEventBlock(lines);
					}
				}
			}
		}));

		running.handleAsync((r, e) -> {
			logger.trace("Failure at SSE connection: {}, {}", r, e.getMessage());
			if (running != null) {
				logger.trace("Waiting {} ms to reconnect", retryTimeoutMs);
				if (retryTimeoutMs > 0) {
					try {
						Thread.sleep(retryTimeoutMs);
					} catch (InterruptedException ex) {
						throw new RuntimeException("Failed to sleep", ex);
					}
					if (Duration.between(lastConnectionActivity, LocalDateTime.now()).toMinutes() > MAX_ALLOWED_DISCONNECTION_TIME_MINUTES) {
						stop();
					} else {
						start();
					}
				}
			} else {
				logger.trace("Stopping SSE listener");
				stop();
			}
			return null;
		});

		return this;
	}

	/**
	 * Blocks until this client has stopped listening. If not listening then returns
	 * immediately
	 * 
	 * @return This client instance
	 */
	public HttpSseClient join() {
		logger.trace("Joining SSE");
		while (running != null) {
			try {
				running.join();
			} catch (Exception e) {
			}
		}
		logger.trace("Joined");
		lastEventID = "";
		return this;
	}

	/**
	 * Stops without reconnecting. Executes {@link EventStreamListener#onClose()} on
	 * listeners
	 * 
	 * @return This client instance
	 */
	public HttpSseClient stop() {
		logger.trace("Stopping SSE handler");
		CompletableFuture<HttpResponse<Void>> run = running;
		running = null;
		HttpResponse<Void> response = null;
		if (run != null) {
			if (run.isDone()) {
				if (!run.isCancelled() && !run.isCompletedExceptionally()) {
					response = run.getNow(null);
					logger.trace("Response status code: {}", response.statusCode());
				}
			} else {
				run.cancel(true);
				logger.trace("Cancelled");
			}
		}
		logger.trace("Close");
		return this;
	}

	private void handleEventBlock(String[] lines) {
		StringBuilder data = new StringBuilder();
		String event = null;
		var hasDataOrEvent = false;

		for (String line : lines) {
			int idx = line.indexOf(':');
			if (idx <= 0) {
				continue; // ignore invalids or comments
			}
			String key = line.substring(0, idx);
			String value = line.substring(idx + 1).trim();

			switch (key.trim().toLowerCase()) {
			case "event":
				event = value;
				hasDataOrEvent = true;
				break;

			case "data":
				if (data.length() > 0) {
					data.append("\n");
				}
				data.append(value);
				hasDataOrEvent = true;
				break;

			case "id":
				try {
					lastEventID = value;
				} catch (Exception ex) {
					logger.error("failed to handle id: {}", value, ex);
				}
				break;

			case "retry":
				try {
					retryTimeoutMs = Long.parseLong(value);
				} catch (Exception ex) {
					logger.error("failed to handle retry: {}", value, ex);
				}
				break;

			default:
				break;
			}
		}

		if (hasDataOrEvent) {
			Event eventReceived = new Event(lastEventID, event, data.toString());
			logger.trace("Incoming Event: {}", eventReceived);
			if (consumer != null) {
				consumer.accept(eventReceived);
			}
		}
	}

	public static void receiveEvent(Event e) {
		logger.trace("Event received: {}", e);
	}
	
	public static void main(String[] args) {
		String lastId = "";
		if (args.length == 1) {
			lastId = args[0].trim();
		}
		logger.info("Last event id: {}", lastId);
		new HttpSseClient(HttpClient.newHttpClient(), "http://localhost:8080/events/sse")
			.setEventConsumer(HttpSseClient::receiveEvent)
			.setLastEventId(lastId)
			.start()
			.join();
	}
}