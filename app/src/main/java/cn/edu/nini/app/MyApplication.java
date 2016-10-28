package cn.edu.nini.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.edu.nini.db.CityDB;
import cn.edu.nini.domain.City;
import cn.edu.nini.miniweather.R;

/**
 * Created by nini on 2016/10/26.
 * 继承Application类，重写onCreate方法，在里面进行一些初始 化的操作，如打开数据库，启动后台的service
 */
public class MyApplication extends Application {

    private static final String TAG = "MyAPP";
    private static Application mApplication;
    public List<City> mCityList;
    CityDB mCityDB;
    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "MyApplication->OnCreate");
        mApplication = this;
        //打开数据库
        mCityDB=openCityDB();
        initCityList();
    }

    public static Application getInstance() {
        return mApplication;
    }

    public CityDB getmCityDB(){
        return mCityDB;
    }
    /**
     * 初始化数据库的方法
     */
    private CityDB openCityDB() {
        //构建在手机内部存储中的路径
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "databases"
                + File.separator;
        String name="city.db";
        File mulu=new File(path);
        if(!mulu.exists()){
            mulu.mkdirs();
        }

        File db=new File(path,name);

        Log.d(TAG,path+name);

        if(!db.exists()){
            Log.i(TAG,"db id not exists");
            try{
                //创建数据库文件别忘了
                db.createNewFile();
                //从资源文件中读取数据库
                InputStream inputStream = getResources().openRawResource(R.raw.city);
                Log.d(TAG,inputStream.toString());
                FileOutputStream fos=new FileOutputStream(db);
                int len=0;
                byte[] buffer=new byte[1024];
                while((len=inputStream.read(buffer))!=-1){
                    fos.write(buffer,0,len);
                    fos.flush();
                }
                //关闭
                fos.close();
                inputStream.close();
            }catch (Exception e){
                e.printStackTrace();
                System.exit(0);
            }
        }
        return new CityDB(this);
    }
    private boolean prepareCityList(){
        //获取到所有城市的集合
        mCityList=mCityDB.getAllCity();

        for (City city:mCityList){
            String cityname=city.getCity();
            //Log.d(TAG,cityname);
        }
        return true;
    }
    private void initCityList(){
        mCityList=new ArrayList<City>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareCityList();
            }
        }).start();
    }

}
