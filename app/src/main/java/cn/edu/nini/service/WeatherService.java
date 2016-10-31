package cn.edu.nini.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import cn.edu.nini.domain.IWeatherService;
import cn.edu.nini.domain.TodayWeather;

/**
 * Created by nini on 2016/10/26.
 */
//第六步编写解析函数。
public class WeatherService extends Service {
    static final int UPDATE_INTERVAL = 20 * 60 * 1000;//更新时间间隔
    private Timer timer = new Timer();//计时器
    private String TAG = "MyService";
    public static final String BROADCASTACTION = "cn.edu.nini.updataWeather";
    private IBinder myBinder = new MyBinder();
    private static TodayWeather todayWeather;

    private class MyBinder extends IWeatherService.Stub {

        //这是个例子，不想让外界调用的方法
        public void call洗桑拿(){
            洗桑拿();
        }

        @Override
        public void callQueryWeather(final String cityCode, final int arg) throws RemoteException {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "service->2222()");

                    TodayWeather todayWeather = queryWeatherCode(cityCode, arg);
                    Intent i = new Intent();
                    i.setAction(BROADCASTACTION);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("todayweather", todayWeather);
                    i.putExtras(bundle);
                    sendBroadcast(i);
                }
            }).start();
        }

        @Override
        public TodayWeather callParseXML(String xmldata) throws RemoteException {
            try {
                return parseXML(xmldata);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public String getMyData() throws RemoteException {
            return "这是我自己写的数据";
        }

    }


    /**
     * 这是必须要重写的方法
     *
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "service->onbind()");
        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("服务已启动");

    }

    //service被启动时回调的方法
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.d(TAG, "service->onStart()");
        //flag等于1代表长期运行服务，2代表单次获取调用服务
        int flag = intent.getIntExtra("flag",1);
        if (flag == 1) {
            //每过一段时间执行一次更新天气的操作
            timer.scheduleAtFixedRate(new TimerTask() {
                                          @Override
                                          public void run() {
                                              SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
                                              String current_city = sp.getString("cityname", "嘉兴");
                                              String current_cityCode = sp.getString("cityID", "101010100");
                                              TodayWeather todayWeather = queryWeatherCode(current_cityCode, 1);
                                              Intent i = new Intent();
                                              i.setAction(BROADCASTACTION);
                                              Bundle bundle = new Bundle();
                                              bundle.putSerializable("todayweather", todayWeather);
                                              i.putExtras(bundle);
                                              sendBroadcast(i);
                                          }
                                      }
                    , 2000//第二个参数  是指延时多少时间后执行   initial delay
                    , UPDATE_INTERVAL);//第三个参数是，每过多少时间执行    subsequent rate
        }else{//执行单次更新操作
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "service->11111()");
                    SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
                    String current_city = sp.getString("cityname", "嘉兴");
                    String current_cityCode = sp.getString("cityID", "101010100");
                    TodayWeather todayWeather = queryWeatherCode(current_cityCode, 1);
                    Intent i = new Intent();
                    i.setAction(BROADCASTACTION);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("todayweather", todayWeather);
                    i.putExtras(bundle);
                    sendBroadcast(i);
                }
            }).start();

        }



        return super.onStartCommand(intent, flags, startId);
    }

    //  service被关闭之前的方法
    @Override
    public void onDestroy() {
        System.out.println("服务已销毁");
        super.onDestroy();
    }

    /**
     * 这是xml解析方法
     *
     * @param xmldata 传递进来要解析的字符串
     * @return 解析后返回的TodayWeather对象
     * @throws Exception
     */
    public static TodayWeather parseXML(String xmldata) throws Exception {
        TodayWeather todayWeather = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;

        try {
            XmlPullParser parser = Xml.newPullParser();
//            parser.setInput(stream, "UTF-8");   这里用流读取不行，为什么？
            parser.setInput(new StringReader(xmldata));//问题出在这里
            int event = parser.getEventType();

            //Log.d("myWeather", "parseXML");

            while (event != parser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if ("resp".equals(parser.getName())) {
                            todayWeather = new TodayWeather();
                        }
                        if (todayWeather != null) {
                            if (("city").equals(parser.getName())) {
                                todayWeather.setCity(parser.nextText());
                            }
                            if (("updatetime").equals(parser.getName())) {
                                todayWeather.setUpdatetime(parser.nextText());
                            }
                            if (("shidu").equals(parser.getName())) {
                                todayWeather.setShidu(parser.nextText());
                            }
                            if (("wendu").equals(parser.getName())) {
                                todayWeather.setWendu(parser.nextText());
                            }
                            if (("fengli").equals(parser.getName())) {
                                todayWeather.setFengli(parser.nextText());
                                fengliCount++;
                            }
                            if (("fengxiang").equals(parser.getName())) {
                                todayWeather.setFengxiang(parser.nextText());
                                fengxiangCount++;
                            }
                            if (("pm25").equals(parser.getName())) {
                                todayWeather.setPm25(parser.nextText());
                            }
                            if (("quality").equals(parser.getName())) {
                                todayWeather.setQuality(parser.nextText());
                            }
                            if (parser.getName().equals("date") && dateCount == 0) {
                                todayWeather.setDate(parser.nextText());
                                dateCount++;
                            }
                            if (parser.getName().equals("high") && highCount == 0) {
                                todayWeather.setHigh(parser.nextText());
                                highCount++;
                            }
                            if (parser.getName().equals("low") && lowCount == 0) {
                                todayWeather.setLow(parser.nextText());
                                lowCount++;
                            }
                            if (parser.getName().equals("type") && typeCount == 0) {
                                todayWeather.setType(parser.nextText());
                                typeCount++;
                            }
                            break;
                        }
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return todayWeather;
    }

    private static TodayWeather queryWeatherCode(String cityCode, int arg) {
        final int flag = arg;
        final String address1 = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        final String address2 = "http://wthrcdn.etouch.cn/WeatherApi?city=" + cityCode;

        //Log.d("myService", address1);
        //Log.d("myService", address2);


        todayWeather = new TodayWeather();
        HttpURLConnection con = null;
        //判断当前是请求城市名字还是城市id
        String finalAddress = "";
        if (flag == 1) {
            finalAddress = address1;
        } else if (flag == 2) {
            finalAddress = address2;
        }
        try {
            URL url = new URL(finalAddress);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            InputStream in = con.getInputStream();
            //读取输入流
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder response = new StringBuilder();
            String str;

            while ((str = reader.readLine()) != null) {
                response.append(str);
                //Log.d("myWeather222", str);
            }
            String responseStr = response.toString();
            //Log.d("myWeather", responseStr);

            //加载今日天气
            if (con.getResponseCode() == 200) {
                //这里获得天气信息
                todayWeather = parseXML(responseStr);
                if (todayWeather != null) {
                    //Log.d("myWeather1111", todayWeather.toString());
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.d("myWeather", e.toString());
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return todayWeather;
    }
    //这是个例子，不想让外界调用的方法
    private void 洗桑拿(){

    }

}
