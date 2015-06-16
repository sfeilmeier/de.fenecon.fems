/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.agent.load;

import java.util.List;

import de.fenecon.fems.agent.consumption.ConsumptionField;
import de.fenecon.fems.agent.source.SourceCategory;

/**
 * General agent for a load.
 * 
 * @author Stefan Feilmeier
 */
public abstract class LoadAgent implements Comparable<LoadAgent> {
	/**
	 * Each {@link LoadAgent} has an internal fallback priority in case of equal
	 * Added Values. It is defined on creation of a new object.
	 */
	private static Integer nextPriority = 0;
	/** Internal fallback priority of this agent. */
	private final int priority;

	/**
	 * Creates a new {@link LoadAgent} with a given fallback priority.
	 */
	public LoadAgent() {
		synchronized (nextPriority) {
			this.priority = nextPriority;
			nextPriority++;
		}
	}

	/**
	 * Compares this {@link LoadAgent}s fallback priority to another one's.
	 */
	@Override
	public int compareTo(LoadAgent o) {
		return priority - o.priority;
	}

	/**
	 * Defines the added value of this load with regard to a
	 * {@link SourceCategory}.ordinal().
	 * 
	 * @param timestamp
	 *            the timestamp to query
	 * @return the added value, comparable with {@link SourceCategory}
	 */
	public abstract float getAddedValue(long timestamp);

	/**
	 * Gets a list of {@link ConsumptionField}s, where this load is attached to.
	 * 
	 * @return the list of {@link ConsumptionField}s
	 */
	public abstract List<ConsumptionField> getConsumptionFields();

	/**
	 * Gets the fallback priority.
	 * 
	 * @return the fallback priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Gets the required power of this load.
	 * 
	 * @param timestamp
	 *            the timestamp to query
	 * @return the required power in Watt
	 */
	public abstract double getRequiredPower(long timestamp);

	/**
	 * Returns the SimpleName of this class.
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
