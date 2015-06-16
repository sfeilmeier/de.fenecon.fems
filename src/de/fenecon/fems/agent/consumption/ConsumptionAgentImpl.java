/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.agent.consumption;

import java.util.Set;

import de.fenecon.fems.helper.PredictionAgentImpl;
import de.fenecon.fems.helper.Predictor;

/**
 * Implementation of a {@link ConsumptionAgent}.
 * 
 * @author Stefan Feilmeier
 */
public class ConsumptionAgentImpl extends PredictionAgentImpl implements ConsumptionAgent {

	/** The field identifier of this consumption. */
	protected final ConsumptionField field;

	/**
	 * Creates a new instance. Preferable use with
	 * {@link ConsumptionAgentFactory}.
	 * 
	 * @param field
	 *            the field identifier of this consumption
	 * @param predictors
	 *            the predictors used by this agent
	 */
	public ConsumptionAgentImpl(ConsumptionField field, Set<Predictor> predictors) {
		super(predictors);
		this.field = field;
	}

	/**
	 * Gets the field identifier.
	 */
	@Override
	public ConsumptionField getField() {
		return field;
	}

}
