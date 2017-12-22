package com.sony.civa_z.androidasrnlpdemo;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 5109U11454 on 2017/12/7.
 *
 * http://www.cnblogs.com/smyhvae/p/4006009.html
 *
 */

//子线程：使用POST方法向服务器发送用户名、密码等数据
class PostThread extends Thread {
    private String strUrlPath = "http://192.168.0.121:3010/recognize?timeout=50";
    private int bufferSize = 1024;
    private String data_path = null;
    private Handler mainHandler = null;
    public Handler postHandler = null;

    PostThread(String data_path,  Handler mainHandler){
        this.data_path = data_path;
        this.mainHandler = mainHandler;
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void run() {
        super.run();
        Looper.prepare();
        postHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                sendPostMsg();
            }
        };
        Looper.loop();
    }
    private void sendPostMsg(){

        try {
            Log.d("PostThread: ", "start");
            URL url = new URL(strUrlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "audio/x-wav; rate=16000");
            conn.setDoOutput(true); //如果要输出，则必须加上此句

            OutputStream out = conn.getOutputStream();

            DataInputStream dos = new DataInputStream(new FileInputStream(data_path));
            Log.d("PostThread data_path: ", data_path);
            byte[] tempBuffer = new byte[bufferSize];
            notify(1, null);
            while(true){
                int len = dos.read(tempBuffer);
                Log.d("PostThread: ", String.valueOf(len));
                if (len > 0) {
                    out.write(tempBuffer);
                }else{
                    break;
                }
            }
            notify(2, null);
            if (conn.getResponseCode() == 200) {
                Log.d("PostThread: ", "200");
                InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                String result = "";
                while(true) {
                    byte[] inBuff = new byte[bufferSize];
                    int len = inputStream.read(inBuff);
                    if (len <= 0)
                        break;
                    result = result + new String(inBuff, "utf-8");
                    /*
                    JSONObject jsonObject = new JSONObject(new String(inBuff, "utf-8"));
                    Log.d("PostThread id : ", jsonObject.getString("id"));
                    Log.d("PostThread  : ", jsonObject.getString("hypotheses"));
                    */
                }
                notify(3, result);
            }
        }catch (Exception e){
            Log.d("PostThread err: ", e.getLocalizedMessage());
            notify(-1, e.getLocalizedMessage());
        }
    }
    private void notify(int i, String s){
        Message resultMsg = new Message();
        resultMsg.what = i;
        resultMsg.obj = s;
        mainHandler.sendMessage(resultMsg);
    }
}