package com.sony.civa_z.androidasrnlpdemo;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private AudioRecordManager audioRecordManager  = null;
    private TextView status = null;
    private Button record_botton = null;
    private File dir = null;
    private PcmToWavUtil pcmToWavUtil = null;
    private String pcm_file = null;
    private String wav_file = null;
    private Handler myhandler = null;
    private PostThread postThread = null;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        record_botton = findViewById(R.id.recordButton);
        status = findViewById(R.id.Status);
        audioRecordManager = AudioRecordManager.getInstance();

        dir = new File(Environment.getExternalStorageDirectory(),"sounds");
        pcm_file = dir.getAbsolutePath() + "/record.pcm";
        wav_file = pcm_file.replace(".pcm", ".wav");

        pcmToWavUtil = new PcmToWavUtil(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        record_botton.setOnTouchListener(new MyOnTouchListener());
        myhandler = new MyHandler();
        postThread = new PostThread(wav_file, myhandler);
    }

    private class MyOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //start record
                status.setText("onTouch down");
                postThread.interrupt();
                audioRecordManager.startRecord(pcm_file);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                //stop record
                status.setText("onTouch up");
                audioRecordManager.stopRecord();
                pcmToWavUtil.pcmToWav(pcm_file, wav_file);
                postThread.start();
                PlayThread playThread = new PlayThread(pcm_file);
                playThread.start();
            }
            return true;
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 1:
                    status.setText("Send start");
                    break;
                case 2:
                    status.setText("Send end");
                    break;
                case 3:
                    status.setText("Receive responds");
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }
}
