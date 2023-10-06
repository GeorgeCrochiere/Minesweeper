package dev.gdcrochiere.minesweaper;

import java.io.Serializable;

public class DataTransfer implements Serializable {
    private int rows;
    private int cols;
    private int mines;
    public DataTransfer(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
    }
    public int getRows(){
        return this.rows;
    }
    public int getCols() {
        return cols;
    }
    public int getMines() {
        return mines;
    }
}
