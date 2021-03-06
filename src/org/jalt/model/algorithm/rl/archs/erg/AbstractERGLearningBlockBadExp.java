package org.jalt.model.algorithm.rl.archs.erg;

import static org.jalt.util.DefaultTestProperties.BAD_EXP_VALUE;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jalt.infra.log.Log;
import org.jalt.model.algorithm.Algorithm;
import org.jalt.model.algorithm.reachability.PPFERG;
import org.jalt.model.algorithm.stoppingcriterium.StopOnAvoidableExpression;
import org.jalt.model.algorithm.stoppingcriterium.StoppingCriteria;
import org.jalt.model.algorithm.table.QTable;
import org.jalt.model.algorithm.table.erg.ERGQTable;
import org.jalt.model.chooser.Chooser;
import org.jalt.model.chooser.erg.BadExpressionChooser;
import org.jalt.model.function.PropositionFunction;
import org.jalt.model.function.transition.BlockedGridTransitionFunction;
import org.jalt.model.model.ERG;
import org.jalt.model.model.impl.GridModel;
import org.jalt.model.problem.Problem;
import org.jalt.model.propositional.Expression;
import org.jalt.model.solution.Policy;
import org.jalt.model.state.State;
import org.jalt.test.MainTest;
import org.jalt.util.DefaultTestProperties;
import org.jalt.util.erg.ERGLearningUtils;

/**
 * 
 * @author andvicoso
 */
public abstract class AbstractERGLearningBlockBadExp implements Algorithm<ERG, Policy> {
	protected final Set<Expression> avoid = new HashSet<Expression>(5);
	protected Set<State> blocked;
	protected final Chooser<Expression> expFinder = new BadExpressionChooser(BAD_EXP_VALUE, avoid);

	protected void initilize(ERG model) {
		avoid.clear();
		blocked = new HashSet<>(model.getStates().size() / 3);
		// POG
		setBlockedTransitionFunction(model);
	}

	private void setBlockedTransitionFunction(ERG model) {
		if (model instanceof GridModel) {
			GridModel gridModel = (GridModel) model;
			int rows = gridModel.getRows();
			int cols = gridModel.getCols();
			model.setTransitionFunction(new BlockedGridTransitionFunction(rows, cols, blocked));
		}
	}

	@Override
	public Policy run(Problem<ERG> prob, Map<String, Object> params) {
		int iteration = 0;
		ERG model = prob.getModel();
		ERGQTable q;
		Expression toBlock;
		Policy policy;
		// initialize
		initilize(model);
		boolean valid = false;
		// start main loop
		do {
			iteration++;
			// CREATE AND SAVE Q-TABLE (SHARED BY ALL AGENTS)
			q = new ERGQTable(model.getStates(), model.getActions());
			params.put(QTable.NAME, q);
			// Log.info("\nITERATION " + iteration + ":");
			// 1. RUN QLEARNING UNTIL A BAD REWARD EXPRESSION IS FOUND
			policy = runLearning(prob, params);
			// 2. GET BAD EXPRESSION FROM QLEARNING ITERATIONS
			toBlock = expFinder.chooseOne(q.getExpsValues());
			// valid expression: not null and not empty
			valid = isValid(toBlock);
			// 3. CHANGE THE Q VALUE FOR STATES THAT WERE VISITED IN
			// QLEARNING EXPLORATION AND HAVE THE FOUND EXPRESSION
			if (valid) {
				Log.info("Found bad expression: " + toBlock);
				// avoid bad exp
				avoid.add(toBlock);
				// Log.info("Avoid: " + avoid);
				populateBlocked(model, toBlock);// q
				// run vi again->new environment to compare
				params.putAll(MainTest.runVI(prob));
			}
		} while (valid);

		if (!avoid.isEmpty()) {
			policy = extractPolicyPPFERG(params, prob, q);
		} else {
			policy = q.getPolicy();
		}

		// Log.info("Preservation goal:" + model.getPreservationGoal());
		// Log.info("\nQTable: \n" + q.toString(model));

		return q.getPolicy();// policy;
	}

	// protected void populateBlocked(ERGQTable q) {
	// mark as blocked all states that
	// contains one of the "avoid" expressions
	// PropositionFunction pf = ERGFactory.createPropositionFunction(q);
	// for (State state : q.getStates()) {
	// Expression exp = pf.getExpressionForState(state);
	// for (Expression toBlock : avoid) {
	// if (toBlock.equals(exp)) {
	// blocked.add(state);
	// }
	// }
	// }
	//
	// Log.info("blocked: " + blocked);

	protected void populateBlocked(ERG model, Expression toBlock) {
		PropositionFunction pf = model.getPropositionFunction();
		for (State state : model.getStates()) {
			Expression exp = pf.getExpressionForState(state);
			if (toBlock.equals(exp)) {
				blocked.add(state);
			}
		}
	}

	private Problem<ERG> createNewProblem(Problem<ERG> oldProb, ERGQTable q) {
		ERG newModel = ERGLearningUtils.createModel(oldProb.getModel(), q, avoid);
		setBlockedTransitionFunction(newModel);
		Problem<ERG> newProblem = new Problem<>(newModel, oldProb.getInitialStates(), oldProb.getFinalStates());
		return newProblem;
	}

	protected boolean isValid(Expression exp) {
		return exp != null && !exp.isEmpty();
	}

	@Override
	public String printResults() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nBad exp reward param: ").append(BAD_EXP_VALUE);

		return sb.toString();
	}

	protected StoppingCriteria getStopCriteria() {
		return new StoppingCriteria(new StopOnAvoidableExpression(BAD_EXP_VALUE, avoid), DefaultTestProperties.DEFAULT_STOPON);
	}

	protected Policy extractPolicyPPFERG(Map<String, Object> pParameters, Problem<ERG> oldProb, ERGQTable q) {
		// 4. CREATE NEW MODEL AND PROBLEM FROM AGENT EXPLORATION
		Problem<ERG> newProblem = createNewProblem(oldProb, q);
		// 5. CREATE PPFERG ALGORITHM
		final PPFERG<ERG> ppferg = new PPFERG<ERG>();
		// 6. GET THE VIABLE POLICIES FROM PPFERG EXECUTED OVER THE NEW
		// MODEL
		Log.info("Starting PPFERG...");
		Policy policy = ppferg.run(newProblem, pParameters);
		// after ppferg
		// Log.info(prob.toString(policy));
		// 7. GET THE FINAL POLICY FROM THE PPFERG VIABLE POLICIES
		policy = policy.optimize(q);
		// after optimize
		// Log.info(prob.toString(policy));
		return policy;
	}

	protected abstract Policy runLearning(Problem<ERG> prob, Map<String, Object> pParameters);

}
