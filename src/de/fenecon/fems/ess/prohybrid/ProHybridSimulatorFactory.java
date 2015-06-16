/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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

/**
 * Factory for a {@link ProHybridSimulator}.
 * 
 * @author Stefan Feilmeier
 */
public class ProHybridSimulatorFactory {

	/**
	 * Creates a new {@link ProHybridSimulator} with data from a CSV file.
	 * 
	 * @return the new {@link ProHybridSimulator}
	 * @throws IOException
	 *             if no valid {@link ProHybridSimulator} could be created from
	 *             the CSV file
	 */
	public static ProHybridSimulator create(String fems) throws IOException {
		Field[] fields = new Field[] { FemsConstants.PV1, FemsConstants.PV2, FemsConstants.CONSUMPTION_PHASE1,
				FemsConstants.CONSUMPTION_PHASE2, FemsConstants.CONSUMPTION_PHASE3 };
		final TreeMap<Long, HashMap<Field, Double>> cacheMap = new TreeMap<Long, HashMap<Field, Double>>();

		// read CSV file
		final Reader in = new FileReader(FemsTools.getCsvPath(fems).toFile());
		final Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(in);
		for (CSVRecord record : records) {
			HashMap<Field, Double> values = new HashMap<Field, Double>();
			for (Field field : fields) {
				values.put(field, Double.parseDouble(record.get(field.getName())));
			}
			cacheMap.put(Long.parseLong(record.get("timestamp")), values);
		}
		return new ProHybridSimulator(cacheMap);
	}
}
