package com.sony.civa_z.androidasrnlpdemo;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 5109U11454 on 2017/12/20.
 */

public class ResultParser {
    private String s;
    String host_id;
    String utterance;
    int id;
    private int status;
    Float[] intention;
    ResultParser(String s){
        this.s = s;
    }
    boolean analysis(){
        try {
            Log.d("analysis", s);
            JSONObject jsonObject = new JSONObject(s);
            host_id = jsonObject.getString("id");
            status = jsonObject.getInt("status");
            JSONArray hypotheses = jsonObject.getJSONArray("hypotheses");
            JSONObject hypotheses0 = hypotheses.getJSONObject(0);
            utterance = hypotheses0.getString("utterance");
            String nlp_result = hypotheses0.getString("nlp_result");
            String[] nlp_result_list = nlp_result.split(" ");
            id = Integer.parseInt(nlp_result_list[0]);
            ArrayList<Float> intention_list = new ArrayList<Float>();
            for (int i = 1; i < nlp_result_list.length; ++i){
                intention_list.add(Float.parseFloat((nlp_result_list[i])));
            }
            intention = (Float[])intention_list.toArray(new Float[intention_list.size()]);

            if (utterance.length() <= 0 || status != 0 || id <= 0){
                return false;
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public String[] Intention_List= {
        "ALARM_CHANGE",
        "ALARM_CHECK",
        "ALARM_DELETE",
        "ALARM_OFF",
        "ALARM_ON",
        "ALARM_SET",
        "CALENDAR_CHECK_DATE",
        "CALENDAR_CHECK_DOW",
        "CALENDAR_CHECK_YEAR",
        "CALENDAR_VIEW",
        "CLOCK_CHECK_TIME",
        "CLOCK_CHECK_TIME_DIFF",
        "MAIL_CHECK",
        "MAIL_NOTIFY",
        "MAIL_REPLY",
        "MAIL_SEND",
        "MUSIC_CHECK_CURRENT",
        "MUSIC_CHECK_PLAYLIST",
        "MUSIC_PLAY",
        "MUSIC_SEARCH",
        "MUSIC_STOP",
        "NEWS_SEARCH",
        "PHONE_CALL",
        "PHONE_CHECK_MISSEDCALL",
        "PHONE_REDIAL",
        "PHONE_RETURN_CALL",
        "SCHEDULE_ADD",
        "SCHEDULE_CHANGE",
        "SCHEDULE_CHECK",
        "SCHEDULE_CHECK_FREETIME",
        "SCHEDULE_DELETE",
        "SCHEDULE_KEEP",
        "SCHEDULE_REMIND",
        "SMS_CHECK",
        "SMS_REPLY",
        "SMS_SEND",
        "TIMER_CANCEL",
        "TIMER_CHANGE",
        "TIMER_CHECK",
        "TIMER_PAUSE",
        "TIMER_RESUME",
        "TIMER_START",
        "WEATHER_CHECK",
        "WEATHER_NOTIFY",
    };

}
