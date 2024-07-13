package com.example.assignment2.Utilities;

public class ScoreData implements Comparable<ScoreData>{
    private String name;
    private int score;
    private double latitude;
    private double longitude;

    public ScoreData(String name, int score, double latitude, double longitude) {
        this.name = name;
        this.score = score;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public int compareTo(ScoreData other) {
        return Integer.compare(other.score, this.score);
    }
}
