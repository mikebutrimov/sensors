package org.unhack.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    //sensors
    private SensorManager mSensorManager;
    private Sensor mSensor;
    double x,y,z,last_x,last_y,last_z = 0;
    ArrayList<double[]> accel_readings = new ArrayList<>();
    ArrayList<Double> accel_history = new ArrayList<>();
    private double DOOR_THRESHOLD = 0.5;
    private boolean stopByAccel = false;
    private boolean securityEnabledByTiming = false;
    private static int SEC_TIMING_THRESHOLD = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //sensors
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        mSensor = null;
        for (Sensor sensor: deviceSensors){
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                mSensor = sensor;
                Log.d("Sensor :", mSensor.getName() + " found and was set up");
            }
        }
        if (mSensor == null){
            //process without accelerometer start
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        mSensorManager.registerListener(this,mSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mSensorManager.unregisterListener(this);
    }


    //sensors part
    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        if (true) { // due security forced to be enabled every fucking time

            //Log.d("Sensor reads: ", String.valueOf(event.values[0]) + " " +
            //                        String.valueOf(event.values[1]) + " " +
            //                        String.valueOf(event.values[2]));

            //float sensors_avg = abs(event.values[0] + event.values[1] + event.values[2]);
            //Log.d("Sensor abs reads: ", String.valueOf(sensors_avg));
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];
            double[] buf = {z,y,z};
            accel_readings.add(buf);

            if (last_x == 0 || last_y == 0 || last_z == 0) {
                last_x = x;
                last_z = z;
                last_y = y;
            }

            double last_reading = Math.abs(x + y + z);

            double prev_reading = Math.abs(last_x + last_y + last_z);
            accel_history.add(prev_reading);

            for (double reading: accel_history){
                if ((last_reading - prev_reading) > DOOR_THRESHOLD) {
                    Log.d("QAZWSX", "new code working");
                }
            }

            if (Math.abs(last_reading - prev_reading) > DOOR_THRESHOLD) {
                Log.d("Sensors", "Door shake");
            }

            last_x = x;
            last_y = y;
            last_z = z;
        }


    }
}
