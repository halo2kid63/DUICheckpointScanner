package co.huy.android.duicheckpointscanner;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.tbruyelle.rxpermissions2.RxPermissions;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        new CountDownTimer(1000,1000){
            @Override
            public void onTick(long millisUntilFinished){
            }

            @Override
            public void onFinish(){
                RxPermissions rxPermissions = new RxPermissions(MainActivity.this);

                rxPermissions
                        // ask single or multiple permission once
                        .request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET)
                        .subscribe(granted -> {
                            // All requested permissions are granted
                            if (granted) {
                                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                                startActivity(intent);
                            }
                            // One or all requested permissions are denied
                            else {
                                Toast.makeText(MainActivity.this, "Please allow location service for using app!", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
            }
        }.start();

    }
}