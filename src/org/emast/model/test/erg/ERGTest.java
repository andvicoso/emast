package org.emast.model.test.erg;

import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.AlgorithmFactory;
import org.emast.model.algorithm.controller.ERGLearningBlockEachBadExp;
import org.emast.model.algorithm.iteration.rl.DynaQ;
import org.emast.model.algorithm.iteration.rl.QLearning;
import org.emast.model.algorithm.iteration.rl.ReinforcementLearning;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.problem.ProblemFactory;
import org.emast.model.test.BatchTest;
import org.emast.model.test.Test;
import org.emast.model.test.erg.antenna.AntennaExamples;
import org.emast.model.test.erg.generic.GenericERGProblemFactory;
import org.emast.util.CollectionsUtils;
import org.emast.util.ProblemsCLI;

/**
 * 
 * @author anderson
 */
public class ERGTest {

	private static Problem<ERG> createFromCLI() {
		ProblemFactory factory = GenericERGProblemFactory.createDefaultFactory();
		return new ProblemsCLI(factory).run();
	}

	private static AlgorithmFactory<ERG, ?> createAlgorithmFactory() {
		return new AlgorithmFactory() {
			@Override
			public Algorithm create() {
				return new ERGLearningBlockEachBadExp(createAlgorithm());
			}
		};
	}

	private static ReinforcementLearning<ERG> createAlgorithm() {
		return new QLearning<ERG>();
	}

	public static void main(final String[] pArgs) {
		// AntennaCoverageProblemFactory.createDefaultFactory();
		Problem<ERG> p =  createFromCLI();//AntennaExamples.getSMC13();//
		Test test = new Test(p, createAlgorithmFactory());
		test.run(CollectionsUtils.asMap(ReinforcementLearning.AGENT_NAME, 0));
	}
}