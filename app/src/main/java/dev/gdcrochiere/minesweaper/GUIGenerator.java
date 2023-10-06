package dev.gdcrochiere.minesweaper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

public class GUIGenerator extends GridLayout {
    public static final String STANDARD_MINE = "¤";
    public static final String HOLD_MINE = "▶";
    private int rows;
    private int cols;
    private Button[][] buttonGrid;
    private TextView numMinesLeft;
    private int[] mineColors;
    private Button toggleMines;
    private TextView status;
    private int[] gridColors;
    public GUIGenerator(Context game, int rows, int columns, int screenWidth, int screenHeight, GridLayout grid, OnClickListener gameListener) {
        super(game);
        grid.setColumnCount(columns);
        grid.setRowCount(rows);
        this.rows = rows;
        this.cols = columns;

        numMinesLeft = findViewById(R.id.mines_left);
        status = findViewById(R.id.toggle_mines);
        mineColors = getResources().getIntArray(R.array.num_colors);
        gridColors = getResources().getIntArray(R.array.buttonColorGrid);

        // Default Layout Parameters
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
        lp.leftMargin = 10;
        lp.rightMargin = 10;
        lp.topMargin = 10;
        lp.bottomMargin = 10;

        int width = screenWidth / columns;
        int height = (int)(screenHeight * 0.8) / rows;
        int buttonWidth = width;
        if (height < width) buttonWidth = height;
        lp.height = buttonWidth;
        lp.width = buttonWidth;

        // Button Grid for game
        buttonGrid = new Button[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                buttonGrid[i][j] = new Button(game);
                buttonGrid[i][j].setTextSize((int)(buttonWidth * 0.15));
                buttonGrid[i][j].setOnClickListener(gameListener);
                buttonGrid[i][j].setBackgroundColor(gridColors[(i+j)%2]);
                buttonGrid[i][j].setLayoutParams(lp);
                grid.addView(buttonGrid[i][j], buttonWidth, buttonWidth);
            }
        }

    }

    public Button getButton(int x, int y) {
        return buttonGrid[x][y];
    }

    public int[] getCoords(Button b) {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                if (b == buttonGrid[i][j]) {
                    return new int[] {i, j};
                }
            }
        }
        return new int[] {0,0};
    }

    public void disableButton(int x, int y) {
        buttonGrid[x][y].setEnabled(false);
    }
    public void stopGame() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                disableButton(i, j);
            }
        }
    }

    public void setButtonRed(int x, int y) {
        buttonGrid[x][y].setBackgroundColor(Color.RED);
    }
    public boolean compareFlagText(int x, int y) {
        return (GUIGenerator.HOLD_MINE) == (buttonGrid[x][y].getText());
    }
    public boolean testFlagged(int x, int y) {
        return (buttonGrid[x][y].getText()).equals(HOLD_MINE);
    }
    public boolean getButtonState(int x, int y) {
        return buttonGrid[x][y].isEnabled();
    }
    @SuppressLint("ResourceAsColor")
    public void update(MinesweaperGame g) {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (g.revealedCurrent[i][j] == 1) {
                    disableButton(i, j);
                    g.revealedCurrent[i][j] = 0;
                    int numMines = g.getMineVal(i,j);
                    buttonGrid[i][j].setTextColor(mineColors[9]);
                    buttonGrid[i][j].setBackgroundColor(mineColors[numMines]);
                    if (numMines == 0) {
                        buttonGrid[i][j].setText("");
                    } else {
                        buttonGrid[i][j].setText(Integer.toString(g.getMineVal(i,j)));
                    }
                }
            }
        }
    }
}

