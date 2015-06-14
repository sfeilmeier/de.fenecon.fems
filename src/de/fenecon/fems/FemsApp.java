package de.fenecon.fems;

import de.fenecon.fems.agent.consumption.ConsumptionAgentImpl;
import de.fenecon.fems.agent.consumption.ConsumptionAgentFactory;
import de.fenecon.fems.agent.scheduler.SchedulerAgent;
import de.fenecon.fems.agent.source.grid.GridAgent;
import de.fenecon.fems.agent.source.grid.GridAgentFactory;
import de.fenecon.fems.agent.source.pv.PvAgent;
import de.fenecon.fems.agent.source.pv.PvAgentFactory;
import de.fenecon.fems.ess.prohybrid.ProHybridSimulator;
import de.fenecon.fems.ess.prohybrid.ProHybridSimulatorFactory;

public class FemsApp {

	public static void main(String[] args) throws Exception {
		// Create Source Agents
		PvAgent pv1Agent = PvAgentFactory.create(FemsConstants.PV1);
		PvAgent pv2Agent = PvAgentFactory.create(FemsConstants.PV2);
		GridAgent gridPh1Agent = GridAgentFactory.create(FemsConstants.GRID_PHASE1);
		GridAgent gridPh2Agent = GridAgentFactory.create(FemsConstants.GRID_PHASE2);
		GridAgent gridPh3Agent = GridAgentFactory.create(FemsConstants.GRID_PHASE3);
		
		// Create Consumption Agents
		ConsumptionAgentImpl consumptionPh1Agent = ConsumptionAgentFactory.create(FemsConstants.CONSUMPTION_PHASE1);
		ConsumptionAgentImpl consumptionPh2Agent = ConsumptionAgentFactory.create(FemsConstants.CONSUMPTION_PHASE2);
		ConsumptionAgentImpl consumptionPh3Agent = ConsumptionAgentFactory.create(FemsConstants.CONSUMPTION_PHASE3);
		
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
		
		// Initialize Simulator
		ProHybridSimulator proHybridSim = ProHybridSimulatorFactory.create();
		proHybridSim.addListener(pv1Agent);
		proHybridSim.addListener(pv2Agent);
		proHybridSim.addListener(consumptionPh1Agent);
		proHybridSim.addListener(consumptionPh2Agent);
		proHybridSim.addListener(consumptionPh3Agent);
		
		//Encog.getInstance().shutdown();
	}
}
