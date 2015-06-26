/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.agent.source.pv;

import de.fenecon.fems.agent.source.SourceAgent;
import de.fenecon.fems.agent.source.SourceCategory;
import de.fenecon.fems.helper.Field;
import de.fenecon.fems.helper.PredictionAgentImpl;
import de.fenecon.fems.helper.Predictor;

/**
 * Defines a {@link SourceAgent} for a photovoltaic installation.
 * 
 * @author Stefan Feilmeier
 */
public class PvAgent extends PredictionAgentImpl implements SourceAgent {
	/** the field of this pv installation */
	private final PvField field;

	/**
	 * Creates a new PvAgent. Use with {@link PvAgentFactory}.
	 * 
	 * @param field
	 *            the field of this pv installation
	 * @param predictor
	 *            the predictor for this agent
	 */
	public PvAgent(PvField field, Predictor predictor) {
		super(predictor);
		this.field = field;
	}

	@Override
	public Field getField() {
		return field;
	}

	/**
	 * Gets the {@link SourceCategory} "PHOTOVOLTAICS".
	 * 
	 * @return the {@link SourceCategory}
	 */
	@Override
	public SourceCategory getSourceCategory() {
		return SourceCategory.PHOTOVOLTAICS;
	}
}
