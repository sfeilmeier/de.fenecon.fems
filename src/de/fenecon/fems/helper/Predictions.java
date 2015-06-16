/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.helper;

import java.util.concurrent.ConcurrentSkipListSet;

import de.fenecon.fems.FemsConstants;

/**
 * Holds a list of {@link Prediction}s.
 * 
 * @author Stefan Feilmeier
 */
public class Predictions {
	/** The list of {@link Prediction}s */
	private final ConcurrentSkipListSet<Prediction> predictions;

	/**
	 * Creates a new empty list of {@link Prediction}s
	 */
	public Predictions() {
		predictions = new ConcurrentSkipListSet<Prediction>();
	}

	/**
	 * Creates a new list of {@link Prediction}s, initializing with the given
	 * Prediction as its first entry.
	 * 
	 * @param prediction
	 *            the given prediction
	 */
	public Predictions(Prediction prediction) {
		this();
		addPrediction(prediction);
	}

	/**
	 * Adds a new prediction to the list.
	 * 
	 * @param newPrediction
	 *            the new prediction
	 */
	public void addPrediction(Prediction newPrediction) {
		predictions.add(newPrediction);
	}

	/**
	 * Gets the best prediction (= the prediction with the highest accuracy, the
	 * smallest lag) from the list.
	 * 
	 * @return
	 */
	public Prediction getBestPrediction() {
		// use internal sorting of ConcurrentSkipListSet to return the most
		// accurate prediction
		return predictions.first();
	}

	/**
	 * Gets the predictions of the internal list.
	 * 
	 * @return the predictions
	 */
	public ConcurrentSkipListSet<Prediction> getPredictions() {
		return predictions;
	}

	@Override
	public synchronized String toString() {
		StringBuilder builder = new StringBuilder("Predictions [");
		String split = "";
		for (Prediction prediction : predictions) {
			builder.append(split);
			builder.append(prediction.getLagWindowSize() * FemsConstants.SLICE_SECONDS + ":");
			builder.append(String.format("%.2f", prediction.getValue()));
			split = ", ";
		}
		builder.append("]");
		return builder.toString();
	}
}
