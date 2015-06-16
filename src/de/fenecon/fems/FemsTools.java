/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Hold helper tools that are used throughout the application.
 * 
 * @author Stefan Feilmeier
 */
public class FemsTools {
	/**
	 * Gets the path of the CSV file for a specific FEMS.
	 * 
	 * @param fems
	 *            the name of the fems (e.g. "fems1")
	 * @return the path to the CSV file
	 */
	public static Path getCsvPath(String fems) {
		return Paths.get(FemsConstants.FILESPATH, fems + ".csv");
	}

	/**
	 * Returns the current timestamp, rounded down to a time-slice.
	 * 
	 * @return the rounded current timestamp
	 */
	public static long getCurrentRoundedUtcTimestamp() {
		/*
		 * DateTime nowInUtc = new
		 * DateTime(FemsConstants.LOCAL_TIMEZONE).toDateTime(DateTimeZone.UTC);
		 * return roundTimestampToSlice((nowInUtc.getMillis() / 1000));
		 */
		return FemsConstants.CURRENT_TIMESTAMP; // only for Simulation
	}

	/**
	 * Rounds a given timstamp down to the next time-slice.
	 * 
	 * @param timestamp
	 *            the timestamp to round
	 * @return the rounded timestamp
	 */
	public static long roundTimestampToSlice(long timestamp) {
		return (timestamp / (FemsConstants.SLICE_SECONDS)) * (FemsConstants.SLICE_SECONDS);
	}

	/**
	 * Pretty prints a timestamp in human readable form in the local timezone.
	 * 
	 * @param timestamp
	 *            the timestamp to format
	 * @return the formatted timestamp
	 */
	public static String timestampToString(long timestamp) {
		DateTime date = new DateTime(timestamp * 1000, DateTimeZone.UTC).toDateTime(FemsConstants.LOCAL_TIMEZONE);
		return date.toString("dd.MM.y HH:mm");
	}
}
