package org.jalt.model.test.erg;

import org.jalt.model.algorithm.Algorithm;
import org.jalt.model.algorithm.AlgorithmFactory;
import org.jalt.model.algorithm.iteration.rl.ReinforcementLearning;
import org.jalt.model.problem.Problem;
import org.jalt.model.problem.ProblemFactory;
import org.jalt.model.test.erg.generic.GenericERGProblemFactory;
import org.jalt.view.ui.cli.ProblemsCLI;

@SuppressWarnings("rawtypes")
public class AlgorithmTest {

	private Class<? extends ReinforcementLearning> learningClass;

	public AlgorithmTest(Class<? extends ReinforcementLearning> learning) {
		this.learningClass = learning;
	}

	public Problem createFromCLI(int agents) {
		ProblemFactory factory = GenericERGProblemFactory.createDefaultFactory();
		return new ProblemsCLI(factory).run();
	}

	public AlgorithmFactory createAlgorithmFactory() {
		return new AlgorithmFactory() {
			@Override
			public Algorithm create() {
				return createAlgorithm();
			}
		};
	}

	public ReinforcementLearning createAlgorithm() {
		try {
			return learningClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
		}
		return null;
	}

	public Class<? extends ReinforcementLearning> getLearningClass() {
		return learningClass;
	}

}