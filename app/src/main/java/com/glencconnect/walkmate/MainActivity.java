package com.glencconnect.walkmate;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.glencconnect.walkmate.model.Data;

import java.util.Locale;

/**
 * Created by glenc on 26 Sept 2020
 **/
public class MainActivity extends AppCompatActivity implements SensorEventListener {


    private TextView sensorText;
    private Sensor accelerometerSensor;
    private SensorManager sensorManager;
    private TextView textX, textY, textZ;
    private EditText write;
    private float currentX,currentY,currentZ,lastX,lastY,lastZ;
    private float xDiff;
    private float yDiff;
    private float zDiff;
    private float shakeRatio = 5f;
    private Vibrator vibrator;
    private boolean isSensorAvalaible = false;
    private boolean isAnotherCall = false;
    private TextToSpeech ttObj;
    private boolean isShaking = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        write = findViewById(R.id.write);

        //sensor views
        textX = findViewById(R.id.x_value);
        textY = findViewById(R.id.y_value);
        textZ = findViewById(R.id.z_value);
        sensorText = findViewById(R.id.sensorList);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //get list of sensors on your system
//        List<Sensor> list = sensorManager.getSensorList(Sensor.TYPE_ALL);
//
//        StringBuilder data = new StringBuilder();
//        for (Sensor sensor : list){
//            data.append(sensor.getName() + "\n");
//            data.append(sensor.getVersion() + "\n");
//            data.append(sensor.getVendor() + "\n");
//        }
//
//        sensorText.setText(data + Data.getComplaint());

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isSensorAvalaible = true;
        }else{
            sensorText.setText("sensor Accelerometer not found");
            isSensorAvalaible = false;
        }

        ttObj = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if (status != TextToSpeech.ERROR){
                    ttObj.setLanguage(Locale.UK);
                    ttObj.setPitch(0f);


                }
            }
        });


    }

    public void speakText(View view){
        String toSpeak = write.getText().toString();
        ttObj.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null,null);



    }

    @Override
    public void onSensorChanged(SensorEvent event) {


        textX.setText(event.values[0] + "ms");
        textY.setText(event.values[1] + "ms");
        textZ.setText(event.values[2] + "ms");

        currentX = event.values[0];
        currentY = event.values[1];
        currentZ = event.values[2];

        if (isAnotherCall){
            xDiff = Math.abs(lastX - currentX);
            yDiff = Math.abs(lastY - currentY);
            zDiff = Math.abs(lastZ - currentZ);

            if((xDiff > shakeRatio && yDiff >shakeRatio) ||
                    (xDiff > shakeRatio && zDiff > shakeRatio)||
                    (yDiff > shakeRatio && zDiff >shakeRatio)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    vibrator.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
                }else{
                    vibrator.vibrate(500);
                }

                isShaking = true;
                if (isShaking && !ttObj.isSpeaking())
                    ttObj.speak(Data.getComplaint(),TextToSpeech.QUEUE_FLUSH,null,null);
//                else
//                    ttObj.speak(Data.getConcession(),TextToSpeech.QUEUE_FLUSH,null,null);
            }
        }else{

            vibrator.cancel();
            ttObj.speak(Data.getConcession(), TextToSpeech.QUEUE_FLUSH, null, null);

        }

        lastX = currentX;
        lastY = currentY;
        lastZ = currentZ;
        isAnotherCall = true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {



    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSensorAvalaible){
            sensorManager.registerListener(this,accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isSensorAvalaible){
            sensorManager.unregisterListener(this);
        }
        if (ttObj != null){
            ttObj.shutdown();
            ttObj.stop();
        }
    }
}
