package org.jalt.util.erg;

import java.util.Map;
import java.util.Set;

import org.jalt.infra.log.Log;
import org.jalt.model.action.Action;
import org.jalt.model.algorithm.rl.td.QLearning;
import org.jalt.model.algorithm.table.erg.ERGQTable;
import org.jalt.model.algorithm.table.erg.ERGQTableItem;
import org.jalt.model.chooser.Chooser;
import org.jalt.model.chooser.MinValueChooser;
import org.jalt.model.exception.InvalidExpressionException;
import org.jalt.model.function.PropositionFunction;
import org.jalt.model.function.reward.RewardFunction;
import org.jalt.model.function.transition.TransitionFunction;
import org.jalt.model.model.ERG;
import org.jalt.model.model.impl.ERGModel;
import org.jalt.model.propositional.Expression;
import org.jalt.model.propositional.Proposition;
import org.jalt.model.state.State;
import org.jalt.util.ModelUtils;

/**
 * 
 * @author andvicoso
 */
public class ERGFactory {

	public static ERG create(ERG model, QLearning<ERG> q) {
		return create(model, q, model.getPropositionFunction(), model.getPropositions(),
				model.getPreservationGoal(), model.getGoal());
	}

	public static ERG create(ERG model, QLearning<ERG> q, PropositionFunction pf,
			Set<Proposition> props, Expression preservGoal, Expression finalGoal) {
		return create(model, (ERGQTable) q.getQTable(), pf, props, preservGoal, finalGoal);
	}

	public static ERG create(ERG model, ERGQTable qt, PropositionFunction pf,
			Set<Proposition> props, Expression preservGoal, Expression finalGoal) {
		Map<Expression, Double> expsValues = qt.getExpsValues();
		if (!expsValues.isEmpty()) {
			RewardFunction rf = model.getRewardFunction();// createRewardFunction(qt);
			TransitionFunction tf = ModelUtils.createTransitionFunctionFrequency(qt, model);
			Expression newPreservGoal = createPresevationGoal(model, expsValues, null);

			if (newPreservGoal != null) {
				Log.info("Changed preservation goal from {" + preservGoal + "} to {"
						+ newPreservGoal + "}");

				ERG erg = new ERGModel();
				erg.setActions(model.getActions());
				erg.setStates(model.getStates());
				erg.setRewardFunction(rf);
				erg.setTransitionFunction(tf);
				erg.setPropositionFunction(pf);
				erg.setPreservationGoal(newPreservGoal);
				erg.setGoal(finalGoal);
				erg.setPropositions(props);

				return erg;
			}
		}
		return null;
	}

	private static boolean existValidFinalState(PropositionFunction pf, Expression newPreservGoal,
			Iterable<State> finalStates) {
		try {

			for (State state : finalStates) {
				if (pf.satisfies(state, newPreservGoal)) {
					return true;
				}
			}
		} catch (InvalidExpressionException ex) {
		}

		return false;
	}

	public static Expression createPreservationGoal(Map<Expression, Double> expsValues,
			Expression preservGoal) {
		Set<Expression> exps = getBadExpressions(expsValues);
		Expression newPreservGoal = new PreservationGoalFactory().createPreservationGoalExp(
				preservGoal, exps);

		return newPreservGoal;
	}

	public static Expression createPresevationGoal(ERG model, Map<Expression, Double> expsValues,
			Set<Expression> avoid) {
		// PropositionFunction pf = model.getPropositionFunction();
		// Expression pg = model.getPreservationGoal();
		Expression finalNewPg = null;

		try {
			// Collection<State> finalStates = pf.intension(model.getStates(),
			// model.getPropositions(), model.getGoal());
			// createPreservationGoal(expsValues, pg);

			while (true) {
				Expression exp = getBadExpressions(expsValues).iterator().next();
				if (!avoid.contains(exp)) {
					finalNewPg = exp;
					break;
				} else if (expsValues.isEmpty()) {
					break;
				}
				expsValues.remove(exp);
			}

			// if (canChangePreservGoal(pf, pg, newPg, finalStates)) {
			// finalNewPg = newPg;
			// Log.info("Changed preservation goal from {"+ pg + "} to {" +
			// finalNewPg + "}");
			// }
		} catch (Exception ex) {
		}

		return finalNewPg;
	}

	public static boolean canChangePreservGoal(PropositionFunction pf, Expression preservGoal,
			Expression newPreservGoal, Iterable<State> finalStates) {

		// compare previous goal with the newly created
		return !newPreservGoal.equals(preservGoal) && !preservGoal.contains(newPreservGoal)
				&& !preservGoal.contains(newPreservGoal.negate())
				&& existValidFinalState(pf, newPreservGoal, finalStates);
	}

	public static Set<Expression> getBadExpressions(Map<Expression, Double> expsValues) {
		Chooser<Expression> chooser = new MinValueChooser<Expression>();
		return chooser.choose(expsValues);
	}

	public static PropositionFunction createPropositionFunction(ERGQTable q) {
		PropositionFunction pf = new PropositionFunction();

		for (State state : q.getStates()) {
			for (Action action : q.getActions()) {
				ERGQTableItem item = q.get(state, action);
				State fState = item.getFinalState();
				if (fState != null && item != null && item.getExpression() != null) {
					pf.add(fState, item.getExpression().getPropositions());
				}
			}
		}

		return pf;
	}
}
