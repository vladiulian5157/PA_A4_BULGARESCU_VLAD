package org.example;

import java.io.Serializable;

public class Cell implements Serializable {
    private static final long serialVersionUID = 1L;
    public int row, col;
    // 0:Top, 1:Right, 2:Bottom, 3:Left
    public boolean[] walls = {true, true, true, true};

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }
}