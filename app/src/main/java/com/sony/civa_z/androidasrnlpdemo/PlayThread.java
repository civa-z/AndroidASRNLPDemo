package com.sony.civa_z.androidasrnlpdemo;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.DataInputStream;
import java.io.FileInputStream;

/**
 * Created by 5109U11454 on 2017/12/12.
 */

public class PlayThread extends Thread {
    private String pcm_file;
    public PlayThread(String pcm_file){
        this.pcm_file = pcm_file;
    }
    @Override
    public void run(){
        int bufferSize = AudioTrack.getMinBufferSize(16000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        AudioTrack audioTrack= new AudioTrack(AudioManager.STREAM_MUSIC, 16000, AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);
        audioTrack.play();
        DataInputStream dos = null;
        try {
            dos = new DataInputStream(new FileInputStream(pcm_file));
            byte[] tempBuffer = new byte[bufferSize];
            while (true) {
                int len = dos.read(tempBuffer);
                if (len > 0) {
                    Log.d("PlayThread: ", String.valueOf(len));
                    audioTrack.write(tempBuffer, 0, tempBuffer.length);
                } else {
                    Log.d("PlayThread: ", "Finish");
                    break;
                }
            }
            dos.close();
        }catch (Exception e){
        }finally {
            audioTrack.stop();
            audioTrack.release();
        }
    }
}
