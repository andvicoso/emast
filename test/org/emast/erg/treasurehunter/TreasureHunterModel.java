package org.emast.erg.treasurehunter;

import org.emast.model.model.impl.ERGGridModel;

/**
 *
 * @author anderson
 */
public class TreasureHunterModel extends ERGGridModel {

    public TreasureHunterModel(final int pRows, final int pCols, final int pAgents) {
        super(pRows, pCols);
        setAgents(pAgents);
    }
}
