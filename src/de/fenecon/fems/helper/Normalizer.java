package de.fenecon.fems.helper;

public class Normalizer {

	private final double dataHigh;
	private final double dataLow;
	private final double normalizedHigh;
	private final double normalizedLow;

	public double getDataHigh() {
		return dataHigh;
	}

	public double getDataLow() {
		return dataLow;
	}

	public double getNormalizedHigh() {
		return normalizedHigh;
	}

	public double getNormalizedLow() {
		return normalizedLow;
	}

	/**
	 * Constructs the normalization utility for a given normalization range
	 * 
	 * @param dataHigh
	 *            the high value for the input data.
	 * @param dataLow
	 *            the low value for the input data.
	 * @param dataHigh
	 *            the high value for the normalized data.
	 * @param dataLow
	 *            the low value for the normalized data.
	 */
	public Normalizer(double dataLow, double dataHigh, double normalizedLow, double normalizedHigh) {
		this.dataHigh = dataHigh;
		this.dataLow = dataLow;
		this.normalizedHigh = normalizedHigh;
		this.normalizedLow = normalizedLow;
	}

	/**
	 * Normalizes a value
	 * 
	 * @param value
	 *            the value to normalize
	 * @return the normalized value
	 */
	public double normalize(double value) {
		if (value > dataHigh)
			return normalizedHigh;
		if (value < dataLow)
			return normalizedLow;
		return ((value - dataLow) / (dataHigh - dataLow)) * (normalizedHigh - normalizedLow) + normalizedLow;
	}

	@Override
	public String toString() {
		return "Normalizer [dataHigh=" + dataHigh + ", dataLow=" + dataLow + ", normalizedHigh=" + normalizedHigh
				+ ", normalizedLow=" + normalizedLow + "]";
	}

	/**
	 * Denormalize a value
	 * 
	 * @param value
	 *            the normalized value
	 * @return the real value.
	 */
	public double denormalize(double value) {
		if (value < normalizedLow)
			return dataLow;
		if (value > normalizedHigh)
			return dataHigh;
		return ((dataLow - dataHigh) * value - normalizedHigh * dataLow + dataHigh * normalizedLow)
				/ (normalizedLow - normalizedHigh);
	}
}
