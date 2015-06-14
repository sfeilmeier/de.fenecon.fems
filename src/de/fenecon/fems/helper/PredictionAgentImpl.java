package de.fenecon.fems.helper;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.fenecon.fems.ess.EssListener;

public abstract class PredictionAgentImpl implements EssListener, PredictionWorkerCallback, PredictionAgent {	
	
	private final Set<Predictor> predictors;
	private final ConcurrentHashMap<Long, Predictions> predictions = new ConcurrentHashMap<Long, Predictions>();
	private final ExecutorService executor;
	
	/**
	 * 
	 * 
	 * @param predictors
	 */
	public PredictionAgentImpl(Set<Predictor> predictors) {		
		this.predictors = predictors;
		executor = Executors.newFixedThreadPool(predictors.size()); // initialize a thread pool with one slot per predictor
	}
	
	/**
	 * Add a new prediction; overwrite existing prediction only if the predicted lag was shorter (= more accurate)
	 * 
	 * @param timestamp
	 * @param prediction
	 */
	public void addPrediction(long timestamp, Prediction prediction) {
		if(prediction == null) return; // not accepting invalid predictions
		predictions.putIfAbsent(timestamp, new Predictions());
		Predictions existingPredictions = predictions.get(timestamp);
		existingPredictions.addPrediction(prediction);
	}
	
	private void clearOldPredictions(long timestamp) {
		for(Long oldTimestamp : predictions.keySet()) {
			if(oldTimestamp < timestamp) {
				try { predictions.remove(oldTimestamp); } catch (NullPointerException e) {;}
			}
		}
	}

	@Override
	public void newValue(long timestamp, double value) {
		//System.out.println("Agent " + toString() + " : new data " + value + " @" + timestamp + " (" + predictions.get(timestamp) + ")");
		addPrediction(timestamp, new Prediction(value, 0)); // add the current value to predictions 
		for(Predictor predictor : predictors) {
			Runnable predictionWorker = new PredictionWorker(predictor, timestamp, value, this);
			executor.execute(predictionWorker);
		}
		clearOldPredictions(timestamp);
	}

	@Override
	public Prediction getBestPredictionAtTimestamp(Long timestamp) {
		Predictions timestampPredictions = predictions.get(timestamp);
		if(timestampPredictions == null) {
			return null;
		} else {
			return timestampPredictions.getBestPrediction();
		}
	}

	@Override
	public String toString() {
		return getField().getName();
	}

	@Override
	public int compareTo(PredictionAgent o) {
		return getField().compareTo(o.getField());
	}
}
