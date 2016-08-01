package com.fynydd.trucktracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
// import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private float mLastX, mLastY, mLastZ;
    private boolean mInitialized;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private final float NOISE = (float) 1.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TextView textTextView;
        Intent batteryStatus;
        IntentFilter iUSBfilter;
        ImageView image;
        Context context;
        context = this;

        iUSBfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = context.registerReceiver(null, iUSBfilter);

        // Had to move super line below above the setContent line or we get
        // java.lang.IllegalArgumentException: AppCompat does not support the current
        // theme features
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        image = (ImageView) findViewById(R.id.USBimageView);
        //    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //    setSupportActionBar(toolbar);

        switch (batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                //        Toast.makeText(context, "Battery plugged AC", Toast.LENGTH_LONG).show();
                image.setImageResource(R.drawable.greensmallled);
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                //        Toast.makeText(context, "Battery plugged USB", Toast.LENGTH_LONG).show();
                image.setImageResource(R.drawable.greensmallled);
                break;
            default:
                //       Toast.makeText(context, "Battery NOT plugged", Toast.LENGTH_LONG).show();
                image.setImageResource(R.drawable.redsmallled);
                break;
        }

        mInitialized = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }


    private BroadcastReceiver myUSBBroadcastReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                    intent = context.registerReceiver(null, filter);
                    setContentView(R.layout.activity_main);
                    ImageView image = (ImageView) findViewById(R.id.USBimageView);
                    switch (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
                        case BatteryManager.BATTERY_PLUGGED_AC:
                            image.setImageResource(R.drawable.greensmallled);
                            break;
                        case BatteryManager.BATTERY_PLUGGED_USB:
                            image.setImageResource(R.drawable.greensmallled);
                            break;
                        default:
                            image.setImageResource(R.drawable.redsmallled);
                            break;
                    }
                }
            };

    @Override
    public void onSensorChanged(SensorEvent event) {
        TextView tvX= (TextView)findViewById(R.id.x_axis);
        TextView tvY= (TextView)findViewById(R.id.y_axis);
        TextView tvZ= (TextView)findViewById(R.id.z_axis);
       // ImageView iv = (ImageView)findViewById(R.id.arrowimage);
        ImageView moveEvent = (ImageView)findViewById(R.id.MoveImageView);

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if (!mInitialized) {
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            tvX.setText("0.0");
            tvY.setText("0.0");
            tvZ.setText("0.0");
            mInitialized = true;
        } else {
            float deltaX = Math.abs(mLastX - x);
            float deltaY = Math.abs(mLastY - y);
            float deltaZ = Math.abs(mLastZ - z);
            if (deltaX < NOISE) deltaX = (float)0.0;
            if (deltaY < NOISE) deltaY = (float)0.0;
            if (deltaZ < NOISE) deltaZ = (float)0.0;
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            tvX.setText(Float.toString(deltaX));
            tvY.setText(Float.toString(deltaY));
            tvZ.setText(Float.toString(deltaZ));
       //     iv.setVisibility(View.VISIBLE);
            if (deltaX > deltaY) {
                moveEvent.setImageResource(R.drawable.greensmallled);

            } else if (deltaY > deltaX) {
                moveEvent.setImageResource(R.drawable.greensmallled);

            } else {
                moveEvent.setImageResource(R.drawable.redsmallled);
            }
        }
    }

    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(myUSBBroadcastReceiver, filter);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        unregisterReceiver(myUSBBroadcastReceiver);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // can be safely ignored for this demo
    }


    // @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
