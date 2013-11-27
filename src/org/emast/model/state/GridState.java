package org.emast.model.state;

import org.emast.util.grid.GridUtils;

public class GridState extends State {

    private final int row;
    private final int col;

    public GridState(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public String getName() {
        if (super.getName() == null) {
            setName(GridUtils.getGridStateName(row, col));
        }
        return super.getName();
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GridState) {
            GridState gs = (GridState) obj;
            return row == gs.row && col == gs.col;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + this.row;
        hash = 97 * hash + this.col;
        return hash;
    }
}
