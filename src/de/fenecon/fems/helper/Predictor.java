/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.helper;

import java.util.HashMap;

import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;

/**
 * A wrapper around the Encog objects and methods to calculate the result of a
 * machine learning network.
 * 
 * @author Stefan Feilmeier
 */
public class Predictor {
	/**
	 * The lag window size of this prediction method. It represents the number of past values in the input vector for the machine learning method.
	 */
	private final int lagWindowSize;
	/**
	 * The Encog method used for this network. The object is loaded from a
	 * serialized file using {@link PredictionAgentFactory}
	 */
	private final MLRegression method;
	/**
	 * The Normalizer used for this network. The object is
	 * loaded from a serialized file using {@link PredictionAgentFactory}
	 */
	private final Normalizer normalizer;
	/**
	 * The time-series window cache for the input vector of this method
	 */
	private final RingCache windowCache;

	/**
	 * Creates a new Predictor. Use via {@link PredictionAgentFactory}.
	 * 
	 * @param method
	 *            the Encog prediction method
	 * @param lagWindowSize
	 *            the lag window size for the time-series window
	 * @param normalizer
	 *            the Normalizer
	 */
	public Predictor(MLRegression method, int lagWindowSize, Normalizer normalizer) {
		this.method = method;
		this.lagWindowSize = lagWindowSize;
		this.normalizer = normalizer;
		this.windowCache = new RingCache(lagWindowSize);
	}

	/**
	 * Adds a new value to the time-series window and calculates the new
	 * {@link Prediction}s.
	 * 
	 * @param value
	 *            the new value
	 * @return the calculated predictions per leadWindow
	 */
	public synchronized HashMap<Integer, Prediction> addValueAndPredict(double newValue) {
		HashMap<Integer, Prediction> predictionsPerLead = new HashMap<Integer, Prediction>();
		Double newNormValue = normalizer.normalize(newValue);
		windowCache.push(newNormValue);
		/* TODO
		while (!window.isReady()) {
			window.add(slice); // if window not full, just interpolate current
								// value => better a bad prediction than none
		} */
		if(windowCache.isWindowReady()) {
			MLData mlInputData = new BasicMLData(windowCache.getWindow());
			MLData mlOutputData = method.compute(mlInputData);
			for(int lead=1; lead<mlOutputData.size()+1; lead++) {
				double normValue = mlOutputData.getData(lead-1);
				double value = normalizer.denormalize(normValue);
				predictionsPerLead.put(lead, new Prediction(value, lead));
			}
		}
		return predictionsPerLead;
	}

	/**
	 * Gets the Encog machine learning network method.
	 * 
	 * @return the Encog method.
	 */
	public MLRegression getMethod() {
		return method;
	}

	/**
	 * Gets the Normalizer.
	 * 
	 * @return the Normalizer
	 */
	public Normalizer getNormalizer() {
		return normalizer;
	}
	
	/**
	 * Gets the lag window size. It represents the number of past values in the input vector for the machine learning method.
	 * 
	 * @return the lag window size.
	 */	
	public int getLagWindowSize() {
		return lagWindowSize;
	}
}
