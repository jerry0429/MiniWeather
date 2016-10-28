package cn.edu.nini.service;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by nini on 2016/10/27.
 */
public class PreferenceService {

    private Context context;

    public PreferenceService(Context context){
        this.context=context;
    }


    public void save(String cityname, String cityID) {
        SharedPreferences preferences=context.getSharedPreferences("config",Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("cityname",cityname);
        edit.putString("cityID",cityID);
        //一定要提交
        edit.commit();

    }
}
