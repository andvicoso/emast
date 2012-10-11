package org.emast;

import org.emast.model.agent.combineresults.CombineResults;
import org.emast.model.agent.combineresults.PropRepCombineResults;
import org.emast.model.agent.factory.AgentFactory;
import org.emast.model.agent.factory.CommAgentFactory;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.planning.ERGExecutor;
import org.emast.model.algorithm.planning.PolicyGenerator;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.model.ERG;
import org.emast.model.planning.PreservationGoalFactory;
import org.emast.model.planning.propositionschooser.CombinePropsChooser;
import org.emast.model.planning.propositionschooser.PropositionsChooser;
import org.emast.model.planning.rewardcombinator.MeanRewardCombinator;
import org.emast.model.problem.Problem;
import org.emast.model.test.Test;
import org.emast.util.FileUtils;

/**
 *
 * @author Anderson
 */
public class MultipleTest {

    private static ERGExecutor createExecutor() {
        double badMsgValue = -20;
        double badRewardValue = -20;
        double messageCost = -1;
        int maxIterations = 1;
        
        PreservationGoalFactory goalFactory = new PreservationGoalFactory();
        PropositionsChooser chooser = new CombinePropsChooser(new MeanRewardCombinator(), badRewardValue);
        CombineResults comb = new PropRepCombineResults(chooser, goalFactory);
        PolicyGenerator<ERG> pg = new PPFERG<ERG>();
        AgentFactory factory = //new PropReputationAgentFactory<ERG>(badRewardValue);
                new CommAgentFactory(messageCost, badRewardValue, badMsgValue);
        
        return new ERGExecutor(pg, factory, comb, maxIterations);//new Planner(pg, factory.createAgents(agents));//
    }

    private static Algorithm[] createAlgorithms() {
        return new Algorithm[]{new PPFERG(), createExecutor()};
    }

    private static Problem createProblem() {
        return FileUtils.fromFile("problems/RoverModel/problem9.emast");
    }

    public static void main(String[] args) {
        new Test(createProblem(), createAlgorithms()).run();
    }
}