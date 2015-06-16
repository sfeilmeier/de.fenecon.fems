/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.helper;

import de.fenecon.fems.FemsConstants;

/**
 * Worker thread to execute one specific calculation of a
 * {@link PredictionAgent}.
 * 
 * @author Stefan Feilmeier
 */
public class PredictionWorker implements Runnable {
	/** The callback object, that is notified about the calculation result */
	private final PredictionWorkerCallback callback;
	/** The current timestamp of the prediction */
	private final long currentTimestamp;
	/** The predictor to use */
	private final Predictor predictor;
	/** The new value for this timestamp */
	private final double value;

	/**
	 * Creates a new {@link PredictionWorker}. Usually called by the
	 * {@link PredictionAgentImpl} on notification about a new value.
	 * 
	 * @param predictor
	 *            the predictor
	 * @param currentTimestamp
	 *            the current timestamp of the value
	 * @param value
	 *            the new value
	 * @param callback
	 *            the callback object
	 */
	public PredictionWorker(Predictor predictor, long currentTimestamp, double value, PredictionWorkerCallback callback) {
		this.predictor = predictor;
		this.currentTimestamp = currentTimestamp;
		this.value = value;
		this.callback = callback;
	}

	/**
	 * Executes the prediction calculation using the {@link Predictor} and
	 * notifies the {@link PredictionWorkerCallback} about the result.
	 */
	@Override
	public void run() {
		try {
			Prediction prediction = predictor.addValueAndPredict(value);
			long newTimestamp = currentTimestamp + predictor.getLagWindowSize() * FemsConstants.SLICE_SECONDS;
			callback.addPrediction(newTimestamp, prediction);
		} catch (Exception e) {
			System.out.println("Exception. Worker not successful: " + e.getMessage());
		}

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
