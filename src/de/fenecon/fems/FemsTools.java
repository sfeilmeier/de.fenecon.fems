package de.fenecon.fems;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import de.fenecon.fems.types.Field;

public class FemsTools {
	public static Path getCsvPath(String fems, Field field) {
		return Paths.get(FemsConstants.FILESPATH, fems + "_" + field + ".csv");
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
