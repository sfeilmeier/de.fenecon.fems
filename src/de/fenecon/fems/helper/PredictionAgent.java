/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.helper;

/**
 * General definition of a {@link PredictionAgent}
 * 
 * @author Stefan Feilmeier
 */
public interface PredictionAgent extends Comparable<PredictionAgent> {

	/**
	 * Gets the best prediction for a given timestamp.
	 * 
	 * @param timestamp
	 *            the timestamp
	 * @return the best prediction
	 */
	public Prediction getBestPredictionAtTimestamp(Long timestamp);

	/**
	 * Get the field descriptor of this agent.
	 * 
	 * @return the field descriptor
	 */
	public Field getField();

	/**
	 * Gets all available predictions for a given Timestamp.
	 * 
	 * @param timestamp
	 *            the timestamp
	 * @return the predictions
	 */
	public Predictions getPredictionsAtTimestamp(Long timestamp);

}
