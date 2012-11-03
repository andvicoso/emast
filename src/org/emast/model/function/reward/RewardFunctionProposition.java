package org.emast.model.function.reward;

import java.util.Collection;
import java.util.Map;
import org.emast.model.action.Action;
import org.emast.model.function.PropositionFunction;
import org.emast.model.model.ERG;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class RewardFunctionProposition<M extends ERG> extends DefaultRewardFunction<M, Proposition> {

    public RewardFunctionProposition(M pModel, Map<Proposition, Double> pRewardValues, double pOtherwiseValue) {
        super(pModel, pRewardValues, pOtherwiseValue);
    }

    @Override
    public double getValue(final State pState, final Action pAction) {
        for (Proposition condition : getRewards().keySet()) {
            final PropositionFunction pf = getModel().getPropositionFunction();
            final Collection<State> badStates = pf.getStatesWithProposition(condition);
            //any state that leads to a bad proposition gives a getBadReward()
            final Collection<State> nextStates = getModel().getTransitionFunction().getFinalStates(
                    getModel().getStates(), pState, pAction);
            for (final State state : nextStates) {
                if (badStates.contains(state)) {
                    return getRewards().get(condition);
                }
            }
        }

        return getOtherwiseValue();
    }
}
