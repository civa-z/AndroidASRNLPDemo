package com.sony.civa_z.androidasrnlpdemo;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private AudioRecordManager audioRecordManager  = null;
    private TextView status = null;
    private Button record_botton = null;
    private File dir = null;
    private PcmToWavUtil pcmToWavUtil = null;
    private String pcm_file = null;
    private String wav_file = null;

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

                    pcmToWavUtil.pcmToWav(pcm_file, wav_file);

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
