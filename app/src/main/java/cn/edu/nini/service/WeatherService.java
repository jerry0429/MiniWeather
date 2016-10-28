package cn.edu.nini.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.Timer;

import cn.edu.nini.domain.TodayWeather;

/**
 * Created by nini on 2016/10/26.
 */
//第六步编写解析函数。
public class WeatherService extends Service {
    static final int UPDATE_INTERVAL = 2 * 60 * 60 * 1000;//更新时间间隔
    private Timer timer = new Timer();//计时器
    private String TAG = "MyService";

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
        return null;
    }

    //service被启动时回调的方法
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "service->onStart()");

        return super.onStartCommand(intent, flags, startId);
    }

    //  service被关闭之前的方法
    @Override
    public void onDestroy() {
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
//            parser.setInput(stream, "UTF-8");
            parser.setInput(new StringReader(xmldata));//问题出在这里
            int event = parser.getEventType();

            Log.d("myWeather", "parseXML");

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


}
