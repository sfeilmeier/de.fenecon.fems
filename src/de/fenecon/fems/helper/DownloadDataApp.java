package de.fenecon.fems.helper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
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

public class DownloadDataApp {

	private final static String QUERY = "SELECT MEAN(value) FROM %s GROUP BY time(%ds) fill(null) WHERE time > %ds AND time < %ds";
	
	public static void main(String[] args) throws IOException {
		DownloadDataApp d = new DownloadDataApp();
		DateTime fromDate = new DateTime(2015, 1, 1, 0, 0, FemsConstants.LOCAL_TIMEZONE);
		DateTime toDate = new DateTime(2015, 6, 1, 0, 0, FemsConstants.LOCAL_TIMEZONE);
		Field[] fields = new Field[]{ 
				FemsConstants.PV1,
				FemsConstants.PV2,
				FemsConstants.CONSUMPTION_PHASE1,
				FemsConstants.CONSUMPTION_PHASE2,
				FemsConstants.CONSUMPTION_PHASE3
		};
		String fems = "fems20";
		
		d.start(fems, fields, fromDate, toDate);
	}

	public void start(String fems, Field[] fields, DateTime fromDate, DateTime toDate) throws IOException {
		// Preparations (timestamps, query)
		System.out.println("From: " + fromDate);
		System.out.println("To : " + toDate);
		long fromTimestamp = fromDate.toDateTime(DateTimeZone.UTC).getMillis() / 1000;
		long toTimestamp = toDate.toDateTime(DateTimeZone.UTC).getMillis() / 1000;
		StringBuilder queryFromString = new StringBuilder();
		String split = "";
		for(Field field : fields) {
			queryFromString.append(split);
			queryFromString.append(field.getTechnicalName());
			split = ", ";
		}
		
		String query = String.format(QUERY, queryFromString, FemsConstants.SLICE_SECONDS, fromTimestamp, toTimestamp);
		System.out.println("Query: " + query);
		
		// Get data from influxdb
		InfluxDB influxDB = InfluxDBFactory.connect("http://fenecon.de:8086", "readonly", "Wog9a6z1iryfKHz3EK6u");
		List<Serie> series = influxDB.query(fems, query, TimeUnit.SECONDS);
		
		// Write to internal HashMap to get rid of double entries by influxdb
		SortedMap<Long, Double[]> data = new TreeMap<Long, Double[]>();
		List<String> headers = new LinkedList<String>();
		for(Serie serie : series) {
			for(Field field : fields) {
				if(field.getTechnicalName().equals(serie.getName())) {
					headers.add(field.getName());
				}
			}
			int colIndex = headers.size()-1;
			List<Map<String, Object>> rows = serie.getRows();
			for(Map<String, Object> row : rows) {
				Long timestamp = ((Double)row.get("time")).longValue();
				data.putIfAbsent(timestamp, new Double[fields.length]);
				Double[] dataRow = data.get(timestamp);
				if(dataRow[colIndex] == null) {
					dataRow[colIndex] = (Double)row.get("mean");
				}
			}
		}
		// Open CSV handler
		try(CSVPrinter csvFilePrinter = new CSVPrinter(new FileWriter(FemsTools.getCsvPath(fems).toFile()), FemsConstants.CSV_FORMAT)) {
			csvFilePrinter.print("timestamp");
			for(String header : headers) {
				csvFilePrinter.print(header);
			}
			csvFilePrinter.println();
			for(Long timestamp : data.keySet()) {
				Double[] dataRow = data.get(timestamp);
				if(dataRow[1] != null) { // ignore null
					csvFilePrinter.print(timestamp);
					for(Double datafield : dataRow) {
						csvFilePrinter.print(datafield);
					}
					csvFilePrinter.println();
				}
			}
		};
	}
}
