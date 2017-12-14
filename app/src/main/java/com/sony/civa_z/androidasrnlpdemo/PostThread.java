package com.sony.civa_z.androidasrnlpdemo;


import android.os.Handler;
import android.util.Log;

import org.json.JSONObject;

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
    private String strUrlPath = "http://192.168.0.121:3010/recognize?timeout=10";
    private int bufferSize = 1024;
    private String data_path = null;
    private Handler mainHandler = null;

    PostThread(String data_path,  Handler mainHandler){
        this.data_path = data_path;
        this.mainHandler = mainHandler;
    }

    @Override
    public void run() {
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
            while(true){
                int len = dos.read(tempBuffer);
                Log.d("PostThread: ", String.valueOf(len));
                if (len > 0) {
                    out.write(tempBuffer);
                }else{
                    break;
                }
            }

            if (conn.getResponseCode() == 200) {
                Log.d("PostThread: ", "200");
                InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                while(true) {
                    byte[] inBuff = new byte[bufferSize];
                    int len = inputStream.read(inBuff);
                    if (len <= 0)
                        break;
                    JSONObject jsonObject = new JSONObject(new String(inBuff, "utf-8"));
                    Log.d("PostThread id : ", jsonObject.getString("id"));
                    Log.d("PostThread  : ", jsonObject.getString("hypotheses"));
                }
            }
        }catch (Exception e){
            Log.d("PostThread err: ", e.getLocalizedMessage());
        }
    }
}