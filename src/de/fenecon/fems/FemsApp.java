/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems;

import de.fenecon.fems.agent.consumption.ConsumptionAgent;
import de.fenecon.fems.agent.consumption.ConsumptionAgentFactory;
import de.fenecon.fems.agent.load.HeatingDeviceLoadAgent;
import de.fenecon.fems.agent.load.LoadAgent;
import de.fenecon.fems.agent.scheduler.SchedulerAgent;
import de.fenecon.fems.agent.source.SourceAgent;
import de.fenecon.fems.agent.source.grid.GridAgent;
import de.fenecon.fems.agent.source.grid.GridAgentFactory;
import de.fenecon.fems.agent.source.pv.PvAgent;
import de.fenecon.fems.agent.source.pv.PvAgentFactory;
import de.fenecon.fems.ess.prohybrid.ProHybridSimulator;
import de.fenecon.fems.ess.prohybrid.ProHybridSimulatorFactory;

/**
 * Creates a complete environment for load scheduling.
 * 
 * The {@link FemsApp} instantiates all {@link SourceAgent}s,
 * {@link ConsumptionAgent}s and {@link LoadAgent}s and starts a complete
 * simulation.
 * 
 * @author Stefan Feilmeier
 *
 */
public class FemsApp {

	public static void main(String[] args) throws Exception {
		// Create Source Agents
		PvAgent pv1Agent = PvAgentFactory.create(FemsConstants.PV1);
		PvAgent pv2Agent = PvAgentFactory.create(FemsConstants.PV2);
		GridAgent gridPh1Agent = GridAgentFactory.create(FemsConstants.GRID_PHASE1);
		GridAgent gridPh2Agent = GridAgentFactory.create(FemsConstants.GRID_PHASE2);
		GridAgent gridPh3Agent = GridAgentFactory.create(FemsConstants.GRID_PHASE3);

		// Create Consumption Agents
		ConsumptionAgent consumptionPh1Agent = ConsumptionAgentFactory.create(FemsConstants.CONSUMPTION_PHASE1);
		ConsumptionAgent consumptionPh2Agent = ConsumptionAgentFactory.create(FemsConstants.CONSUMPTION_PHASE2);
		ConsumptionAgent consumptionPh3Agent = ConsumptionAgentFactory.create(FemsConstants.CONSUMPTION_PHASE3);

		// Create Load Agents
		LoadAgent heatingDeviceLoadAgent = new HeatingDeviceLoadAgent();

		// Create Scheduler Agent
		SchedulerAgent scheduler = new SchedulerAgent();
		scheduler.addSourceAgent(pv1Agent);
		scheduler.addSourceAgent(pv2Agent);
		scheduler.addSourceAgent(gridPh1Agent);
		scheduler.addSourceAgent(gridPh2Agent);
		scheduler.addSourceAgent(gridPh3Agent);
		scheduler.addConsumptionAgent(consumptionPh1Agent);
		scheduler.addConsumptionAgent(consumptionPh2Agent);
		scheduler.addConsumptionAgent(consumptionPh3Agent);
		scheduler.addLoadAgent(heatingDeviceLoadAgent);

		// Initialize Simulator
		ProHybridSimulator proHybridSim = ProHybridSimulatorFactory.create("fems20");
		proHybridSim.addListener(pv1Agent);
		proHybridSim.addListener(pv2Agent);
		proHybridSim.addListener(consumptionPh1Agent);
		proHybridSim.addListener(consumptionPh2Agent);
		proHybridSim.addListener(consumptionPh3Agent);
	}
}
