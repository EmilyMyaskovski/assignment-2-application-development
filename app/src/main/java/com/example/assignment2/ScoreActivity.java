package com.example.assignment2;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.assignment2.Utilities.ScoreData;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class ScoreActivity extends AppCompatActivity {

    public static final String KEY_SCORE = "KEY_SCORE";
    public static final String KEY_STATUS = "KEY_STATUS";
    private RecyclerView recyclerView;
  //  private RecordAdapter scoreAdapter;
    private List<ScoreData> scoreList;
    private MaterialTextView score;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_score);
        initViews();
        findViewById();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        scoreList = new ArrayList<>();
      // scoreAdapter = new RecordAdapter(scoreList,);
      //  recyclerView.setAdapter(scoreAdapter);


    }



    private void initViews() {

        Intent previousActivity = getIntent();

        String status = previousActivity.getStringExtra(KEY_STATUS);
        int points = previousActivity.getIntExtra(KEY_SCORE, 0);

        score.setText(status + "\n" + score);
    }

    private void findViewById() {
        recyclerView = findViewById(R.id.recycler_view);
        score = findViewById(R.id.scoreText);
    }
}