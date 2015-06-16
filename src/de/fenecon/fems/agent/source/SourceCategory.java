/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.agent.source;

import de.fenecon.fems.agent.load.LoadAgent;
import de.fenecon.fems.agent.scheduler.SchedulerAgent;

/**
 * Defines a category of a electricity source. It is used as a group-by
 * parameter for source predictions in {@link SchedulerAgent} and as a measure
 * of priority in the form of "Added Value" of a {@link LoadAgent}.
 * 
 * @author Stefan Feilmeier
 */
public enum SourceCategory {
	PHOTOVOLTAICS, ENERGY_STORAGE_SYSTEM, BLOCK_HEATING_DEVICE, POWER_GRID, DIESEL_GENERATOR
}
