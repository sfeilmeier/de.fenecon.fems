/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.agent.load;

import de.fenecon.fems.agent.scheduler.SchedulerAgent;

/**
 * Defines the actions that can be defined for a {@link LoadAgent} by the
 * {@link SchedulerAgent}.
 * 
 * @author Stefan Feilmeier
 */
public enum LoadAction {
	START, STOP
}
