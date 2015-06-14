package de.fenecon.fems.helper;

public interface PredictionAgent extends Comparable<PredictionAgent> {
	
	public Prediction getBestPredictionAtTimestamp(Long timestamp);
	
	public Field getField();
	
}
