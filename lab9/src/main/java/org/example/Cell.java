package org.example;

import java.io.Serializable;

public class Cell implements Serializable {
    public int row, col;
    public boolean[] walls = {true, true, true, true};
    private boolean occupied = false;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public synchronized boolean occupy() {
        if (occupied) return false;
        occupied = true;
        return true;
    }

    public synchronized void release() {
        occupied = false;
    }
}