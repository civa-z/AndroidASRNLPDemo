package com.sony.civa_z.androidasrnlpdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private AudioRecordManager audioRecordManager  = null;
    private TextView result = null;
    private TextView status = null;
    private TextView ip_text = null;
    private Button record_botton = null;
    private Button ip_set_button = null;
    private File dir = null;
    private PcmToWavUtil pcmToWavUtil = null;
    private String pcm_file = null;
    private String wav_file = null;
    private Handler myhandler = null;
    private PostThread postThread = null;
    private PackageControler packageControler = null;
    private String default_ip_address = "192.168.0.121";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        record_botton = findViewById(R.id.recordButton);
        record_botton.setOnTouchListener(new MyOnTouchListener());

        ip_set_button = findViewById(R.id.ip_set_button);
        ip_set_button.setOnClickListener(new MyIpSetListener());

        ip_text = findViewById(R.id.ip_text);
        ip_text.setText(default_ip_address);

        status = findViewById(R.id.Status);
        result = findViewById(R.id.result);
        audioRecordManager = AudioRecordManager.getInstance();
        packageControler = new PackageControler(this);

        dir = new File(Environment.getExternalStorageDirectory(),"sounds");
        pcm_file = dir.getAbsolutePath() + "/record.pcm";
        wav_file = pcm_file.replace(".pcm", ".wav");

        pcmToWavUtil = new PcmToWavUtil(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

        myhandler = new MyHandler();
        postThread = new PostThread(wav_file, myhandler);
        postThread.setIp(default_ip_address);
        postThread.start();
    }

    private class MyOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //start record
                status.setText("Touch down");
                audioRecordManager.startRecord(pcm_file);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                //stop record
                status.setText("Touch up");
                record_botton.setEnabled(false);
                audioRecordManager.stopRecord();
                pcmToWavUtil.pcmToWav(pcm_file, wav_file);
                postThread.postHandler.sendMessage(new Message());
                PlayThread playThread = new PlayThread(pcm_file);
                playThread.start();
            }
            return true;
        }
    }

    private class MyIpSetListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            String new_ip = ip_text.getText().toString();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(ip_text.getWindowToken(), 0) ;

            status.setText("Set new IP: " + new_ip);
            postThread.setIp(new_ip);
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case -1:
                    status.setText("Send error: " + msg.obj.toString());
                    record_botton.setEnabled(true);
                    break;
                case 1:
                    status.setText("Send start");
                    break;
                case 2:
                    status.setText("Send end");
                    break;
                case 3:
                    status.setText("Receive responds");
                    String resultMsg = msg.obj.toString();
                    ResultParser resultParser = new ResultParser(resultMsg);
                    if (resultParser.analysis()){
                        result.setText(resultParser.utterance + "\n" + resultParser.Intention_List[resultParser.id - 1]);
                        packageControler.activePackage(resultParser.Intention_List[resultParser.id - 1], resultParser.utterance);
                    } else{
                        result.setText(resultParser.utterance + "\n" + "Error");
                    }
                    record_botton.setEnabled(true);
                    break;
                default:
                    break;
            }

            super.handleMessage(msg);
        }
    }
}
