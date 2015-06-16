/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.helper;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.encog.ml.MLRegression;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.obj.SerializeObject;

import de.fenecon.fems.FemsConstants;
import de.fenecon.fems.helper.Predictor;

/**
 * Abstract class to create a new {@link PredictionAgent}.
 * 
 * @author Stefan Feilmeier
 */
public abstract class PredictionAgentFactory {

	/**
	 * Loads the Encog a machine learning network files from
	 * "*_{field}_*.method" and "*_{field}_*.normalization" inside the FILESPATH
	 * folder defined in {@link FemsConstants}. Those files should have been
	 * created by a FADSE simulation. If anything goes wrong, an exception is
	 * thrown.
	 * 
	 * @param field
	 *            the field descriptor
	 * @return a set of {@link Predictor}s for this field
	 * @throws Exception
	 *             if {@link Predictor}s could not be loaded
	 */
	protected static Set<Predictor> getPredictors(Field field) throws Exception {
		final Set<Predictor> predictors = new HashSet<Predictor>();

		// Import Encog files
		try (DirectoryStream<Path> paths = Files.newDirectoryStream(Paths.get(FemsConstants.FILESPATH), "*_" + field
				+ "_*.method")) {
			for (Path path : paths) {
				Integer lagWindowSize = null;
				MLRegression method = null;
				NormalizationHelper normhelper = null;
				Integer leadWindowSize = null;
				String filename = path.getName(path.getNameCount() - 1).toString().split("\\.")[0];
				for (String part : filename.split("_")) {
					if (part.startsWith("LAG")) {
						lagWindowSize = Integer.parseInt(part.substring(3));
					} else if (part.startsWith("LEAD")) {
						leadWindowSize = Integer.parseInt(part.substring(4));
					}
				}
				method = (MLRegression) EncogDirectoryPersistence.loadObject(path.toAbsolutePath()
						.resolveSibling(filename + ".method").toFile());
				normhelper = (NormalizationHelper) SerializeObject.load(path.toAbsolutePath()
						.resolveSibling(filename + ".normalization").toFile());
				if (lagWindowSize != null && method != null && normhelper != null && leadWindowSize != null) {
					Predictor predictor = new Predictor(lagWindowSize, method, normhelper, leadWindowSize);
					predictors.add(predictor);
				}
			}
		} catch (Exception e) {
			throw e;
		}

		// Check if all predictors are available
		for (int lagWindowSize = 1; lagWindowSize <= FemsConstants.MAX_PREDICTION_WINDOW; lagWindowSize++) {
			boolean found = false;
			for (Predictor predictor : predictors) {
				if (predictor.getLagWindowSize() == lagWindowSize) {
					found = true;
				}
			}
			if (!found) {
				throw new Exception("PvPredictor for " + field.toString() + ": " + lagWindowSize
						* FemsConstants.SLICE_SECONDS + " is missing.");
			}
		}

		return predictors;
	}
}
