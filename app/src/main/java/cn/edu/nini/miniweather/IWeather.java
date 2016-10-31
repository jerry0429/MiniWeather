package cn.edu.nini.miniweather;

import cn.edu.nini.domain.TodayWeather;

/**
 * Created by nini on 2016/10/29.
 */
public interface IWeather {
    //这里可以定义一些本地程序与服务之间通讯的方法,即调用服务中的方法。
     void callQueryWeather(String cityCode, int arg);

     TodayWeather callParseXML(String xmldata);
}
