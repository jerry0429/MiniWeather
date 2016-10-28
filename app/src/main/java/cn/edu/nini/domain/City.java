package cn.edu.nini.domain;

/**
 * Created by nini on 2016/10/26.
 */
public class City {
    private int _id;

    public City(int _id, String province, String city, String number, String firstPY, String allPY, String allFirstPY) {
        this._id = _id;
        this.province = province;
        this.city = city;
        this.number = number;
        this.firstPY = firstPY;
        this.allPY = allPY;
        this.allFirstPY = allFirstPY;
    }

    @Override
    public String toString() {
        return "City{" +
                "_id=" + _id +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", number='" + number + '\'' +
                ", firstPY='" + firstPY + '\'' +
                ", allPY='" + allPY + '\'' +
                ", allFirstPY='" + allFirstPY + '\'' +
                '}';
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    private String province;
    private String city;
    private String number;
    private String firstPY;
    private String allPY;
    private String allFirstPY;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFirstPY() {
        return firstPY;
    }

    public void setFirstPY(String firstPY) {
        this.firstPY = firstPY;
    }

    public String getAllPY() {
        return allPY;
    }

    public void setAllPY(String allPY) {
        this.allPY = allPY;
    }

    public String getAllFirstPY() {
        return allFirstPY;
    }

    public void setAllFirstPY(String allFirstPY) {
        this.allFirstPY = allFirstPY;
    }

    public City() {
    }

}
