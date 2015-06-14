package de.fenecon.fems.agent.source.grid;


public class GridAgentFactory {
	public static GridAgent create(GridField field) {
		return new GridAgent(field);
	}
}
