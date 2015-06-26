/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.helper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.csv.CSVPrinter;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Serie;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import de.fenecon.fems.FemsConstants;
import de.fenecon.fems.FemsTools;
import de.fenecon.fems.agent.source.pv.PvField;

/**
 * Helper App to generate a CSV file from the FENECON Online-Monitoring InfluxDB
 * database.
 * 
 * @author Stefan Feilmeier
 */
public class DownloadDataApp {
	/** parameterized, SQL-like query string */
	private final static String QUERY = "SELECT MEAN(value) FROM %s GROUP BY time(%ds) fill(null) WHERE time > %ds AND time < %ds";

	/**
	 * Defines from- and to-date, and the fields that should be received from
	 * the InfluxDB database and starts the download.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		DownloadDataApp d = new DownloadDataApp();
		DateTime fromDate = new DateTime(2015, 1, 1, 0, 0, FemsConstants.LOCAL_TIMEZONE);
		DateTime toDate = new DateTime(2015, 6, 1, 0, 0, FemsConstants.LOCAL_TIMEZONE);
		Field[] fields = new Field[] { FemsConstants.PV1, FemsConstants.PV2, FemsConstants.CONSUMPTION_PHASE1,
				FemsConstants.CONSUMPTION_PHASE2, FemsConstants.CONSUMPTION_PHASE3 };
		String fems = FemsConstants.FEMS_NAME;
		String influxdbUser = args[0];
		String influxdbPassword = args[1];

		d.start(fems, influxdbUser, influxdbPassword, fields, fromDate, toDate);
	}

	/**
	 * Starts the download and saves the result to the CSV file as defined in
	 * {@link FemsTools#getCsvPath()}
	 * 
	 * @param fems
	 *            the name of the FEMS to query
	 * @param influxdbUser
	 *            the InfluxDB username
	 * @param influxdbPassword
	 *            the InfluxDB password
	 * @param fields
	 *            the fields to query
	 * @param fromDate
	 *            the from-date of the query
	 * @param toDate
	 *            the to-date of the query
	 * @throws IOException
	 *             if anything goes wrong and it was not able to create the CSV
	 *             file.
	 */
	private void start(String fems, String influxdbUser, String influxdbPassword, Field[] fields, DateTime fromDate,
			DateTime toDate) throws IOException {
		// Preparations (timestamps, query)
		System.out.println("From: " + fromDate);
		System.out.println("To : " + toDate);
		long fromTimestamp = fromDate.toDateTime(DateTimeZone.UTC).getMillis() / 1000;
		long toTimestamp = toDate.toDateTime(DateTimeZone.UTC).getMillis() / 1000;
		StringBuilder queryFromString = new StringBuilder();
		String split = "";
		for (Field field : fields) {
			queryFromString.append(split);
			queryFromString.append(field.getTechnicalName());
			split = ", ";
		}

		String query = String.format(QUERY, queryFromString, FemsConstants.SLICE_SECONDS, fromTimestamp, toTimestamp);
		System.out.println("Query: " + query);

		// Get data from influxdb
		InfluxDB influxDB = InfluxDBFactory.connect(FemsConstants.INFLUXDB_URL, influxdbUser, influxdbPassword);
		List<Serie> series = influxDB.query(fems, query, TimeUnit.SECONDS);

		// Write to internal HashMap to get rid of double entries by influxdb
		SortedMap<Long, HashMap<Field, Double>> data = new TreeMap<Long, HashMap<Field, Double>>();
		for (Serie serie : series) {
			Field thisField = null;
			for (Field field : fields) {
				if (field.getTechnicalName().equals(serie.getName())) {
					thisField = field;
				}
			}
			assert thisField != null;

			List<Map<String, Object>> rows = serie.getRows();
			for (Map<String, Object> row : rows) {
				Long timestamp = ((Double) row.get("time")).longValue();
				data.putIfAbsent(timestamp, new HashMap<Field, Double>());
				HashMap<Field, Double> valuePerField = data.get(timestamp);
				if (valuePerField.get(thisField) == null) {
					Double value = (Double) row.get("mean");
					value = processData(timestamp, thisField, value);
					valuePerField.put(thisField, value);
				}
			}
		}
		// Open CSV handler
		try (CSVPrinter csvFilePrinter = new CSVPrinter(new FileWriter(FemsTools.getCsvPath(fems).toFile()),
				FemsConstants.CSV_FORMAT)) {
			csvFilePrinter.print("timestamp");
			for (Field field : fields) {
				csvFilePrinter.print(field.getName());
			}
			csvFilePrinter.println();
			for (Long timestamp : data.keySet()) {
				csvFilePrinter.print(timestamp);
				HashMap<Field, Double> valuePerField = data.get(timestamp);
				for (Field field : fields) {
					Double value = valuePerField.get(field);
					if (value != null) {
						csvFilePrinter.print(value);
					} else {
						// TODO: here we simply replace null with 0.; better: interpolation
						csvFilePrinter.print(0.);
					}
				}
				csvFilePrinter.println();
			}
		}
		;
	}

	/**
	 * Process data values, e.g. to clean it
	 * 
	 * @param timestamp
	 *            the timestamp
	 * @param field
	 *            the field of this value
	 * @param value
	 *            the value
	 * @return the cleaned value or null
	 */
	private Double processData(Long timestamp, Field field, Double value) {
		if (value == null)
			return null; // replace null with 0.
		if (field instanceof PvField) {
			DateTime date = FemsTools.timestampToDateTime(timestamp);
			int hourOfDay = date.getHourOfDay(); // from 0 to 23
			if (hourOfDay < 6 || hourOfDay > 20)
				return 0.; // return 0 for PV data in the night
			if ((hourOfDay < 9 || hourOfDay > 17) && value < 65)
				return 0.; // return 0 for wrong data in morning/evening
		}
		return value;
	}
}
