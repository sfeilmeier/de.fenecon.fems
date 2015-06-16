/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.helper;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.fenecon.fems.FemsConstants;
import de.fenecon.fems.agent.consumption.ConsumptionAgentFactory;
import de.fenecon.fems.agent.source.pv.PvAgentFactory;
import de.fenecon.fems.ess.EssListener;

/**
 * General implementation of a {@link PredictionAgent}. It manages a set of
 * {@link Predictor}s, which are queried as soon as a new value is received via
 * {@link EssListener}. The calculation of predictions is handled by separate
 * {@link PredictionWorker}s and the results sent back to this object via
 * {@link PredictionWorkerCallback}.
 * 
 * @author Stefan Feilmeier
 */
public abstract class PredictionAgentImpl implements EssListener, PredictionWorkerCallback, PredictionAgent {
	private final ExecutorService executor;
	/** Collection of prediction results per timestamp */
	private final ConcurrentHashMap<Long, Predictions> predictions = new ConcurrentHashMap<Long, Predictions>();

	/**
	 * Set of predictors. One {@link Predictor} per slice as defined in
	 * {@link FemsConstants}
	 */
	private final Set<Predictor> predictors;

	/**
	 * Creates a new {@link PredictionAgentImpl}. Use with an appropriate
	 * implementation of {@link PredictionAgentFactory}, like
	 * {@link ConsumptionAgentFactory} or {@link PvAgentFactory}.
	 * 
	 * @param predictors
	 *            the predictors for this agent
	 */
	public PredictionAgentImpl(Set<Predictor> predictors) {
		this.predictors = predictors;
		// initialize a thread pool with one slot per predictor
		executor = Executors.newFixedThreadPool(predictors.size());
	}

	@Override
	public void addPrediction(long timestamp, Prediction prediction) {
		if (prediction == null)
			return; // not accepting invalid predictions
		predictions.putIfAbsent(timestamp, new Predictions());
		Predictions existingPredictions = predictions.get(timestamp);
		existingPredictions.addPrediction(prediction);
	}

	/**
	 * Remove old predictions (elder than current timestamp) from the cache.
	 * 
	 * @param timestamp
	 *            the current timestamp
	 */
	private void clearOldPredictions(long timestamp) {
		for (Long oldTimestamp : predictions.keySet()) {
			if (oldTimestamp < timestamp) {
				try {
					predictions.remove(oldTimestamp);
				} catch (NullPointerException e) {
					;
				}
			}
		}
	}

	/**
	 * Compare the {@link Field} descriptor of two {@link PredictionAgent}s.
	 */
	@Override
	public int compareTo(PredictionAgent o) {
		return getField().compareTo(o.getField());
	}

	@Override
	public Prediction getBestPredictionAtTimestamp(Long timestamp) {
		Predictions timestampPredictions = predictions.get(timestamp);
		if (timestampPredictions == null) {
			return null;
		} else {
			return timestampPredictions.getBestPrediction();
		}
	}

	@Override
	public Predictions getPredictionsAtTimestamp(Long timestamp) {
		return predictions.get(timestamp);
	}

	/**
	 * On arrival of a new value from {@link EssListener}, add the new value as
	 * the final prediction (lag = 0) and execute all predictors with this new
	 * value using {@link PredictionWorker}s. The result will be sent to
	 * {@link PredictionAgentImpl#addPrediction()} via
	 * {@link PredictionWorkerCallback}.
	 *
	 * @param timestamp
	 *            the timestamp of the value
	 * @param value
	 *            the value
	 */
	@Override
	public void newValue(long timestamp, double value) {
		// add the current value to predictions
		addPrediction(timestamp, new Prediction(value, 0));
		// start the workers
		for (Predictor predictor : predictors) {
			Runnable predictionWorker = new PredictionWorker(predictor, timestamp, value, this);
			executor.execute(predictionWorker);
		}
		clearOldPredictions(timestamp);
	}

	@Override
	public String toString() {
		return getField().getName();
	}
}
