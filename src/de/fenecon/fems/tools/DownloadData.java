package de.fenecon.fems.tools;

import java.io.FileWriter;
import java.io.IOException;
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
import de.fenecon.fems.types.Field;

public class DownloadData {

	private final static String QUERY = "SELECT MEAN(value) FROM %s GROUP BY time(%ds) fill(null) WHERE time > %ds AND time < %ds";
	
	public static void main(String[] args) throws IOException {
		DownloadData d = new DownloadData();
		DateTime fromDate = new DateTime(2015, 1, 1, 0, 0, FemsConstants.LOCAL_TIMEZONE);
		DateTime toDate = new DateTime(2015, 6, 1, 0, 0, FemsConstants.LOCAL_TIMEZONE);
		d.start("fems20", FemsConstants.PV1, fromDate, toDate);
	}

	public void start(String fems, Field field, DateTime fromDate, DateTime toDate) throws IOException {
		// Preparations (timestamps, query)
		System.out.println("From: " + fromDate);
		System.out.println("To : " + toDate);
		long fromTimestamp = fromDate.toDateTime(DateTimeZone.UTC).getMillis() / 1000;
		long toTimestamp = toDate.toDateTime(DateTimeZone.UTC).getMillis() / 1000;
		String query = String.format(QUERY, field.getTechnicalName(), FemsConstants.SLICE_SECONDS, fromTimestamp, toTimestamp);
		System.out.println("Query: " + query);
		
		// Get data from influxdb
		InfluxDB influxDB = InfluxDBFactory.connect("http://fenecon.de:8086", "readonly", "Wog9a6z1iryfKHz3EK6u");
		List<Serie> series = influxDB.query(fems, query, TimeUnit.SECONDS);
		
		// Write to internal HashMap to get rid of double entries by influxdb
		SortedMap<Long, Double> data = new TreeMap<Long, Double>();
		for(Serie serie : series) {
			if(serie.getName().equals(field.getTechnicalName())) {
				List<Map<String, Object>> rows = serie.getRows();
				for(Map<String, Object> row : rows) {
					Long timestamp = ((Double)row.get("time")).longValue();
					data.putIfAbsent(timestamp, (Double)row.get("mean"));
				}
			}
		}
		
		// Open CSV handler
		try(CSVPrinter csvFilePrinter = new CSVPrinter(new FileWriter(FemsTools.getCsvPath(fems, field).toFile()), FemsConstants.CSV_FORMAT)) {
			csvFilePrinter.printRecord("timestamp", "value");
			//double lastValue = 0.;
			for(Long timestamp : data.keySet()) {
				int hour = new DateTime(timestamp*1000, DateTimeZone.UTC).toDateTime(FemsConstants.LOCAL_TIMEZONE).getHourOfDay();
				//final Double realvalue; 
				if(hour < 6 || hour > 20) {
					// clear night hours
				} else {
					Double thisvalue = data.get(timestamp);
					if(thisvalue == null) {
						// ignore null values
					} else {
						//realvalue = thisvalue != null ? thisvalue : lastValue; // simple interpolation
						//csvFilePrinter.printRecord(timestamp, realvalue);
						//lastValue = realvalue;
						csvFilePrinter.printRecord(timestamp, thisvalue);
					}
				}
			}
		};
	}
}
