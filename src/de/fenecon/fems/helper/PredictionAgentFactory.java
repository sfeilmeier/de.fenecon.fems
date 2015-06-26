/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.helper;

import java.io.FileReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.encog.ml.MLRegression;
import org.encog.persist.EncogDirectoryPersistence;

import de.fenecon.fems.FemsConstants;

/**
 * Abstract class to create a new {@link PredictionAgent}.
 * 
 * @author Stefan Feilmeier
 */
public abstract class PredictionAgentFactory {

	/**
	 * Loads the Encog machine learning network file from "{field}_*.method" and
	 * the properties file from "properties.prop" inside the FILESPATH folder
	 * defined in {@link FemsConstants}. Those files should have been created by
	 * a FADSE simulation. If anything goes wrong, an exception is thrown.
	 * 
	 * @param field
	 *            the field descriptor
	 * @return a {@link Predictor} for this field
	 * @throws Exception
	 *             if {@link Predictor} could not be loaded
	 */
	protected static Predictor getPredictor(Field field) throws Exception {
		// Import Properties file
		final Normalizer normalizer;
		{
			Properties properties = new Properties();
			FileReader reader = new FileReader(Paths.get(FemsConstants.FILESPATH, "properties.prop").toFile());
			properties.load(reader);
			int leadWindowSize = Integer.parseInt(properties.getProperty("LeadWindowSize"));
			if(leadWindowSize != FemsConstants.MAX_PREDICTION_WINDOW) {
				throw new Exception("Provided properties and MLMethods are not fitting for Prediction Window!");
			}
			// Create Normalizer
			String prefix = "Normalizer_" + field + "_";
			Double dataLow = Double.parseDouble(properties.getProperty(prefix + "DataLow"));
			Double dataHigh = Double.parseDouble(properties.getProperty(prefix + "DataHigh"));
			Double normalizedLow = Double.parseDouble(properties.getProperty(prefix + "NormalizedLow"));
			Double normalizedHigh = Double.parseDouble(properties.getProperty(prefix + "NormalizedHigh"));
			normalizer = new Normalizer(dataLow, dataHigh, normalizedLow, normalizedHigh);
		}
		
		// Import Encog file
		Integer lagWindowSize = null;
		MLRegression method = null;
		{
			try (DirectoryStream<Path> paths = Files.newDirectoryStream(Paths.get(FemsConstants.FILESPATH),
					String.format("%s_*.method", field))) {
				for (Path path : paths) {
					String filename = path.getName(path.getNameCount() - 1).toString().split("\\.")[0];
					for (String part : filename.split("_")) {
						if (part.startsWith("LAG")) {
							lagWindowSize = Integer.parseInt(part.substring(3));
						}
					}
					method = (MLRegression) EncogDirectoryPersistence.loadObject(path.toAbsolutePath()
							.resolveSibling(filename + ".method").toFile());
				}
			} catch (Exception e) {
				throw e;
			}
		}
		if (method == null || lagWindowSize == null) {
			throw new Exception("No MLMethod found!");
		}
		
		// Create Predictor
		final Predictor predictor = new Predictor(method, lagWindowSize, normalizer);
		return predictor;
	}
}
