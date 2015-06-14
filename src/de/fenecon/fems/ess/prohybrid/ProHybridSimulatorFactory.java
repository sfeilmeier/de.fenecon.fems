package de.fenecon.fems.ess.prohybrid;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import de.fenecon.fems.FemsConstants;
import de.fenecon.fems.FemsTools;
import de.fenecon.fems.helper.Field;

public class ProHybridSimulatorFactory {
	public static ProHybridSimulator create() throws IOException {
		String fems = "fems20";
		Field[] fields = new Field[]{ 
				FemsConstants.PV1,
				FemsConstants.PV2,
				FemsConstants.CONSUMPTION_PHASE1,
				FemsConstants.CONSUMPTION_PHASE2,
				FemsConstants.CONSUMPTION_PHASE3
		};
		final TreeMap<Long, HashMap<Field, Double>> cacheMap = new TreeMap<Long, HashMap<Field, Double>>();
		
		// read CSV file
		final Reader in = new FileReader(FemsTools.getCsvPath(fems).toFile());
		final Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(in);
		for (CSVRecord record : records) {
			HashMap<Field, Double> values = new HashMap<Field, Double>();
			for(Field field : fields) {
				values.put(field, Double.parseDouble(record.get(field.getName())));
			}			
			cacheMap.put(Long.parseLong(record.get("timestamp")), values);
		}
		return new ProHybridSimulator(cacheMap);
	}
}
