package dk.stonemountain.business.ui.util.time;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class TimeConverter {
	private TimeConverter() {
		// prevent instanstiation
	}
	
	public static Date toDate(LocalDateTime t) {
		return Date.from(t.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static LocalDateTime toLocalDateTime(Date t) {
		return Instant.ofEpochMilli(t.getTime())
			.atZone(ZoneId.systemDefault())
			.toLocalDateTime();
	}

	public static LocalDateTime toLocalDateTime(ZonedDateTime t) {
		return t.toLocalDateTime();
	}
}
