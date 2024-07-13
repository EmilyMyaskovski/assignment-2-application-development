package com.example.assignment2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.assignment2.Interfeces.MoveCallback;
import com.example.assignment2.Utilities.MoveDetector;
import com.example.assignment2.Utilities.SignalManager;
import com.example.assignment2.Utilities.SoundCrash;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final int COIN_POINT = 5;
    private static long delay;
    private static final long SLOW_DELAY = 1500L; // 1.5 seconds delay for slow mode
    private static final long FAST_DELAY = 400L; // 0.4 seconds delay for fast mode

    private ExtendedFloatingActionButton main_BTN_right;
    private ExtendedFloatingActionButton main_BTN_left;
    private AppCompatImageView[] cars;
    private SoundCrash soundPlayer;
    private AppCompatImageView[] hearts;
    private LinearLayout[] lines_container;
    private int currentLineIndex = 1;
    private AppCompatImageView[][] stones;
    private AppCompatImageView[][] coins;
    private int lives = 3;
    private Handler handler = new Handler();
    public StoneManager stoneManager;
    public CoinManager coinManager;
    private MaterialTextView speed;
    private ExtendedFloatingActionButton slow;
    private ExtendedFloatingActionButton fast;
    private ExtendedFloatingActionButton sensorMode;
    private ExtendedFloatingActionButton arrowsMode;
    private ExtendedFloatingActionButton recordsList;
    private ExtendedFloatingActionButton[] speedButtons;
    private ExtendedFloatingActionButton[] controlButtons;
    private MoveDetector moveDetector;
    private String speedMode;
    private String controlMode;
    private MaterialTextView scoreText;
    private SoundCrash soundCrash;
    private MoveCallback moveCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onCreate", "onCreate: Starting MainActivity");

        setContentView(R.layout.menu);
        findMenuViews();
        initMenuViews();

        SignalManager.init(this);
        stoneManager = new StoneManager(9, 5);
        coinManager = new CoinManager(9, 5);
        soundPlayer = new SoundCrash(this);
        soundCrash = new SoundCrash(this);
        moveDetector = new MoveDetector(this, moveCallback);

        moveDetector.start();

        setupButtonListeners();
    }

    private void setupButtonListeners() {
        // Set up listeners for speed buttons
        for (int i = 0; i < speedButtons.length; i++) {
            int pressed = i;
            speedButtons[i].setOnClickListener(v -> {
                for (int j = 0; j < speedButtons.length; j++) {
                    if (j == pressed) {
                        speedButtons[j].setBackgroundColor(getColor(R.color.pink_300));
                        speedMode = speedButtons[j].getText().toString(); // Use the button's text
                    } else {
                        speedButtons[j].setBackgroundColor(getColor(R.color.pink_100));
                    }
                }
                Log.d("setupButtonListeners", "Speed mode set to: " + speedMode);
                checkAndStartGame();
            });
        }

        // Set up listeners for control buttons
        for (int i = 0; i < controlButtons.length; i++) {
            int pressed = i;
            controlButtons[i].setOnClickListener(v -> {
                for (int j = 0; j < controlButtons.length; j++) {
                    if (j == pressed) {
                        controlButtons[j].setBackgroundColor(getColor(R.color.pink_300));
                        controlMode = controlButtons[j].getText().toString(); // Use the button's text
                    } else {
                        controlButtons[j].setBackgroundColor(getColor(R.color.pink_100));
                    }
                }
                Log.d("setupButtonListeners", "Control mode set to: " + controlMode);
                checkAndStartGame();
            });
        }
    }

    private void checkAndStartGame() {
        if (speedMode != null && controlMode != null) {
            Log.d("checkAndStartGame", "Both modes selected");
            setContentView(R.layout.activity_main);
            findViews();
            initViews();
            startGameWithMode(speedMode, controlMode);
        }
    }

    private void startGameWithMode(String speedMode, String controlMode) {
        Log.d("startGameWithMode", "Starting game with speedMode: " + speedMode + ", controlMode: " + controlMode);
        if (controlMode != null && Objects.equals(controlMode, "arrows")) {
            main_BTN_left.setVisibility(View.VISIBLE);
            main_BTN_right.setVisibility(View.VISIBLE);
            main_BTN_left.setOnClickListener(view -> moveCarLeft());
            main_BTN_right.setOnClickListener(view -> moveCarRight());
        } else if (controlMode != null && Objects.equals(controlMode, "sensor")) {
            initMoveDetector();
            main_BTN_left.setVisibility(View.INVISIBLE);
            main_BTN_right.setVisibility(View.INVISIBLE);

        }

        if (speedMode != null && Objects.equals(speedMode, "slow")) {
            delay = SLOW_DELAY;
        } else {
            delay = FAST_DELAY;
        }

        startFallingStones();
        Log.d("startGameWithMode", "Stones falling at speed: " + delay);
        updateUI(); // update the view
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        moveDetector.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (moveDetector != null) {
            moveDetector.stop();
        }
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (moveDetector != null) {
            moveDetector.stop();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (moveDetector != null) {
            moveDetector.start();
        }
        soundPlayer = new SoundCrash(this);
    }

    protected void startFallingStones() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                stoneManager.updateMatrix();
                coinManager.updateMatrix();
                checkPoint();
                checkCollision();
                updateUI();
                handler.postDelayed(this, delay);
            }
        });
    }

    protected void updateUI() {
        Log.d("updateUI", "updatingUI");

        for (int row = 0; row < stones.length; row++) {
            for (int col = 0; col < stones[row].length; col++) {
                stones[row][col].setVisibility(stoneManager.getMatrix()[row][col] == 1 ? View.VISIBLE : View.INVISIBLE);
            }
        }

        for (int row = 0; row < coins.length; row++) {
            for (int col = 0; col < coins[row].length; col++) {
                coins[row][col].setVisibility(coinManager.getMatrix()[row][col] == 1 ? View.VISIBLE : View.INVISIBLE);
            }
        }
    }

    public void checkPoint() {
        int carCol = currentLineIndex;
        for (AppCompatImageView[] coin : coins) {
            if (coins[coins.length-1][carCol].getVisibility() == View.VISIBLE) {
                //updating score
                coinManager.setScore_points(coinManager.getScore()+COIN_POINT);
                coin[carCol].setVisibility(View.INVISIBLE);
                Log.d("checkPoint", "Score: " + coinManager.getScore());
                updateScoreText();
                break;
            }
        }
    }

    private void updateScoreText() {
        if (scoreText != null) {
            scoreText.setText(String.valueOf(coinManager.getScore()));
        }
    }


    protected void checkCollision() {
        int carCol = currentLineIndex;
        if (stones[stones.length - 1][carCol].getVisibility() == View.VISIBLE) {
            handleCollision();
        }
    }

    private void handleCollision() {
        SignalManager.getInstance().toast("Crash");
        soundCrash.playSound(R.raw.crash_sound);
        SignalManager.getInstance().vibrate(500);

        if (lives > 0) {
            hearts[hearts.length - lives].setVisibility(View.INVISIBLE);
            lives--;
        }
        if (lives == 0) {
            gameOver();
        }
    }

    private void gameOver() {
        handler.removeCallbacksAndMessages(null);
        Intent intent = new Intent(this, GameOver.class);
        intent.putExtra(GameOver.KEY_SCORE, coinManager.getScore());
        Log.d("Main activity gameOver", "score = " + coinManager.getScore());
        intent.putExtra(GameOver.KEY_LATITUDE, 0.0);
        intent.putExtra(GameOver.KEY_LONGITUDE, 0.0);
        startActivity(intent);
        finish();
    }

    private void startGame(String mode) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("GAME_MODE", mode);
        startActivity(intent);
    }

    private void initMoveDetector() {
        Log.d("MoveDetector", "starting sensors");
        moveDetector = new MoveDetector(this,
                new MoveCallback() {
                    @Override
                    public void moveRight() {
                        Log.d("MoveDetector", "moveRight detected");
                         moveCarRight(); // Move car to the right
                    }

                    @Override
                    public void moveLeft() {
                        Log.d("MoveDetector", "moveLeft detected");
                        moveCarLeft(); // Move car to the left
                    }

                    @Override
                    public void moveY(){
                        Log.d("MoveDetector", "moveY detected");
                        moveCarLeft();
                    }
                });
        moveDetector.start();
    }

    private void initMenuViews() {
        slow.setOnClickListener(v -> startGame("slow"));
        fast.setOnClickListener(v -> startGame("fast"));
        sensorMode.setOnClickListener(v -> startGame("sensor"));
        arrowsMode.setOnClickListener(v -> startGame("arrows"));
        recordsList.setOnClickListener(v -> showRecords());
    }

    private void initViews() {
        cars[0].setVisibility(View.INVISIBLE);
        cars[1].setVisibility(View.VISIBLE);
        cars[2].setVisibility(View.INVISIBLE);
        cars[3].setVisibility(View.INVISIBLE);
        cars[4].setVisibility(View.INVISIBLE);

        main_BTN_left.setVisibility(View.VISIBLE);
        main_BTN_right.setVisibility(View.VISIBLE);
        main_BTN_left.setOnClickListener(v -> moveCarLeft());
        main_BTN_right.setOnClickListener(v -> moveCarRight());

        recordsList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScoreMapActivity.class);
            startActivity(intent);
        });
    }

    private void findMenuViews() {
        speedButtons = new ExtendedFloatingActionButton[]{
                findViewById(R.id.slow),
                findViewById(R.id.fast)
        };
        controlButtons = new ExtendedFloatingActionButton[]{
                findViewById(R.id.sensor_mode),
                findViewById(R.id.arrows_mode)
        };

        recordsList = findViewById(R.id.recordsList);

        slow = speedButtons[0];
        fast = speedButtons[1];
        sensorMode = controlButtons[0];
        arrowsMode = controlButtons[1];
    }

    private void findViews() {
        main_BTN_left = findViewById(R.id.main_BTN_left);
        main_BTN_right = findViewById(R.id.main_BTN_right);
        cars = new AppCompatImageView[]{
                findViewById(R.id.main_IMG_car1),
                findViewById(R.id.main_IMG_car2),
                findViewById(R.id.main_IMG_car3),
                findViewById(R.id.main_IMG_car4),
                findViewById(R.id.main_IMG_car5)
        };

        hearts = new AppCompatImageView[]{
                findViewById(R.id.heart1),
                findViewById(R.id.heart2),
                findViewById(R.id.heart3)
        };

        lines_container = new LinearLayout[]{
                findViewById(R.id.line1),
                findViewById(R.id.line2),
                findViewById(R.id.line3),
                findViewById(R.id.line4),
                findViewById(R.id.line5)
        };

        stones = new AppCompatImageView[][]{
                {findViewById(R.id.stone1_1), findViewById(R.id.stone1_2), findViewById(R.id.stone1_3), findViewById(R.id.stone1_4), findViewById(R.id.stone1_5)},
                {findViewById(R.id.stone2_1), findViewById(R.id.stone2_2), findViewById(R.id.stone2_3), findViewById(R.id.stone2_4), findViewById(R.id.stone2_5)},
                {findViewById(R.id.stone3_1), findViewById(R.id.stone3_2), findViewById(R.id.stone3_3), findViewById(R.id.stone3_4), findViewById(R.id.stone3_5)},
                {findViewById(R.id.stone4_1), findViewById(R.id.stone4_2), findViewById(R.id.stone4_3), findViewById(R.id.stone4_4), findViewById(R.id.stone4_5)},
                {findViewById(R.id.stone5_1), findViewById(R.id.stone5_2), findViewById(R.id.stone5_3), findViewById(R.id.stone5_4), findViewById(R.id.stone5_5)},
                {findViewById(R.id.stone6_1), findViewById(R.id.stone6_2), findViewById(R.id.stone6_3), findViewById(R.id.stone6_4), findViewById(R.id.stone6_5)},
                {findViewById(R.id.stone7_1), findViewById(R.id.stone7_2), findViewById(R.id.stone7_3), findViewById(R.id.stone7_4), findViewById(R.id.stone7_5)},
                {findViewById(R.id.stone8_1), findViewById(R.id.stone8_2), findViewById(R.id.stone8_3), findViewById(R.id.stone8_4), findViewById(R.id.stone8_5)},
                {findViewById(R.id.stone9_1), findViewById(R.id.stone9_2), findViewById(R.id.stone9_3), findViewById(R.id.stone9_4), findViewById(R.id.stone9_5)}
        };

        coins = new AppCompatImageView[][]{
                {findViewById(R.id.coin1_1), findViewById(R.id.coin1_2), findViewById(R.id.coin1_3), findViewById(R.id.coin1_4), findViewById(R.id.coin1_5)},
                {findViewById(R.id.coin2_1), findViewById(R.id.coin2_2), findViewById(R.id.coin2_3), findViewById(R.id.coin2_4), findViewById(R.id.coin2_5)},
                {findViewById(R.id.coin3_1), findViewById(R.id.coin3_2), findViewById(R.id.coin3_3), findViewById(R.id.coin3_4), findViewById(R.id.coin3_5)},
                {findViewById(R.id.coin4_1), findViewById(R.id.coin4_2), findViewById(R.id.coin4_3), findViewById(R.id.coin4_4), findViewById(R.id.coin4_5)},
                {findViewById(R.id.coin5_1), findViewById(R.id.coin5_2), findViewById(R.id.coin5_3), findViewById(R.id.coin5_4), findViewById(R.id.coin5_5)},
                {findViewById(R.id.coin6_1), findViewById(R.id.coin6_2), findViewById(R.id.coin6_3), findViewById(R.id.coin6_4), findViewById(R.id.coin6_5)},
                {findViewById(R.id.coin7_1), findViewById(R.id.coin7_2), findViewById(R.id.coin7_3), findViewById(R.id.coin7_4), findViewById(R.id.coin7_5)},
                {findViewById(R.id.coin8_1), findViewById(R.id.coin8_2), findViewById(R.id.coin8_3), findViewById(R.id.coin8_4), findViewById(R.id.coin8_5)},
                {findViewById(R.id.coin9_1), findViewById(R.id.coin9_2), findViewById(R.id.coin9_3), findViewById(R.id.coin9_4), findViewById(R.id.coin9_5)}
        };

        scoreText = findViewById(R.id.scoreText);
    }

    private void moveCarRight() {
        if (currentLineIndex < cars.length - 1) {
            cars[currentLineIndex].setVisibility(View.INVISIBLE);
            currentLineIndex++;
            cars[currentLineIndex].setVisibility(View.VISIBLE);
        }
        Log.d("CarMovement", "Car moved Right");
        updateUI();
    }

    private void moveCarLeft() {
        if (currentLineIndex > 0) {
            cars[currentLineIndex].setVisibility(View.INVISIBLE);
            currentLineIndex--;
            cars[currentLineIndex].setVisibility(View.VISIBLE);
        }
        Log.d("CarMovement", "Car moved Left");
        updateUI();
    }


    private void showRecords() {
        // Your code to show the records list
        Intent intent = new Intent(this, ScoreMapActivity.class);
        startActivity(intent);
    }
}
