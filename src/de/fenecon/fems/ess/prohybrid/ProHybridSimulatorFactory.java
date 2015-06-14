package de.fenecon.fems.ess.prohybrid;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import de.fenecon.fems.FemsConstants;
import de.fenecon.fems.FemsTools;

public class ProHybridSimulatorFactory {
	public static ProHybridSimulator create() throws IOException {
		final LinkedList<String[]> historyCache = new LinkedList<String[]>();
		final Reader in = new FileReader(FemsTools.getCsvPath("fems20", FemsConstants.PV1).toFile());
		final Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(in);
		for (CSVRecord r : records) {
			historyCache.add(new String[] { r.get("timestamp"), r.get("value") });
		}
		return new ProHybridSimulator(historyCache);
	}
}
