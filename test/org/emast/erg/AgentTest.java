package org.emast.erg;

import org.emast.CurrentProblem;
import org.emast.model.algorithm.ensemble.AgentEnsembleICAPSSP;
import org.emast.model.algorithm.iteration.rl.SARSA;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.test.Test;

/**
 *
 * @author anderson
 */
public class AgentTest {

    private static Problem createProblem() {
        Problem p = CurrentProblem.create();
        //p = ReinforcementConverter.convert(p);
        return p;
    }

    public static void main(final String[] pArgs) {
        new Test(createProblem(), new SARSA()).run();// new ValueIterationAlgorithm(), 
    }
}
