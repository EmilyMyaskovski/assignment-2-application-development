package com.example.assignment2.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.assignment2.R;
import com.example.assignment2.Utilities.ScoreData;
import com.example.assignment2.Utilities.SharePreferencesManager;
import com.example.assignment2.Utilities.RecordAdapter;
import com.example.assignment2.Interfeces.Callback_ScoresItemClicked;
import java.util.List;

public class ScoreFragment extends Fragment implements Callback_ScoresItemClicked {

    private RecyclerView recyclerViewScores;
    private RecordAdapter scoreAdapter;
    private SharePreferencesManager sharePreferencesManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score, container, false);
        recyclerViewScores = view.findViewById(R.id.recycler_view);
        recyclerViewScores.setLayoutManager(new LinearLayoutManager(getContext()));

        sharePreferencesManager = SharePreferencesManager.getInstance(getContext());
        List<ScoreData> records = sharePreferencesManager.getRecords();
        scoreAdapter = new RecordAdapter(records, this, getContext());
        recyclerViewScores.setAdapter(scoreAdapter);

        return view;
    }

    @Override
    public void scoresItemClicked(ScoreData scoreData) {
        // Implement the action to be taken when a score item is clicked
    }
}
