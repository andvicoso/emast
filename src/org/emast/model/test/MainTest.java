package org.emast.model.test;

import java.util.Collections;
import java.util.Map;

import org.emast.model.algorithm.iteration.ValueIteration;
import org.emast.model.algorithm.iteration.rl.QLearning;
import org.emast.model.algorithm.iteration.rl.ReinforcementLearning;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;
import org.emast.model.test.erg.AlgorithmTest;
import org.emast.model.test.erg.ERGTest;
import org.emast.util.CollectionsUtils;
import org.emast.util.PolicyUtils;
import org.emast.view.ui.cli.ProblemsCLI;

/**
 * 
 * @author anderson
 */
@SuppressWarnings("rawtypes")
public class MainTest {

	public static void main(final String[] pArgs) {
		// int count = 0;
		Problem prob = ProblemsCLI.getAllFromDir("GenericERGProblem\\nov13\\one").get(0);// ProblemIntroVI.getProblemIntroVI2();//AntennaExamples.getSMC13();
		// List<Problem<ERG>> ps =
		// ProblemsCLI.getAllFromDir("GenericERGProblem\\nov13\\one");
		// for (Problem<ERG> prob : ps) {

		AlgorithmTest algTest = new ERGTest(QLearning.class);
		Map<String, Object> params = createParamsMap();
		runVI(prob, params);

		// Log.info("\n################################");
		// Log.info("TEST RUN " + count++);

		Test test = new BatchTest(prob, algTest.createAlgorithmFactory());
		test.run(params);
		// }
	}

	private static void runVI(Problem prob, Map<String, Object> params) {
		Policy best = new ValueIteration().run(prob, Collections.emptyMap());
		params.put(PolicyUtils.BEST_VALUES_STR, best.getBestPolicyValue());

		// Log.info("\nV-Values VI: \n" + new
		// GridPrinter().toGrid(prob.getModel(), best.getBestPolicyValue()));
	}

	private static Map<String, Object> createParamsMap() {
		return CollectionsUtils.asMap(ReinforcementLearning.AGENT_NAME, 0);
	}
}
