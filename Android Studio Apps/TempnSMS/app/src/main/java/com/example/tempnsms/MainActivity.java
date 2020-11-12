package com.example.tempnsms;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
//import android.util.Log;

//  Working code - TempSensor
//https://www.youtube.com/watch?v=JKuTnuUsKOI&ab_channel=SarthiTechnology

//  Working code - SMS
//https://www.youtube.com/watch?v=pajvuBZc2WA&t=103s&ab_channel=ProgrammerWorld

public class MainActivity extends AppCompatActivity implements SensorEventListener
{

    private TextView textView;
    private SensorManager sensorManager;
    private Sensor tempSensor; // making an object
    private boolean isTempSensorAvailable;

    private EditText editTextNumber;
    private EditText editTextMessage;

    public Float tempReading;

    CountDownTimer mCountDownTimer;
    public long timeLeftinMilliSeconds = 900000; // 15 mins
    public long countDownInterval = 420000;
    public boolean tempFlag = false;
    public int tempSetPoint = 30;

    public int hr, min, sec;

    public int intBatteryLevel;

    static String messages [] = new String[100];

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("On create");

        textView = findViewById(R.id.textView);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) !=null)
        {
            tempSensor  = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            isTempSensorAvailable = true;
        }else{
            textView.setText("Temp sensor is unavailable!");
            isTempSensorAvailable = false;
        }


        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.SEND_SMS},
                PackageManager.PERMISSION_GRANTED);

//        editTextMessage = findViewById(R.id.editText);
//        editTextNumber  = findViewById(R.id.editTextNumber);


        // Battery level Checking
        BroadcastReceiver broadcastReceiverLevel = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                intBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                if (intBatteryLevel < 20)
                {
                    sendSMS(2);
                }
            }
        };

        registerReceiver(broadcastReceiverLevel, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        textView.setText(sensorEvent.values[0]+ " °C");
        tempReading = sensorEvent.values[0];
//        System.out.println(sensorEvent.values[0]);

        DecimalFormat deci = new DecimalFormat("#.##");
        tempReading = Float.valueOf(deci.format(sensorEvent.values[0]));

        if ( tempReading > tempSetPoint && (tempFlag == false))
        {
            tempFlag = true;
            System.out.println(timeRecorded());
            startTimer();
        }
//        else
//        {
//            stopTimer();
//        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isTempSensorAvailable) {
            // register?
            sensorManager.registerListener(this, tempSensor, sensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isTempSensorAvailable)
        {
            sensorManager.registerListener(this, tempSensor, sensorManager.SENSOR_DELAY_NORMAL);
//            sensorManager.unregisterListener(this);
        }
    }

    public void sendSMS(int x)
    {
        if (x == 1)
        {
            String messages [] = {"Check the pumps immediately in 272!. Current temp is: " + tempReading + " °C", "Timestamp: " + timeRecorded()}; //"Check the pumps immediately in 272!. Current temp is: " + tempReading + " °C" ; //+ " Timestamp: " + timeRecorded(); //editTextMessage.getText().toString();
        }
        else if (x == 2)
        {
           String messages [] = {"272 device battery level is: " + intBatteryLevel + "%"};
        }

//        String numbers ="07886961329";
        String numbers [] ={"07886961329","07832750629", "07459192223"} ; //editTextNumber.getText().toString(); //07459192223//07832750629

        SmsManager mySmsManager = SmsManager.getDefault();

        for (String number: numbers)
        {
            for (String message : messages)
            {
                mySmsManager.sendTextMessage(number, null, message , null, null);
            }
        }
//        String numbers [] ={"07459192223","07832750629"} ; //editTextNumber.getText().toString(); //07459192223//07832750629

//            SmsManager mySmsManager = SmsManager.getDefault();
//            for (String number : numbers)
//            {
//                mySmsManager.sendTextMessage(number, null, message , null, null);
//            }
//        String numbers ="07886961329";
//        SmsManager mySmsManager = SmsManager.getDefault();
//        mySmsManager.sendTextMessage(numbers, null, message , null, null);
    }
    public void startTimer()
    {
        mCountDownTimer = new CountDownTimer(timeLeftinMilliSeconds, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftinMilliSeconds = millisUntilFinished;
                sendSMS(1);
            }

            @Override
            public void onFinish() {
                stopTimer();
                System.out.println(("send_sms"));
//                mCountDownTimer.cancel();
                tempFlag = false;

//                String message  = "Check the pumps immediately in 272!. Current temp is: " + tempReading + " °C";
//                String number2  = "07832750629";
//                SmsManager mySmsManager = SmsManager.getDefault();
//                mySmsManager.sendTextMessage(number2, null, message , null, null);
            }
        }.start();
    }

    public void stopTimer()
    {
        mCountDownTimer.cancel();
    }

    public String timeRecorded()
    {
        Date now = new Date();
        long timestamp = now.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String dateStr = sdf.format(timestamp);
        return dateStr;
    }

}