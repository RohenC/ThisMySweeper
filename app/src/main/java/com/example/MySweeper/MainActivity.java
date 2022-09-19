package com.example.MySweeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.res.Resources;
import android.content.Intent;
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
    private boolean reroute = false;
    private String rerouteMsg1;
    private String rerouteMsg2;
    private String rerouteMsg3;

    // save the TextViews of all cells in an array, so later on,
    // when a TextView is clicked, we know which cell it is
    private ArrayList<TextView> cell_tvs;
    private ArrayList<Integer> cellNums;
    private Set<Integer> bombSet;
    private Set<Integer> visited;

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
        cellNums = new ArrayList<Integer>();
        bombSet = new HashSet<Integer>();
        visited = new HashSet<Integer>();

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

                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.setMargins(dpToPixel(2), dpToPixel(2), dpToPixel(2), dpToPixel(2));
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid.addView(tv, lp);

                //add to 2d array here instead
                cell_tvs.add(tv);
                cellNums.add(0); //start them all off at 0
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
        //need to increment the vals of each of the text in there
        for (Integer i : bombSet)
        {
            //increment neighbors
            boolean topValid = (i > 7);
            boolean bottomValid = (i < 72);
            boolean leftValid = (i % 8 != 0);
            boolean rightValid = (i % 8 != 7);

            //top
            if (topValid) {
                //all valid for top
                updateGridVal(i-8);

                //check top left
                if (leftValid) {
                    updateGridVal(i-9);
                }
                // and top right
                if (rightValid) {
                    updateGridVal(i-7);
                }
            }
            //bottom
            if (bottomValid) {
                updateGridVal(i+8);

                //check bottom right
                if (rightValid) {
                    updateGridVal(i+9);
                }
                //and bottom left
                if (leftValid) {
                    updateGridVal(i+7);
                }
            }
            //right + left
            if (rightValid) {
                updateGridVal(i+1);
            }
            if (leftValid) {
                updateGridVal(i-1);
            }
        }
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

        if (reroute) {
            //reroute to next page with appropriate rerouteMsg
            //also pass in the time
            sendNext();
        }
        else if (tv.getCurrentTextColor() == Color.GREEN) {
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

                    //check if there is a bomb there first
                    if (bombSet.contains(n)) {
                        //print out all the bombs and end the game
                        for (Integer i : bombSet)
                        {
                            //make all text to bombs
                            cell_tvs.get(i).setText(getResources().getString(R.string.mine));
                        }
                        //now reroute to end page if the user clicks another square (so make book variable)
                        running = false;
                        reroute = true;
                        rerouteMsg1 = "You used _ seconds.";
                        rerouteMsg2 = "You Lost.";
                        rerouteMsg3 = "Nice try.";
                    }
                    else {
                        //you pick the square so make it gray and reveal #
                        //here you would also do the BFS expansion
                        BFS(n);
                        if (visited.size() == 76) {
                            running = false; //stop the timer
                            reroute = true;
                            rerouteMsg1 = "You used " + String.valueOf(clock) + " seconds.";
                            rerouteMsg2 = "You Won.";
                            rerouteMsg3 = "Good Job!";
                        }
                    }
                }
                //otherwise do nothing
            }
        }
        //don't do anything if the grid is already gray
    }

    public void sendNext()
    {
        Intent intent = new Intent(this, SecondPage.class);
        intent.putExtra("timeMsg", rerouteMsg1);
        intent.putExtra("outcomeMsg", rerouteMsg2);
        intent.putExtra("encMsg", rerouteMsg3);

        startActivity(intent);
    }

    public void BFS(int start)
    {
        //do bfs
            //clear out the text of the num if 0, reveal if > 0
            //add all of its neighbors to q (only in case of 0)
            //continue till q is empty
        //ALSO NEED A VISITED ARRAY/SET

        Queue<Integer> q = new LinkedList<>();
        q.add(start);
        visited.add(start);

        while(!q.isEmpty())
        {
            int i = q.poll();
            TextView square = cell_tvs.get(i);
            int gridVal = cellNums.get(i);
            if (gridVal > 0) {
                //reveal and stop
                //but check if there was a flag there first
                if (square.getText().equals(getResources().getString(R.string.flag))) {
                    updateFlagCount(1); //add to flagcount
                }
                square.setText(String.valueOf(gridVal));
                square.setTextColor(Color.GRAY);
                square.setBackgroundColor(Color.LTGRAY);
            }
            else {
                //erase text including flags
                if (square.getText().equals(getResources().getString(R.string.flag))) {
                    updateFlagCount(1); //add to flagcount
                }
                square.setText("");
                square.setBackgroundColor(Color.LTGRAY);

                //add all (valid) neighbors to q
                boolean topValid = (i > 7);
                boolean bottomValid = (i < 72);
                boolean leftValid = (i % 8 != 0);
                boolean rightValid = (i % 8 != 7);

                //top
                if (topValid) {
                    //all valid for top
                    if (!visited.contains(i-8)) {
                        q.add(i-8);
                        visited.add(i-8);
                    }

                    //check top left
                    if (leftValid && !visited.contains(i-9)) {
                        q.add(i-9);
                        visited.add(i-9);
                    }
                    // and top right
                    if (rightValid && !visited.contains(i-7)) {
                        q.add(i-7);
                        visited.add(i-7);
                    }
                }
                //bottom
                if (bottomValid) {
                    if (!visited.contains(i+8)) {
                        q.add(i+8);
                        visited.add(i+8);
                    }

                    //check bottom right
                    if (rightValid && !visited.contains(i+9)) {
                        q.add(i+9);
                        visited.add(i+9);
                    }
                    //and bottom left
                    if (leftValid && !visited.contains(i+7)) {
                        q.add(i+7);
                        visited.add(i+7);
                    }
                }
                //right + left
                if (rightValid && !visited.contains(i+1)) {
                    q.add(i+1);
                    visited.add(i+1);
                }
                if (leftValid && !visited.contains(i-1)) {
                    q.add(i-1);
                    visited.add(i-1);
                }
            }
        }
    }

    public void updateFlagCount(int offset)
    {
        final TextView flagView = (TextView) findViewById(R.id.flagNum);
        int flagNum = Integer.parseInt(flagView.getText().toString());
        flagNum += offset;
        flagView.setText(String.valueOf(flagNum));
    }

    public void updateGridVal(int index)
    {
        int gridVal = cellNums.get(index);
        gridVal++;
        cellNums.set(index, gridVal);
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