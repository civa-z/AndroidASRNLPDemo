package com.sony.civa_z.androidasrnlpdemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by 5109U11454 on 2017/12/20.
 */

public class PackageControler {
    private Activity mainActivity;
    public List<String> pName = null;
    PackageManager packageManager = null;
    PackageControler(Activity mainActivity){
        this.mainActivity = mainActivity;

        packageManager = mainActivity.getPackageManager();//获取packagemanager
        List< PackageInfo> pinfo = packageManager.getInstalledPackages(0);//获取所有已安装程序的包信息
        pName = new ArrayList<String>();//用于存储所有已安装程序的包名
        //从pinfo中将包名字逐一取出，压入pName list中
        if(pinfo != null){
            for(int i = 0; i < pinfo.size(); i++){
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
    }

    void activePackage(String category, String utterance){
        String category_ = category.split("_")[0].toLowerCase();
        boolean isFound = false;

        for (String name : pName){
            if(name.contains(category_)){
                try {
                    Intent i = packageManager.getLaunchIntentForPackage(name);
                    this.mainActivity.startActivityForResult(i, RESULT_OK);
                }
                catch (Exception e){
                    continue;
                }
                isFound = true;
                break;
            }
        }
        if (!isFound) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri uri = Uri.parse("http://www.baidu.com/s?wd=" + utterance);
            intent.setData(uri);
            this.mainActivity.startActivity(intent);
        }
        //if (pName.contains(s)){

        //}
        /*
        else {//未安装，跳转至market下载该程序
            Uri uri = Uri.parse("market://details?id=com.skype.android.verizon");//id为包名
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            this.mainActivity.startActivity(it);
        }*/

    }
}
