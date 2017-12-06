package com.sony.civa_z.androidasrnlpdemo;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sony.civa_z.androidasrnlpdemo.AudioRecordManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private AudioRecordManager audioRecordManager  = null;
    private TextView status = null;
    Button record_botton = null;
    File dir = null;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        record_botton = findViewById(R.id.recordButton);
        status = findViewById(R.id.Status);
        audioRecordManager = AudioRecordManager.getInstance();
        dir = new File(Environment.getExternalStorageDirectory(),"sounds");
        record_botton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //start record
                    status.setText("onTouch down");
                    audioRecordManager.startRecord(dir.getAbsolutePath() + "/record.pcm");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //stop record
                    status.setText("onTouch up");
                    audioRecordManager.stopRecord();
                    try {
                        MainActivity.play(dir.getAbsolutePath() + "/record.pcm");
                    } catch (IOException e) {
                        Log.d("Play", "Err1");
                        e.printStackTrace();
                    } catch (Throwable throwable) {
                        Log.d("Play", "Err2");
                        throwable.printStackTrace();
                    }
                }
                return true;
            }
        });
    }
    public static void play(String path) throws Throwable {
        int bufferSize = AudioTrack.getMinBufferSize(16000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        AudioTrack audioTrack= new AudioTrack(AudioManager.STREAM_MUSIC, 16000, AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);
        audioTrack.play();
        DataInputStream dos = new DataInputStream(new FileInputStream(path));
        byte[] tempBuffer = new byte[bufferSize];
        int offset = 0;
        while(true){
            int len = dos.read(tempBuffer);
            if (len > 0) {
                Log.d("Play", String.valueOf(len));
                audioTrack.write(tempBuffer, 0, tempBuffer.length);
            }else{
                break;
            }
        }
        dos.close();
        audioTrack.stop();
        audioTrack.release();
    }

}
