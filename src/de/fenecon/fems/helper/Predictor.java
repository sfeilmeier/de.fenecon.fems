/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.helper;

import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.util.arrayutil.VectorWindow;

/**
 * A wrapper around the Encog objects and methods to calculate the result of a
 * machine learning network.
 * 
 * @author Stefan Feilmeier
 */
public class Predictor {
	/** The preallocated inputs to the network */
	private final MLData input;
	/**
	 * The lag window size of this predictor. It represents the prediction
	 * timeframe of this network
	 */
	private final int lagWindowSize;
	/**
	 * The Encog method used for this network. The object is loaded from a
	 * serialized file using {@link PredictionAgentFactory}
	 */
	private final MLRegression method;
	/**
	 * The Encog normalization helper used for this network. The object is
	 * loaded from a serialized file using {@link PredictionAgentFactory}
	 */
	private final NormalizationHelper normhelper;
	/** The time-series window cache for this method */
	private final VectorWindow window;

	/**
	 * Creates a new Predictor. Use via {@link PredictionAgentFactory}.
	 * 
	 * @param lagWindowSize
	 *            the lag window size
	 * @param method
	 *            the Encog prediction method
	 * @param normhelper
	 *            the Encog normalization helper
	 * @param leadWindowSize
	 *            the lead window size for the time-series window
	 */
	public Predictor(int lagWindowSize, MLRegression method, NormalizationHelper normhelper, int leadWindowSize) {
		this.lagWindowSize = lagWindowSize;
		this.method = method;
		this.normhelper = normhelper;
		this.window = new VectorWindow(leadWindowSize + 1); // allocate window
		this.input = normhelper.allocateInputVector(leadWindowSize + 1); // allocate
																			// input
																			// vector
	}

	/**
	 * Adds a new value to the time-series window and calculates a new
	 * {@link Prediction}.
	 * 
	 * @param value
	 *            the new value
	 * @return the calculated prediction
	 */
	public synchronized Prediction addValueAndPredict(double value) {
		double[] slice = new double[1];
		normhelper.normalizeInputVector(new String[] { String.valueOf(value) }, slice, false);
		window.add(slice);
		while (!window.isReady()) {
			window.add(slice); // if window not full, just interpolate current
								// value => better a bad prediction than none
		}
		window.copyWindow(input.getData(), 0);
		MLData output = method.compute(input);
		String predicted = normhelper.denormalizeOutputVectorToString(output)[0];
		return new Prediction(Double.parseDouble(predicted), lagWindowSize);
	}

	/**
	 * Gets the lag window size. It represents the prediction timeframe of this
	 * network.
	 * 
	 * @return the lag window size
	 */
	public int getLagWindowSize() {
		return lagWindowSize;
	}

	/**
	 * Gets the Encog machine learning network method.
	 * 
	 * @return the Encog method.
	 */
	public MLRegression getMethod() {
		return method;
	}

	@Override
	public String toString() {
		return "PvPredictor [lagWindowSize=" + lagWindowSize + "]";
	}
}
