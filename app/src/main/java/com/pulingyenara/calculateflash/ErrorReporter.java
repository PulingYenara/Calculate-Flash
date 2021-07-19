package com.pulingyenara.calculateflash;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class ErrorReporter extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_error);
        
		Thread.UncaughtExceptionHandler ueh = new Thread.UncaughtExceptionHandler(){
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Intent i = new Intent();
                i.setClass(getApplicationContext(), ErrorReporter.class);
                i.putExtra("error", e.getMessage());
                startActivity(i);
            }
        };
        Thread.setDefaultUncaughtExceptionHandler(ueh);

        Intent i = getIntent();
        String error = i.getStringExtra("error");
        TextView tv = findViewById(R.id.activityerror_errorDetail_TextView);
        tv.setText(error);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
    }
    
}
