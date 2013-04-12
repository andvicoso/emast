package org.emast.util.grid;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import org.emast.model.model.MDP;
import org.emast.model.model.impl.GridModel;
import org.emast.model.problem.Problem;

/**
 *
 * @author Anderson
 */
public class GridModelWriter {

    private final Problem problem;
    private final String filename;

    public GridModelWriter(String filename, Problem problem) {
        this.filename = filename;
        this.problem = problem;
    }

    public void write() throws IOException {
        Writer fw = new BufferedWriter(new FileWriter(filename));
        MDP model = problem.getModel();

        if (model instanceof GridModel) {
            final GridPrinter gridPrinter = new GridPrinter();
            String[][] grid = gridPrinter.getGrid((GridModel) model, problem.getInitialStates());
            String smodel = gridPrinter.toTable(grid);

            fw.write(smodel);
        }

        fw.close();
    }
}