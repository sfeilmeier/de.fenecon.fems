package de.fenecon.fems.types;

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
		return "Prediction [value=" + value + ", lagWindowSize="
				+ lagWindowSize + "]";
	}

	@Override
	public int compareTo(Prediction o) {
		return (int)(lagWindowSize - o.lagWindowSize);
	}
}
