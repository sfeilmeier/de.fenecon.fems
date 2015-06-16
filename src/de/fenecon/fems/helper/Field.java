/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.helper;

/**
 * General definition of a field identifier.
 * 
 * @author Stefan Feilmeier
 */
public class Field implements Comparable<Field> {
	/** the short name of this field */
	private final String name;
	/** the technical name of this field */
	private final String technicalName;

	/**
	 * Creates a specific identifier for this field.
	 * 
	 * @param name
	 *            the short name of this field
	 * @param technicalName
	 *            the technical name of this field
	 */
	public Field(String name, String technicalName) {
		this.name = name;
		this.technicalName = technicalName;
	}

	/**
	 * Compare this fields short name to the other fields short name.
	 */
	@Override
	public int compareTo(Field field) {
		return name.compareTo(field.name);
	}

	/**
	 * If the other object is a "Field", tests if this fields short name is
	 * equel to the other fields short name. Otherwise call general
	 * {@link Object#equals()} function.
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Field) {
			return name.equals(((Field) o).name);
		} else {
			return super.equals(o);
		}
	}

	/**
	 * Gets the short name.
	 * 
	 * @return the short name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the technical name.
	 * 
	 * @return the technical name
	 */
	public String getTechnicalName() {
		return technicalName;
	}

	public String toString() {
		return name;
	}
}
