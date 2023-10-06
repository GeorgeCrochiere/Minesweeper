package dev.gdcrochiere.minesweaper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.Date;

public class GameActivity extends AppCompatActivity {
    private MinesweaperGame game;
    private GUIGenerator gui;
    private GridLayout grid;
    private TextView minesLeft;
    private Button toggle;
    private int[] mineColors;
    private int numMines;
    private Intent intent;
    private DataTransfer dt;
    private Date initTime;
    private Date finalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Set intent for later use
        intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Get game components from layout
        grid = findViewById(R.id.grid_layout);
        minesLeft = findViewById(R.id.mines_left);
        toggle = findViewById(R.id.toggle_mines);
        mineColors = getResources().getIntArray(R.array.num_colors);
        initTime = null;

        // Call handlers
        ButtonGridHandler bgh = new ButtonGridHandler();
        toggleMode tmh = new toggleMode();
        Point size = new Point();
        toggle.setOnClickListener(tmh);

        dt = (DataTransfer)getIntent().getSerializableExtra("DataTransfer");
        numMines = dt.getMines();
        int rows = dt.getRows();
        int cols = dt.getCols();

        // Generate game and GUI
        getWindowManager().getDefaultDisplay().getSize(size);
        game = new MinesweaperGame(rows,cols,numMines);
        gui = new GUIGenerator(this, rows, cols, size.x, size.y, grid, bgh);
        setMinesText(numMines);
        /*
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (game.getMineVal(i, j) == -1) {
                    gui.getButton(i,j).setText(gui.STANDARD_MINE);
                } else if (game.getMineVal(i, j) != 0) {
                    gui.getButton(i,j).setText(Integer.toString(game.getMineVal(i, j)));
                }
            }
        }
        */
    }
    private class ButtonGridHandler implements View.OnClickListener {
        public void onClick(View v) {
            int[] coords = gui.getCoords((Button) v);
            int status = game.getMineVal( coords[0], coords[1]);

            // Get first button press - time
            if (initTime == null) {
                initTime = new Date();
            }

            // Marking where mines are
            if (!game.getPlayState()) {
                if (!gui.compareFlagText(coords[0], coords[1]) && numMines > 0) {
                    numMines--;
                    setMinesText(numMines);
                    gui.getButton(coords[0], coords[1]).setText(gui.HOLD_MINE);
                    gui.getButton(coords[0], coords[1]).setTextColor(getResources().getColor(R.color.dark_red));
                } else if (gui.compareFlagText(coords[0], coords[1])) {
                    numMines++;
                    setMinesText(numMines);
                    gui.getButton(coords[0], coords[1]).setText("");
                    gui.getButton(coords[0], coords[1]).setTextColor(getResources().getColor(R.color.dark_red));
                }
            } else if (gui.compareFlagText(coords[0], coords[1])) { // Flag Present
                ;
            } else if (status == -1) {  // Mine Selected
                game.gameOver(gui);
                toggle.setEnabled(false);
                finalTime = new Date();
                long difference = timeDiff(finalTime, initTime);
                endGamePrompt("You lost! Better luck next time!", difference);
            } else if (status > 0) { // Mines nearby, numbered
                gui.getButton(coords[0], coords[1]).setTextColor(mineColors[9]);
                gui.getButton(coords[0], coords[1]).setText(Integer.toString(game.getMineVal(coords[0], coords[1])));
                gui.getButton(coords[0], coords[1]).setBackgroundColor(mineColors[status]);
                gui.disableButton(coords[0], coords[1]);
            } else {    // Blank Cells
                game.floodfill(coords[0], coords[1], gui);
                gui.update(game);
                //Fill Function given array of parts that need to be filled
            }

            // Check win condition
            if (game.gameWin(gui)) {
                finalTime = new Date();
                long seconds = timeDiff(finalTime, initTime);
                gui.stopGame();
                toggle.setEnabled(false);
                endGamePrompt("You win! Congrats!", seconds);
            }
        }
    }
    private class toggleMode implements View.OnClickListener {
        public void onClick(View v) {
            game.togglePlayState();
            toggleToggleText(game.getPlayState());
        }
    }

    private long timeDiff(Date dEnd, Date dStart) {
        return (dEnd.getTime() - dStart.getTime())/1000;
    }
    public void endGamePrompt(String result, long timeDiff) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(result);
        String secondUnits = "second";
        if (timeDiff != 1) secondUnits += "s";
        alert.setMessage("Time Taken: " + timeDiff + " " + secondUnits + ".\nPlay Minesweeper again?");
        PlayDialog play = new PlayDialog();
        alert.setPositiveButton("Yes", play);
        alert.setNegativeButton("No", play);
        alert.show();
    }
    private class PlayDialog implements DialogInterface.OnClickListener {
        public void onClick( DialogInterface dialog, int id ) {
            if (id == -1) {                 // Yes, go back to selection screen
                GameActivity.this.finish();
            } else if (id == -2) {          // No
                intent.putExtra("EXIT", true);
                startActivity(intent);
                GameActivity.this.finish();
            }
        }
    }

    public void setMinesText(int mines) {
        minesLeft.setText("Mines Left: " + Integer.toString(mines));
    }
    public void toggleToggleText(boolean state) {
        if (numMines < 1) return;
        if (state) {
            toggle.setText("Mark Mines");
        } else {
            toggle.setText("Clear Area");
        }
    }
}
