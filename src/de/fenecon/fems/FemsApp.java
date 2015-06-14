package de.fenecon.fems;

import org.encog.Encog;

import de.fenecon.fems.agent.scheduler.SchedulerAgent;
import de.fenecon.fems.agent.source.pv.PvAgent;
import de.fenecon.fems.agent.source.pv.PvAgentFactory;
import de.fenecon.fems.ess.prohybrid.ProHybridSimulator;
import de.fenecon.fems.ess.prohybrid.ProHybridSimulatorFactory;

public class FemsApp {

	public static void main(String[] args) throws Exception {
		PvAgent pv1PredictionAgent = PvAgentFactory.create(FemsConstants.PV1);
		PvAgent pv2PredictionAgent = PvAgentFactory.create(FemsConstants.PV2);
		ProHybridSimulator proHybridSim = ProHybridSimulatorFactory.create();
		proHybridSim.addPvListener(pv1PredictionAgent);
		proHybridSim.addPvListener(pv2PredictionAgent);
		
		SchedulerAgent scheduler = new SchedulerAgent();
		scheduler.addSourceAgent(pv1PredictionAgent);
		scheduler.addSourceAgent(pv2PredictionAgent);
		
		
		//Pv1Classification c = new Pv1Classification();
		//c.run();
		//Pv1Classification2.run();
		//Pv1Regression.run();
		//XOROnline.run();
		
		Encog.getInstance().shutdown();
	}
}
