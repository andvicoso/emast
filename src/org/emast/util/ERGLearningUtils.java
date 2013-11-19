package org.emast.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.emast.model.action.Action;
import org.emast.model.algorithm.erg.ERGFactory;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.function.PropositionFunction;
import org.emast.model.function.reward.RewardFunction;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.ERG;
import org.emast.model.model.Grid;
import org.emast.model.model.impl.ERGGridModel;
import org.emast.model.model.impl.ERGModel;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.propositional.operator.BinaryOperator;
import org.emast.model.solution.Policy;
import org.emast.model.solution.SinglePolicy;
import org.emast.model.state.State;

public class ERGLearningUtils {

	public static ERG createModel(ERG oldModel, ERGQTable q, Set<Expression> avoid) {
		ERG model = createModel(oldModel);
		// COPY MAIN PROPERTIES
		model.setStates(q.getStates());
		model.setActions(q.getActions());
		model.setGoal(oldModel.getGoal());
		model.setAgents(oldModel.getAgents());
		// GET THE SET OF PROPOSITIONS FROM EXPLORATED STATES
		model.setPropositions(getPropositions(q.getExpsValues()));
		// CREATE NEW PRESERVATION GOAL FROM EXPRESSIONS THAT SHOULD BE AVOIDED
		Expression newPreservGoal = createNewPreservationGoal(oldModel.getPreservationGoal(), avoid);
		model.setPreservationGoal(newPreservGoal);
		// CREATE NEW TRANSITION FUNCTION FROM AGENT'S EXPLORATION (Q TABLE)
		TransitionFunction tf = ERGFactory.createTransitionFunctionFrequency(q);
		model.setTransitionFunction(tf);
		// CREATE NEW PROPOSITION FUNCTION FROM AGENT'S EXPLORATION (Q TABLE)
		PropositionFunction pf = ERGFactory.createPropositionFunction(q);
		model.setPropositionFunction(pf);
		// CREATE NEW REWARD FUNCTION FROM AGENT'S EXPLORATION (Q TABLE)
		RewardFunction rf = ERGFactory.createRewardFunction(q);
		model.setRewardFunction(rf);

		// Log.info("\nTransition Function\n" + new GridPrinter().print(tf,
		// model));

		return model;
	}

	private static Set<Proposition> getPropositions(Map<Expression, Double> expsValues) {
		Set<Proposition> props = new HashSet<Proposition>();
		for (Expression exp : expsValues.keySet()) {
			Set<Proposition> expProps = exp.getPropositions();
			props.addAll(expProps);
		}

		return props;
	}

	private static Expression createNewPreservationGoal(Expression pCurrent, Set<Expression> pAvoid) {
		Expression badExp = new Expression(BinaryOperator.OR, pAvoid.toArray(new Expression[pAvoid
				.size()]));
		return pCurrent.and(badExp.parenthesize().negate());
	}

	private static ERG createModel(ERG oldModel) {
		if (oldModel instanceof Grid) {
			return new ERGGridModel(((Grid) oldModel).getRows(), ((Grid) oldModel).getCols());
		}
		return new ERGModel();
	}

	public static SinglePolicy optmize(Policy policy, ERGQTable q) {
		SinglePolicy single = new SinglePolicy();
		for (Map.Entry<State, Map<Action, Double>> entry : policy.entrySet()) {
			State state = entry.getKey();
			Action bestAction = getBestAction(entry.getValue(), q.getDoubleValues(state));
			single.put(state, bestAction);
		}

		return single;
	}

	public static Action getBestAction(Map<Action, Double> policy, Map<Action, Double> q) {
		Collection<Action> best = getBestAction(policy, policy.keySet());
		if (policy.size() > 1) {
			Collection<Action> bestq = getBestAction(q, policy.keySet());
			if (bestq.size() > 1) {
				best = bestq;
			}
		}

		return best.iterator().next();
	}

	public static Collection<Action> getBestAction(Map<Action, Double> map, Set<Action> keySet) {
		Map<Action, Double> temp = new HashMap<Action, Double>(map);
		for (Action action : map.keySet()) {
			if (!keySet.contains(action)) {
				temp.remove(action);
			}
		}

		Double max = Collections.max(temp.values());
		return CollectionsUtils.getKeysForValue(temp, max);
	}

	private ERGLearningUtils() {
	}

}