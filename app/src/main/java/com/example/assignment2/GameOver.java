package com.example.assignment2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.example.assignment2.Utilities.ScoreData;
import com.example.assignment2.Utilities.SharePreferencesManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class GameOver extends AppCompatActivity {

    public static final String KEY_SCORE = "KEY_SCORE";
    public static final String KEY_LATITUDE = "KEY_LATITUDE";
    public static final String KEY_LONGITUDE = "KEY_LONGITUDE";
    private CoinManager coinManager;
    private MaterialTextView score_LBL_status;
    private ExtendedFloatingActionButton to_score_table;
    private EditText player_name;
    private ExtendedFloatingActionButton send_name;
    private SharePreferencesManager sharePreferencesManager;
    private FusedLocationProviderClient fusedLocationClient;
    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);

        coinManager = new CoinManager(9, 5);
        sharePreferencesManager = SharePreferencesManager.getInstance(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        findViews();
        initViews();
        getCurrentLocation();
    }

    private void initViews() {
        Intent previousActivity = getIntent();
        int score = previousActivity.getIntExtra(KEY_SCORE, coinManager.getScore());
        score_LBL_status.setText("Your Score\n" + score);
        Log.d("GameOver initViews", "score = " + score);

        send_name.setOnClickListener(view -> {
            String playerName = player_name.getText().toString();
            if (!playerName.isEmpty()) {
                saveNewRecords(score, currentLatitude, currentLongitude, playerName);
                changeToScoreMapActivity();
            }
        });

        to_score_table.setOnClickListener(view -> changeToScoreMapActivity());
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
            }
        });
    }

    private List<ScoreData> saveNewRecords(int score, double latitude, double longitude, String name) {
        ScoreData newRecord = new ScoreData(name, score, latitude, longitude);
        List<ScoreData> records = sharePreferencesManager.getRecords();
        records.add(newRecord);
        records.sort((o1, o2) -> Integer.compare(o2.getScore(), o1.getScore()));

        // Remove the last record if the list size exceeds 10
        if (records.size() > 10) {
            records.remove(records.size() - 1);
        }



        return sharePreferencesManager.saveRecords(records);
    }

    private void findViews() {
        score_LBL_status = findViewById(R.id.score_LBL_status);
        to_score_table = findViewById(R.id.to_score_table);
        player_name = findViewById(R.id.player_name);
        send_name = findViewById(R.id.send_name);
    }

    private void changeToScoreMapActivity() {
        Intent scoreIntent = new Intent(this, ScoreMapActivity.class);
        startActivity(scoreIntent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }
}
