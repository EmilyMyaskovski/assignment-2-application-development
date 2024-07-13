package com.example.assignment2;

import android.os.Handler;
import java.util.Random;

public class CoinManager {

    private int[][] matrix;
    private int rows;
    private int cols;
    private Random random = new Random();
    private int[] currentCoinRow;
    private boolean firstIteration;
    private int coinRow;
    private int coinCol;
    private boolean coinVisible;
    private Handler handler = new Handler();
    private int score_points = 0;

    public CoinManager(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        matrix = new int[rows][cols];
        currentCoinRow = new int[cols];
        firstIteration = true;
        resetCoin();
    }

   public int[][] getMatrix() {
        return matrix;
    }


    protected void updateMatrix() {
        // Clear the matrix
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                matrix[row][col] = 0;
            }
        }

        // Move stones down one row in each column if not at the bottom
        for (int col = 0; col < cols; col++) {
            if (currentCoinRow[col] >= 0) {
                if (currentCoinRow[col] < rows - 1) {
                    currentCoinRow[col]++;
                } else {
                    currentCoinRow[col] = -1; // Stone has reached the bottom
                }
            }
        }

        // Move coin down one row if not at the bottom
        if (coinVisible && coinRow < rows - 1) {
            coinRow++;
        } else {
            coinVisible = false;
        }

        // Move coin down one row if not at the bottom
        if (coinVisible && coinRow < rows - 1) {
            coinRow++;
        } else {
            coinVisible = false;
        }

        // Set stones at current positions
        for (int col = 0; col < cols; col++) {
            if (currentCoinRow[col] >= 0) {
                matrix[currentCoinRow[col]][col] = 1;
            }
        }

        // Check if stones have fallen down
        boolean fallingDown = true;
        for (int col = 0; col < cols; col++) {
            if (currentCoinRow[col] != -1) {
                fallingDown = false;
                break;
            }
        }

        if (fallingDown) {
            resetStones();
        }

        // Reset coin if it has fallen
        if (!coinVisible) {
            resetCoin();
        }
    }

    public int getScore(){
        return score_points;
    }

    public void setScore_points(int score_points) {
        this.score_points = score_points;
    }

    protected void resetStones() {
        if (firstIteration) {
            firstIteration = false;
            // Initialize currentStoneRow to -1
            for (int i = 0; i < cols; i++) {
                currentCoinRow[i] = -1;
            }
        }

        // Select 2 out of the columns
        int firstColumn = random.nextInt(cols);
        int secondColumn;
        do {
            secondColumn = random.nextInt(cols);
        } while (secondColumn == firstColumn);

        currentCoinRow[firstColumn] = 0;
        currentCoinRow[secondColumn] = 0;
    }

    protected void resetCoin() {
        coinRow = 0;
        coinCol = random.nextInt(cols);
        coinVisible = true;
    }
}
