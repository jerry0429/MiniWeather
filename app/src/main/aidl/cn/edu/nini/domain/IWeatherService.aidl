// IWeatherService.aidl
package cn.edu.nini.domain;
import cn.edu.nini.domain.TodayWeather;
// Declare any non-default types here with import statements

interface IWeatherService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void callQueryWeather(in String cityCode,in  int arg);

    TodayWeather callParseXML(in String xmldata);

    String getMyData();

}




