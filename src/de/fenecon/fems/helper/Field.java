package de.fenecon.fems.helper;

public class Field implements Comparable<Field> {
	private final String name;
	private final String technicalName;
	
	public Field(String name, String technicalName) {
		this.name = name;
		this.technicalName = technicalName;
	}
	public String toString() {
		return name;
	}
	public String getName() {
		return name;
	}
	public String getTechnicalName() {
		return technicalName;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Field) {
			return name.equals(((Field) o).name); 
		} else {
			return super.equals(o);
		}
	}
	
	@Override
	public int compareTo(Field field) {
		return name.compareTo(field.name);
	}
}
