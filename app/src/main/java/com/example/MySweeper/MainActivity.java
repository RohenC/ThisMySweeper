package com.example.MySweeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.os.Handler;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class MainActivity extends AppCompatActivity {
    private int clock = 0;
    private boolean running = true;
    private static final int COLUMN_COUNT = 8;
    private boolean flag = false;

    // save the TextViews of all cells in an array, so later on,
    // when a TextView is clicked, we know which cell it is
    private ArrayList<TextView> cell_tvs;
    private Set<Integer> bombSet;

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            clock = savedInstanceState.getInt("clock");
            running = savedInstanceState.getBoolean("running");
        }

        runTimer();

        // turns this into a 2d array (of set rowSize 10 and colSize 8)
        cell_tvs = new ArrayList<TextView>();
        bombSet = new HashSet<Integer>();

        // Method (2): add four dynamically created cells
        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout01);
        for (int i = 0; i<=9; i++) {
            for (int j=0; j<=7; j++) {
                TextView tv = new TextView(this);
                tv.setHeight( dpToPixel(32) );
                tv.setWidth( dpToPixel(32) );
                tv.setTextSize( 16 );//dpToPixel(32) );
                tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                tv.setTextColor(Color.GREEN);
                tv.setBackgroundColor(Color.GREEN);
                tv.setOnClickListener(this::onClickTV);
                tv.setText("0"); //all start at 0

                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.setMargins(dpToPixel(2), dpToPixel(2), dpToPixel(2), dpToPixel(2));
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid.addView(tv, lp);

                //add to 2d array here instead
                cell_tvs.add(tv);
            }
        }


        //now randomly create 4 bombs with nums btwn 0->79
        while (bombSet.size() < 4)
        {
            int randIndex = ThreadLocalRandom.current().nextInt(0, 80);
            bombSet.add(randIndex);
        }

        //should now have 4 bombs and now need to convert these into pairs
            //for each pair increment the count of it's 8 neighbors (all start at 0?)

        //imma start by just displaying them if they are clicked upon

    }

    private int findIndexOfCellTextView(TextView tv) {
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }

    public void onClickTV(View view){
        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
//        int i = n/COLUMN_COUNT;
//        int j = n%COLUMN_COUNT;
//        tv.setText(String.valueOf(n));
        if (bombSet.contains(n)) {
            //print out all the bombs and end the game
            for (Integer i : bombSet)
            {
                //make all text to bombs
                cell_tvs.get(i).setText(getResources().getString(R.string.mine));
            }
            //now reroute to end page
        }

        if (tv.getCurrentTextColor() == Color.GREEN) {
            if (flag) {
                //make the text a flag, keep background green
                if (tv.getText().equals(getResources().getString(R.string.flag))) {
                    //remove the flag
                    //call funct to inc flagCount (+1)
                    updateFlagCount(1);
                    tv.setText(String.valueOf(n));
                }
                else {
                    //call function to decrement flag Count (-1)
                    updateFlagCount(-1);
                    tv.setText(getResources().getString(R.string.flag));
                }
            }
            else {
                //before picking check to see if there was a flag there
                    //with getText()
                //if so do nothing
                if (!tv.getText().equals(getResources().getString(R.string.flag))) {
                    //if the flag is not there do stuff

                    //you pick the square so make it gray and reveal #
                    //here you would also do the BFS expansion
                    tv.setText(String.valueOf(n));
                    tv.setTextColor(Color.GRAY);
                    tv.setBackgroundColor(Color.LTGRAY);
                }
                //otherwise do nothing
            }
        }
        //don't do anything if the grid is already gray
    }

    public void updateFlagCount(int offset)
    {
        final TextView flagView = (TextView) findViewById(R.id.flagNum);
        int flagNum = Integer.parseInt(flagView.getText().toString());
        flagNum += offset;
        flagView.setText(String.valueOf(flagNum));
    }

    //create an onClick function
    public void onClickBottom(View view)
    {
        TextView tv = (TextView) view;
        //may need to change to like .equals()
        if (flag) {
            tv.setText(getResources().getString(R.string.pick));
            flag = false;
        }
        else {
            tv.setText(getResources().getString(R.string.flag));
            flag = true;
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("clock", clock);
        savedInstanceState.putBoolean("running", running);
    }

    private void runTimer() {
        final TextView timeView = (TextView) findViewById(R.id.clockNum);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                int seconds = clock;
                String time = String.format("%d", seconds);
                timeView.setText(time);

                if (running) {
                    clock++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }
}