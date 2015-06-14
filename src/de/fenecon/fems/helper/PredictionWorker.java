package de.fenecon.fems.helper;

import de.fenecon.fems.FemsConstants;

public class PredictionWorker implements Runnable {
	private final Predictor predictor;
	private final long currentTimestamp;
	private final double value;
	private final PredictionWorkerCallback callback;
	
	public PredictionWorker(Predictor predictor, long currentTimestamp, double value, PredictionWorkerCallback callback) {
		this.predictor = predictor;
		this.currentTimestamp = currentTimestamp;
		this.value = value;
		this.callback = callback;
	}

	@Override
	public void run() {
		try {
			Prediction prediction = predictor.addValueAndPredict(value);
			long newTimestamp = currentTimestamp + predictor.getLagWindowSize() * FemsConstants.SLICE_SECONDS;
			callback.addPrediction(newTimestamp, prediction);
		} catch (Exception e) {
			System.out.println("Exception. Worker not successful: " + e.getMessage());
		}
		
		try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }

	}
}
