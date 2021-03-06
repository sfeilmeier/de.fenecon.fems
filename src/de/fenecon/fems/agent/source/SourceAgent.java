/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.agent.source;

import de.fenecon.fems.helper.PredictionAgent;

/**
 * General agent for a power source.
 * 
 * @author Stefan Feilmeier
 */
public interface SourceAgent extends PredictionAgent {

	/**
	 * Gets the {@link SourceCategory} of this agent.
	 * 
	 * @return the {@link SourceCategory}
	 */
	public SourceCategory getSourceCategory();

}
