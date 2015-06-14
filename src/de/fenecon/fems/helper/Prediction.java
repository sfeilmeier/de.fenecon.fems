package de.fenecon.fems.helper;

public class Prediction implements Comparable<Prediction> {
	private final double value;
	private final float lagWindowSize; // average
	
	public Prediction(double value, float lagWindowSize) {
		this.value = value;
		this.lagWindowSize = lagWindowSize;
	}
	
	public double getValue() {
		return value;
	}

	public float getLagWindowSize() {
		return lagWindowSize;
	}

	@Override
	public String toString() {
		return String.format("Prediction [value=%.2f, lag=%.1f]", value, lagWindowSize);
	}

	@Override
	public int compareTo(Prediction o) {
		return (int)(lagWindowSize - o.lagWindowSize);
	}
}
