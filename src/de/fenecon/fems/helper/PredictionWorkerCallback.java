/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.helper;

/**
 * Link between {@link PredictionWorker} and the object that needs to be
 * notified on finishing the calculation of a prediction.
 * 
 * @author Stefan Feilmeier
 */
public interface PredictionWorkerCallback {

	/**
	 * Add a new prediction; overwrite existing prediction only if the lag of
	 * the new {@link Prediction} is smaller (more accurate).
	 * 
	 * @param timestamp
	 *            the timestamp of the prediction
	 * @param prediction
	 *            the prediction
	 */
	public void addPrediction(long timestamp, Prediction prediction);
}
