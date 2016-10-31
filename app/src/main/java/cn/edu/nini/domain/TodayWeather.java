package cn.edu.nini.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by nini on 2016/10/25.
 */
public class TodayWeather implements Parcelable, Serializable {
    private String city;
    private String updatetime;
    private String wendu;
    private String shidu;
    private String pm25;
    private String quality;
    private String fengxiang;
    private String fengli;
    private String date;
    private String high;
    private String low;
    private String type;

    @Override
    public String toString() {
        return "TodayWeather{" +
                "city='" + city + '\'' +
                ", updatetime='" + updatetime + '\'' +
                ", wendu='" + wendu + '\'' +
                ", shidu='" + shidu + '\'' +
                ", pm25='" + pm25 + '\'' +
                ", quality='" + quality + '\'' +
                ", fengxiang='" + fengxiang + '\'' +
                ", fengli='" + fengli + '\'' +
                ", date='" + date + '\'' +
                ", high='" + high + '\'' +
                ", low='" + low + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getWendu() {
        return wendu;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    public String getShidu() {
        return shidu;
    }

    public void setShidu(String shidu) {
        this.shidu = shidu;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getFengxiang() {
        return fengxiang;
    }

    public void setFengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
    }

    public String getFengli() {
        return fengli;
    }

    public void setFengli(String fengli) {
        this.fengli = fengli;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public TodayWeather(String city, String updatetime, String wendu, String shidu, String pm25, String quality, String fengxiang, String fengli, String date, String high, String low, String type) {
        this.city = city;
        this.updatetime = updatetime;
        this.wendu = wendu;
        this.shidu = shidu;
        this.pm25 = pm25;
        this.quality = quality;
        this.fengxiang = fengxiang;
        this.fengli = fengli;
        this.date = date;
        this.high = high;
        this.low = low;
        this.type = type;
    }

    public TodayWeather() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.city);
        dest.writeString(this.updatetime);
        dest.writeString(this.wendu);
        dest.writeString(this.shidu);
        dest.writeString(this.pm25);
        dest.writeString(this.quality);
        dest.writeString(this.fengxiang);
        dest.writeString(this.fengli);
        dest.writeString(this.date);
        dest.writeString(this.high);
        dest.writeString(this.low);
        dest.writeString(this.type);
    }

    public static final Parcelable.Creator<TodayWeather> CREATOR = new Parcelable.Creator<TodayWeather>() {

        @Override
        public TodayWeather createFromParcel(Parcel source) {
            return new TodayWeather(source.readString(), source.readString(), source.readString(),
                    source.readString(), source.readString(), source.readString(),
                    source.readString(), source.readString(), source.readString(),
                    source.readString(), source.readString(), source.readString());
        }

        @Override
        public TodayWeather[] newArray(int size) {
            return new TodayWeather[0];
        }
    };
}
