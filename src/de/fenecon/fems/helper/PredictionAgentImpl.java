/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import de.fenecon.fems.FemsConstants;
import de.fenecon.fems.agent.consumption.ConsumptionAgentFactory;
import de.fenecon.fems.agent.source.pv.PvAgentFactory;
import de.fenecon.fems.ess.EssListener;

/**
 * General implementation of a {@link PredictionAgent}. It manages a
 * {@link Predictor}, which is carrying out the actual prediction as soon as a
 * new value is received via {@link EssListener}..
 * 
 * @author Stefan Feilmeier
 */
public abstract class PredictionAgentImpl implements EssListener, PredictionAgent {
	/** Collection of prediction results per timestamp */
	private final ConcurrentHashMap<Long, ConcurrentSkipListSet<Prediction>> predictionsPerTimestamp = new ConcurrentHashMap<Long, ConcurrentSkipListSet<Prediction>>();

	/**
	 * The Predictor for this field
	 */
	private final Predictor predictor;

	/**
	 * Creates a new {@link PredictionAgentImpl}. Use with an appropriate
	 * implementation of {@link PredictionAgentFactory}, like
	 * {@link ConsumptionAgentFactory} or {@link PvAgentFactory}.
	 * 
	 * @param predictor
	 *            the predictor for this agent
	 */
	public PredictionAgentImpl(Predictor predictor) {
		this.predictor = predictor;
	}

	/**
	 * Add a new prediction; overwrite existing prediction only if the lead of
	 * the new {@link Prediction} is smaller (more accurate).
	 * 
	 * @param timestamp
	 *            the timestamp of the prediction
	 * @param prediction
	 *            the prediction
	 */
	public void addPrediction(long timestamp, Prediction prediction) {
		if (prediction == null)
			return; // not accepting invalid predictions
		predictionsPerTimestamp.putIfAbsent(timestamp, new ConcurrentSkipListSet<Prediction>());
		ConcurrentSkipListSet<Prediction> predictions = predictionsPerTimestamp.get(timestamp);
		predictions.add(prediction);
	}

	/**
	 * Gets the best prediction (= the prediction with the highest accuracy, the
	 * smallest lead window size) from the list.
	 * 
	 * @return
	 */
	@Override
	public Prediction getBestPredictionAtTimestamp(long timestamp) {
		// use internal sorting of ConcurrentSkipListSet to return the most
		// accurate prediction
		ConcurrentSkipListSet<Prediction> predictions = predictionsPerTimestamp.get(timestamp);
		try {
			return predictions.first();
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public ConcurrentSkipListSet<Prediction> getPredictionsAtTimestamp(long timestamp) {
		ConcurrentSkipListSet<Prediction> predictions = predictionsPerTimestamp.get(timestamp);
		if(predictions == null) {
			return new ConcurrentSkipListSet<Prediction>();
		}
		return predictions;
	}
	
	/**
	 * Remove old predictions (elder than current timestamp) from the cache.
	 * 
	 * @param timestamp
	 *            the current timestamp
	 */
	private void clearOldPredictions(long timestamp) {
		for (Long oldTimestamp : predictionsPerTimestamp.keySet()) {
			if (oldTimestamp < timestamp) {
				try {
					predictionsPerTimestamp.remove(oldTimestamp);
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

	/**
	 * On arrival of a new value from {@link EssListener}, add the new value as
	 * the final prediction (lead = 0) and execute the predictor with this new
	 * value.
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
		HashMap<Integer, Prediction> predictionsPerLead = predictor.addValueAndPredict(value);
		for(Map.Entry<Integer, Prediction> predictionPerLead : predictionsPerLead.entrySet()) {
			long futureTimestamp = timestamp + FemsConstants.SLICE_SECONDS * predictionPerLead.getKey();
			addPrediction(futureTimestamp, predictionPerLead.getValue());
		}
		clearOldPredictions(timestamp);
	}

	@Override
	public String toString() {
		return getField().getName();
	}
}
