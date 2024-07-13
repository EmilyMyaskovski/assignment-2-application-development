package com.example.assignment2.Utilities;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import com.example.assignment2.Interfeces.MoveCallback;

public class MoveDetector {
    private static final double MOVE_DIFFERENCE = 5.0;
    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener sensorEventListener;

    private long timestamp = 0l;
    private MoveCallback moveCallback;

    public MoveDetector(Context context, MoveCallback moveBackCall) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.moveCallback = moveBackCall;
        initEventListener();
    }


    private void initEventListener() {
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                calculateMove(x, y);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                //pass
            }
        };
    }

    public void calculateMove(float x, float y) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - timestamp > 500) {
            timestamp = currentTime;

            if (moveCallback != null) {
                if (x > MOVE_DIFFERENCE) {
                    moveCallback.moveLeft();
                } else if (x < -MOVE_DIFFERENCE) {
                    moveCallback.moveRight();
                } else if (y > MOVE_DIFFERENCE) {
                    moveCallback.moveY();
                }
            }
        }
    }

    public void start() {
        sensorManager.registerListener(
                sensorEventListener,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        sensorManager.unregisterListener(
                sensorEventListener,
                sensor);
    }
}
