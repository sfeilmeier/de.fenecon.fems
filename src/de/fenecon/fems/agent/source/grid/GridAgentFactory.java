/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.agent.source.grid;

/**
 * Factory for {@link GridAgent}s.
 * 
 * @author Stefan Feilmeier
 */
public class GridAgentFactory {

	/**
	 * Create a new {@link GridAgent} connected to a {@link GridField}.
	 * 
	 * @param field
	 *            the field of the grid phase
	 * @return the new {@link GridAgent}
	 */
	public static GridAgent create(GridField field) {
		return new GridAgent(field);
	}
}
