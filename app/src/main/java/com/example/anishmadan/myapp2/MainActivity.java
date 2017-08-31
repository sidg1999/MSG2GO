package com.example.anishmadan.myapp2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;

import java.util.Calendar;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    ArrayList<Alarm> alarms = new ArrayList<Alarm>();
    Button button;
    private LocationManager locationManager;
    private LocationListener locationListener;
    EditText editText1, editText2, editText3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.SEND_SMS)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.SEND_SMS}, 1);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.SEND_SMS}, 1);
            }
        } else {
            //do nothing
        }

        button = (Button) findViewById(R.id.button);
        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                String time = editText1.getText().toString();
                final String number = editText2.getText().toString();
                final String sms = editText3.getText().toString();
                int hrs = Integer.parseInt(time.charAt(0) + "") * 10 + Integer.parseInt(time.charAt(1) + "");
                int mins = Integer.parseInt(time.charAt(3) + "") * 10 + Integer.parseInt(time.charAt(4) + "");
                long militime = (mins + hrs * 60) * 60 * 1000;

                Calendar rightNow = Calendar.getInstance();
                long offset = rightNow.get(Calendar.ZONE_OFFSET) +
                        rightNow.get(Calendar.DST_OFFSET);
                long currentTime = (rightNow.getTimeInMillis() + offset) %
                        (24 * 60 * 60 * 1000);

                long timeTillRing = militime-currentTime;
                    Toast.makeText(MainActivity.this, "Your message sends in " + (int)(timeTillRing/1000/60) +" minutes", Toast.LENGTH_SHORT).show();
                    alarms.add(new Alarm(number, sms, timeTillRing));
            }
        });
    }

    //handler loops every 30 seconds
    Handler h = new Handler();
    int delay = 30000; //every 30 seconds
    Runnable runnable;
    @Override
    protected void onStart() {
//start handler as activity become visible
        h.postDelayed(new Runnable() {
            public void run() {
                //do something
                for(int i = alarms.size()-1; i>=0; i--){
                    Alarm alarm = alarms.get(i);
                    alarm.timeTillRing-=30000;
                    if(alarm.timeTillRing>-15000 && alarm.timeTillRing<15000 || alarm.timeTillRing<-15000){
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(alarm.number, null, alarm.msg, null, null);
                            Toast.makeText(MainActivity.this, "Sent!", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                        }
                        alarms.remove(i);
                    }
                }
                runnable=this;
                h.postDelayed(runnable, delay);
            }
        }, delay);
        super.onStart();
    }

    @Override
    protected void onPause() {
        h.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "No permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}