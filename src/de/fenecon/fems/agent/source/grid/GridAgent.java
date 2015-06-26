/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.agent.source.grid;

import java.util.concurrent.ConcurrentSkipListSet;

import de.fenecon.fems.agent.source.SourceAgent;
import de.fenecon.fems.agent.source.SourceCategory;
import de.fenecon.fems.helper.Field;
import de.fenecon.fems.helper.Prediction;
import de.fenecon.fems.helper.PredictionAgent;

/**
 * Defines the power grid as a source of electricity.
 * 
 * @author Stefan Feilmeier
 */
public class GridAgent implements SourceAgent {
	/** the field descriptor defining the phase of this source */
	private final GridField field;

	/**
	 * Creates a new {@link GridAgent} defined by its {@link GridField}.
	 * 
	 * @param field
	 *            the connected field
	 */
	public GridAgent(GridField field) {
		this.field = field;
	}

	@Override
	public int compareTo(PredictionAgent o) {
		return this.field.compareTo(o.getField());
	}

	/**
	 * Always returns an infinity value for prediction.
	 * 
	 * @return the infinite prediction
	 */
	@Override
	public Prediction getBestPredictionAtTimestamp(long timestamp) {
		return new Prediction(Double.MAX_VALUE, 0);
	}

	@Override
	public Field getField() {
		return field;
	}

	@Override
	public ConcurrentSkipListSet<Prediction> getPredictionsAtTimestamp(long timestamp) {
		ConcurrentSkipListSet<Prediction> predictions = new ConcurrentSkipListSet<Prediction>();
		predictions.add(getBestPredictionAtTimestamp(timestamp));
		return predictions;
	}

	/**
	 * Gets the {@link SourceCategory} "POWER_GRID".
	 * 
	 * @return the {@link SourceCategory}
	 */
	@Override
	public SourceCategory getSourceCategory() {
		return SourceCategory.POWER_GRID;
	}

	@Override
	public String toString() {
		return field.getName();
	}
}
