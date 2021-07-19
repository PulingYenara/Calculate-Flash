package com.pulingyenara.calculateflash;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import java.util.Random;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.content.Intent;
import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

	int rightAnswer;
	int flashTime = 0;
	boolean flashIsOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		Timer timer = new Timer();
		TimerTask task = new TimerTask(){
			@Override
			public void run() {
				flashTime = flashTime - 1;
				MainActivity.this.runOnUiThread(new Runnable(){
						@Override
						public void run() {
							TextView timeShow = findViewById(R.id.mainActivity_timeShow_textView);
							timeShow.setText(String.valueOf(flashTime));
						}
					});
				if (flashTime > 0 && !flashIsOpen) {
					try {
						CameraManager cm =(CameraManager)getSystemService(CAMERA_SERVICE);
						cm.setTorchMode("0",true);
						flashIsOpen = true;
					} catch (Exception ex) {}
				}
				if(flashTime <= 0 && flashIsOpen){
					try {
						CameraManager cm =(CameraManager)getSystemService(CAMERA_SERVICE);
						cm.setTorchMode("0",false);
						flashIsOpen = false;
					} catch (Exception ex) {}
				}
			}
		};
		timer.schedule(task, 1000, 1000);

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

		Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		TextView formula = findViewById(R.id.mainActivity_formula_textView);
		refreshFormula(formula);

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu,menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.about:
				AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("关于")
                    .setMessage("动画片中常见的脑门上亮灯泡，于是做了这个应用\n\n计算正确后剩余时间+15s\n\n没有乘除，没有原因\n\n人类的智慧将熠熠发光")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dia, int which) {
							dia.dismiss();
						}
                    })
                    .create();
				dialog.show();
		}
		return super.onOptionsItemSelected(item);
	}

	public void sa(View v) {
		TextView timeShow = findViewById(R.id.mainActivity_timeShow_textView);
		TextView formula = findViewById(R.id.mainActivity_formula_textView);
		EditText inputResult = findViewById(R.id.mainActivity_calculateResult_editText);
		flashTime = Integer.parseInt(timeShow.getText().toString());
		if (inputResult.getText().toString().isEmpty()) {
			Toast.makeText(this, "未输入", Toast.LENGTH_SHORT).show();
		} else {
			int result = Integer.parseInt(inputResult.getText().toString());

			if (result == rightAnswer) {
				flashTime = flashTime + 15;
				timeShow.setText(String.valueOf(flashTime));
			}
			refreshFormula(formula);
		}
	}

	void refreshFormula(TextView formula) {
		Random random = new Random();
		//第一个被操作数
		int number1 = random.nextInt(101);
		//第二个被操作数
		int number2 = random.nextInt(101);
		//运算法则
		//0为+，1为-，2为*，除法容易出问题
		int algorithm = random.nextInt(3);
		if (algorithm == 0) {
			formula.setText(number1 + "+" + number2);
			rightAnswer = number1 + number2;
		}
		if (algorithm == 1) {
			formula.setText(number1 + "-" + number2);
			rightAnswer = number1 - number2;
		}
		if (algorithm == 2) {
			formula.setText(number1 + "×" + number2);
			rightAnswer = number1 * number2;
		}
	}
}

