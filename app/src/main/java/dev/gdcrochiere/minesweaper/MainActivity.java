package dev.gdcrochiere.minesweaper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText rows;
    private EditText cols;
    private EditText mines;
    private TextView errorStr;
    private Button submit;
    private static String[] errorMessages = {"All values must be greater than 0.",
            "The number of mines cannot be greater than equal to the number of available spaces, minus 1.",
            "Rows must be 20 or less.", "Columns must be 10 or less."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Intent for exiting
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        // Normal on create, get forum
        rows = findViewById(R.id.edit_rows);
        cols = findViewById(R.id.edit_columns);
        mines = findViewById(R.id.edit_mines);
        errorStr = findViewById(R.id.errorBar);
        submit = findViewById(R.id.submit_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonPress();
            }
        });

        // Set Default Values
        rows.setText("12");
        cols.setText("6");
        mines.setText("15");
    }
    public void buttonPress() {
        int rows = 0;
        int cols = 0;
        int mines = 0;
        try {
            rows = Integer.parseInt(this.rows.getText().toString());
        } catch (Exception e) {}
        try {
            cols = Integer.parseInt(this.cols.getText().toString());
        } catch (Exception e) {}
        try {
            mines = Integer.parseInt(this.mines.getText().toString());
        } catch (Exception e) {}

        if (test(rows, cols, mines)) {
            DataTransfer dt = new DataTransfer(rows, cols, mines);
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("DataTransfer", dt);
            this.startActivity(intent);
        }
    }
    public boolean test(int r, int c, int m) {
        int checked = checkConditions(r, c, m);
        System.out.println(checked);
        if (checked > 0) {
            errorStr.setText(errorMessages[checked - 1]);
            return false;
        } else {
            return true;
        }
    }
    public int checkConditions(int rows, int cols, int mines) {
        // Negative or 0 check
        if (rows < 1 || cols < 1 || mines < 1) {
            return 1;
        }
        // Check equal mines to grid
        if ((rows * cols) <= mines) {
            return 2;
        }
        // Check arbitrary max
        if (rows > 21) {
            return 3;
        }
        if (cols > 20) {
            return 4;
        }
        return 0;
    }
}