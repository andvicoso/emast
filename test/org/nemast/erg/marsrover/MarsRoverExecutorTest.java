package org.nemast.erg.marsrover;

import java.io.File;
import java.util.List;
import org.emast.model.algorithm.planning.ERGExecutor;
import org.emast.model.algorithm.planning.PolicyGenerator;
import org.emast.model.algorithm.planning.agent.factory.AgentIteratorFactory;
import org.emast.model.algorithm.planning.agent.factory.CommAgentIteratorFactory;
import org.emast.model.algorithm.planning.agent.iterator.PropReputationAgentIterator;
import org.emast.model.algorithm.planning.rewardcombinator.impl.MeanRewardCombinator;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.test.Test;
import org.emast.util.FileUtils;

/**
 *
 * @author Anderson
 */
public class MarsRoverExecutorTest extends Test {

    final static int rows = 9;
    final static int cols = 9;
    final static int size = rows * cols;
    final static int agents = (int) (0.1 * size);
    final static int obstacles = (int) (0.2 * size);

    public MarsRoverExecutorTest() {
        super(createProblem(), createExecutor());
    }

    private static Problem createProblem() {
        String dir = "problems" + File.separator
                + "mars" + File.separator;
//        final MarsRoverProblemFactory factory = new MarsRoverProblemFactory();
//        Problem p = factory.createProblem(rows, cols, agents, obstacles);
//
//        FileUtils.toFile(p,  dir+"MarsRoverProblem.emast");

        Problem p = FileUtils.fromFile(dir + "MarsRoverProblem_.emast");

        return p;
    }

    private static ERGExecutor createExecutor() {
        double badRewardValue = -20;
        int maxIterations = 10;
        PolicyGenerator<ERG> pg = new PPFERG<ERG>();
        AgentIteratorFactory factory = new CommAgentIteratorFactory(1, badRewardValue, -20);//PropReputationAgentIteratorFactory();
        List<PropReputationAgentIterator> agentIts = factory.createAgentIterators(agents);

        return new ERGExecutor(pg, agentIts, new MeanRewardCombinator(), maxIterations);
    }

    public static void main(String[] args) {
        new MarsRoverExecutorTest().run();
    }
}
