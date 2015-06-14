package de.fenecon.fems.helper;

public interface PredictionWorkerCallback {

	/**
	 * Add a new prediction. If a prediction for the given timestamp already exists,
	 * the prediction with the shortest lag (= highest accuracy) wins
	 * 
	 * @param timestamp
	 * @param prediction
	 */
	public void addPrediction(long timestamp, Prediction prediction);
}
