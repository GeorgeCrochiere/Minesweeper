package dev.gdcrochiere.minesweaper;

import android.annotation.SuppressLint;

import java.util.Date;
import java.util.Random;

public class MinesweaperGame {
    private int [][] game;
    public int [][] revealedCurrent;
    private int rows;
    private int columns;
    private int mines;
    private boolean currentMode = true;

    public MinesweaperGame(int rows, int columns, int mines) {
        this.rows = rows;
        this.columns = columns;
        this.mines = mines;
        this.game = new int[rows][columns];
        this.revealedCurrent = new int[rows][columns];
        resetRevealed();
        //this.revealed = new int[rows][columns];
        this.assignMines();
        //this.assignDefaultStates();
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                if (this.game[i][j] != -1) {
                    this.game[i][j] = calcMinesNearby(i, j);
                }
            }
        }
    }
    public boolean getPlayState() {
        return this.currentMode;
    }
    public void togglePlayState() {
        this.currentMode = !currentMode;
    }

    public int getMineVal(int x, int y) {
        return game[x][y];
    }

    private void assignMines() {
        Random rand = new Random((int) new Date().getTime());
        for (int i = 0; i < this.mines; i++) {
            int a = rand.nextInt(rows);
            int b = rand.nextInt(columns);
            while (this.game[a][b] == -1) {
                a = rand.nextInt(rows);
                b = rand.nextInt(columns);
            }
            this.game[a][b] = -1;
        }
    }

    /*public void assignDefaultStates() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                this.revealed[i][j] = 0;
            }
        }
    }*/

    private int calcMinesNearby(int row, int col) {
        int result = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                try {
                    if (this.game[row + i][col + j] == -1) {
                        result++;
                    }
                } catch (Exception e) {
                    result += 0;
                }
            }
        }
        return result;
    }

    @SuppressLint("ResourceAsColor")
    public void gameOver(GUIGenerator guiGen) {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                guiGen.disableButton(i, j);
                if (game[i][j] == -1) {
                    guiGen.setButtonRed(i, j);
                    guiGen.getButton(i, j).setText(guiGen.STANDARD_MINE);
                    guiGen.getButton(i, j).setTextColor(R.color.dark_red);
                }
            }
        }
    }
    public boolean gameWin(GUIGenerator gui) {
        int counter = 0;
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                if (gui.getButtonState(i, j)) {
                    counter++;
                }
            }
        }
        return (counter == this.mines);
    }
    @SuppressLint("ResourceAsColor")
    public void floodfill(int x, int y, GUIGenerator gui) {
        // Uses DFS
        if (x >= 0 && y >= 0 && x < this.rows && y < this.columns) {
            if (this.game[x][y] == 0 && this.revealedCurrent[x][y] == 0 && !gui.testFlagged(x, y)) {
                this.revealedCurrent[x][y] = 1;
                floodfill(x+1, y, gui);
                floodfill(x-1, y, gui);
                floodfill(x, y-1, gui);
                floodfill(x, y+1, gui);
                floodfill(x+1, y+1, gui);
                floodfill(x-1, y+1, gui);
                floodfill(x-1, y-1, gui);
                floodfill(x+1, y-1, gui);
            } else {
                if (this.game[x][y] > 0 && this.revealedCurrent[x][y] == 0 && !gui.testFlagged(x, y)) {
                    this.revealedCurrent[x][y] = 1;
                }
            }
        }
    }
    public void resetRevealed() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                this.revealedCurrent[i][j] = 0;
            }
        }
    }
}
