package com.pulingyenara.calculateflash;

import android.os.Bundle;
import android.widget.TextView;
import java.util.Random;
import android.widget.Toast;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AlertDialog;

public class MainActivity extends AppCompatActivity {

    int rightAnswer;
    int flashTime = 0;
    boolean flashIsOpen = false;
    int reward;

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
                        cm.setTorchMode("0", true);
                        flashIsOpen = true;
                    } catch (Exception ex) {}
                }
                if (flashTime <= 0 && flashIsOpen) {
                    try {
                        CameraManager cm =(CameraManager)getSystemService(CAMERA_SERVICE);
                        cm.setTorchMode("0", false);
                        flashIsOpen = false;
                    } catch (Exception ex) {}
                }
            }
        };
        timer.schedule(task, 1000, 1000);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView formula = findViewById(R.id.mainActivity_formula_textView);
        refreshFormula(formula);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("关于")
                    .setMessage("动画片中常见的脑门上亮灯泡，于是做了这个应用\n\n计算正确后剩余时间+15s(加减法)\n+25s(乘除法)\n\n没有乘除，没有原因\n\n人类的智慧将熠熠发光")
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
                flashTime = flashTime + reward;
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
        //0为+，1为-，2为*，3为/
        int algorithm = random.nextInt(4);
        if (algorithm == 0) {
            formula.setText(number1 + "+" + number2);
            rightAnswer = number1 + number2;
            reward = 15;
        }
        if (algorithm == 1) {
            formula.setText(number1 + "-" + number2);
            rightAnswer = number1 - number2;
            reward = 15;
        }
        if (algorithm == 2) {
            formula.setText(number1 + "×" + number2);
            rightAnswer = number1 * number2;
            reward = 25;
        }
        if(algorithm == 3){
            //若除数num2为0，重设num2直到不为0
            while(number2 == 0){
                number2 = random.nextInt(101);
            }
            //若num1不能被num2整除，就将num1随机设为num2的1-100倍
            if(number1 % number2 != 0){
                number1 = number2 * random.nextInt(101);
            }
            formula.setText(number1 + "÷" + number2);
            rightAnswer = number1 / number2;
            reward = 25;
        }
    }
}

