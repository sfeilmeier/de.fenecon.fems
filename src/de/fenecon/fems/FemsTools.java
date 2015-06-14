package de.fenecon.fems;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FemsTools {
	public static Path getCsvPath(String fems) {
		return Paths.get(FemsConstants.FILESPATH, fems + ".csv");
	}
	
	public static long roundTimestampToSlice(long timestamp) {
		return (timestamp / (FemsConstants.SLICE_SECONDS)) * (FemsConstants.SLICE_SECONDS);
	}
	
	public static long getCurrentRoundedUtcTimestamp() {
		/*DateTime nowInUtc = new DateTime(FemsConstants.LOCAL_TIMEZONE).toDateTime(DateTimeZone.UTC);
		return roundTimestampToSlice((nowInUtc.getMillis() / 1000));*/
		return FemsConstants.CURRENT_TIMESTAMP; // only for Simulation
	}
}
