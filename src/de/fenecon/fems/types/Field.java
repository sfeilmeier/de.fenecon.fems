package de.fenecon.fems.types;

public class Field {
	private final String name;
	private final String technicalName;
	
	public Field(String name, String technicalName) {
		this.name = name;
		this.technicalName = technicalName;
	}
	public String toString() {
		return name;
	}
	public String getTechnicalName() {
		return technicalName;
	}
}
