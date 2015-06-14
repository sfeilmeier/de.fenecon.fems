package de.fenecon.fems.agent.source.pv;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.fenecon.fems.agent.source.SourceAgent;
import de.fenecon.fems.ess.PvListener;
import de.fenecon.fems.types.Prediction;
import de.fenecon.fems.types.Predictions;
import de.fenecon.fems.types.PvField;
import de.fenecon.fems.types.SourceCategory;

public class PvAgent implements PvListener, SourceAgent {	
	private final PvField field;
	private final Set<PvPredictor> predictors;
	private final ConcurrentHashMap<Long, Predictions> predictions = new ConcurrentHashMap<Long, Predictions>();
	private final ExecutorService executor;
	
	/**
	 * 
	 * 
	 * @param pv
	 * @param predictors
	 */
	public PvAgent(PvField pv, Set<PvPredictor> predictors) {		
		this.field = pv;
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
	public boolean equals(Object obj) {
		if(obj instanceof PvAgent) {
			return this.field.equals(((PvAgent)obj).field);
		}
		return super.equals(obj);
	}
	
	@Override
	public PvField getField() {
		return this.field;
	}

	@Override
	public void pvNotification(long timestamp, double value) {
		//System.out.println("PvPredictionAgent " + toString() + " : new data " + value + " @" + timestamp + " (" + predictions.get(timestamp) + ")");
		addPrediction(timestamp, new Prediction(value, 0)); // add the current value to predictions 
		for(PvPredictor predictor : predictors) {
			Runnable predictionWorker = new PvPredictionWorker(predictor, timestamp, value, this);
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
	public SourceCategory getSourceCategory() {
		return SourceCategory.PHOTOVOLTAICS;
	}

	@Override
	public String toString() {
		return this.field.toString();
	}

	@Override
	public int compareTo(SourceAgent o) {
		if(o instanceof PvAgent) {
			return field.toString().compareTo(((PvAgent)o).field.toString());
		} else {
			return getSourceCategory().compareTo(o.getSourceCategory());
		}
	}
}
