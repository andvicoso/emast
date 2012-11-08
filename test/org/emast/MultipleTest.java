package org.emast;

import org.emast.erg.EnsembleTest;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.test.Test;

/**
 *
 * @author Anderson
 */
public class MultipleTest {

    private static Algorithm createAgentGroup() {
        return EnsembleTest.createAlgorithm();
    }

    private static Algorithm[] createAlgorithms() {
        return new Algorithm[]{new PPFERG(), createAgentGroup()};
    }

    public static void main(String[] args) {
        new Test(CurrentProblem.create(), createAlgorithms()).run();
    }
}
