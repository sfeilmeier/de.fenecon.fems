/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.helper;

/**
 * Holds a prediction value and its lag size, giving an idea about the accuracy
 * of the prediction. The higher the lag, the worse the prediction.
 * 
 * @author Stefan Feilmeier
 */
public class Prediction implements Comparable<Prediction> {
	/**
	 * The lag window size of this prediction. If the prediction was summed up,
	 * this is an average of all base predictions.
	 */
	private final float lagWindowSize;
	/** The value of this prediction */
	private final double value;

	/**
	 * Creates a new Prediction object.
	 * 
	 * @param value
	 *            the value
	 * @param lagWindowSize
	 *            the lag window size
	 */
	public Prediction(double value, float lagWindowSize) {
		this.value = value;
		this.lagWindowSize = lagWindowSize;
	}

	/**
	 * Compare the accuracy (lag window size) of two {@link Prediction}s.
	 */
	@Override
	public int compareTo(Prediction o) {
		return (int) (lagWindowSize - o.lagWindowSize);
	}

	/**
	 * Gets the lag window size. Smaller is more accurate.
	 * 
	 * @return the lag window size
	 */
	public float getLagWindowSize() {
		return lagWindowSize;
	}

	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	@Override
	public String toString() {
		return String.format("[Value=%.2f, Lag=%.1f]", value, lagWindowSize);
	}
}
