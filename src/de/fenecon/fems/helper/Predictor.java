package de.fenecon.fems.helper;

import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.util.arrayutil.VectorWindow;

public class Predictor {
	private final int lagWindowSize;
	private final MLRegression method;
	
	private final NormalizationHelper normhelper;
	private final VectorWindow window;
	private final MLData input; 
	
	public Predictor(int lagWindowSize, MLRegression method, NormalizationHelper normhelper, int leadWindowSize) {
		this.lagWindowSize = lagWindowSize;
		this.method = method;
		this.normhelper = normhelper;
		this.window = new VectorWindow(leadWindowSize + 1); // allocate window
		this.input = normhelper.allocateInputVector(leadWindowSize + 1); // allocate input vector
	}
	public MLRegression getMethod() {
		return method;
	}
	public synchronized Prediction addValueAndPredict(double value) {
		double[] slice = new double[1];
		normhelper.normalizeInputVector(new String[]{ String.valueOf(value) }, slice, false);
		window.add(slice);
		while(!window.isReady()) {
			window.add(slice); // if window not full, just interpolate current value => better a bad prediction than none
		}
		window.copyWindow(input.getData(), 0);
		MLData output = method.compute(input);
		String predicted = normhelper.denormalizeOutputVectorToString(output)[0];
		return new Prediction(Double.parseDouble(predicted), lagWindowSize);
	}
	public int getLagWindowSize() {
		return lagWindowSize;
	}
	@Override
	public String toString() {
		return "PvPredictor [lagWindowSize=" + lagWindowSize + "]";
	}
}
