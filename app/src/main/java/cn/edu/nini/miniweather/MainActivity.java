package cn.edu.nini.miniweather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.edu.nini.Utils.NetUtils;
import cn.edu.nini.domain.TodayWeather;
import cn.edu.nini.service.PreferenceService;
import cn.edu.nini.service.WeatherService;

/**
 * Created by nini on 2016/10/25.
 */
public class MainActivity extends Activity implements View.OnClickListener {
    private static final int UPDATE_TODAY_WEATHER = 1;
    private Context context = this;
    private ImageView mUpdataBtn;
    private ImageView mCitySelect;
    private ImageView mSetupImg;
    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv, temperatureTv, climateTv, windTv, city_name_Tv;
    private ImageView weatherImg, pmImg;

    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MyAPP", "MainAcitivity->OnCreate");
        mUpdataBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdataBtn.setOnClickListener(this);
        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);
        mSetupImg=(ImageView)findViewById(R.id.title_setup);
        mSetupImg.setOnClickListener(this);
        if (NetUtils.isConnected(context)) {
            Log.d("myWeather", "网络ok");
            Toast.makeText(context, "网络OK！", Toast.LENGTH_SHORT).show();
        } else {
            int d = Log.d("myWeather", "网络挂了");
            Toast.makeText(context, "网络挂了", Toast.LENGTH_SHORT).show();
        }

        initView();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    private void updateTodayWeather(TodayWeather todayWeather) {
        city_name_Tv.setText(todayWeather.getCity() + "天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime() + "发布");
        humidityTv.setText("湿度：" + todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh() + "~" + todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:" + todayWeather.getFengli());
        updateWeatherImg(todayWeather.getType());
        Toast.makeText(MainActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
    }

    //更新天气图标
    private void updateWeatherImg(String weather) {
        switch (weather) {
            case "暴雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                break;
            case "暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "大雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case "大雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
                break;
            case "多云":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "雷阵雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨冰雹":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "晴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            case "沙尘暴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
            case "特大暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "雾":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            case "小雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "阴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "雨夹雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "阵雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "阵雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
            case "中雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "中雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                break;
            default:
                break;
        }
    }

    private void initView() {
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);


        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");

    }

    @Override
    public void onClick(View v) {
        //通过SharedPreferences读取城市id，如果没有定义则缺省为101010100（北京城市 ID）
        if (v.getId() == R.id.title_update_btn) {
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("cityID", "101010100");
            String cityname = sharedPreferences.getString("cityname", "北京");
            Log.d("myWeather", cityCode);


            if (NetUtils.isConnected(context)) {
                Log.d("myWeather", "网络ok");
                //可以查询城市id
                queryWeatherCode(cityCode, 1);
                //也可以查询城市名字
//                queryWeatherCode(cityname,2);
                Toast.makeText(context, "网络OK！", Toast.LENGTH_SHORT).show();
            } else {
                int d = Log.d("myWeather", "网络挂了");
                Toast.makeText(context, "网络挂了", Toast.LENGTH_SHORT).show();
            }
        }
        //城市管理Activity
        if (v.getId() == R.id.title_city_manager) {
            /*
             * 从（天气情况显示界面）到城市列表界面是通过Intent来启动一个新的Activity的，
             * 因此需要用到startActivityForResult(Intent	intent,	int	requestCode)
             */
            Intent i = new Intent();
            i.setClass(context, SelectCityActivity.class);
            startActivityForResult(i, 200);
        }
        //程序设置界面Acitivity
        if(v.getId()==R.id.title_setup){
            Intent i=new Intent(context,MyPreferenceActivity.class);
            startActivity(i);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    private void queryWeatherCode(String cityCode, int arg) {
//        final String address = "http://192.168.31.33:8080/webImage_android/XMLServlet";
        final int flag = arg;
        final String address1 = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        final String address2 = "http://wthrcdn.etouch.cn/WeatherApi?city=" + cityCode;

        Log.d("myWeather", address1);
        Log.d("myWeather", address2);

        //开启一个新线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                TodayWeather todayWeather = new TodayWeather();
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
                        Log.d("myWeather222", str);
                    }
                    String responseStr = response.toString();
                    Log.d("myWeather", responseStr);

                    //加载今日天气
                    if (con.getResponseCode() == 200) {
                        todayWeather = WeatherService.parseXML(responseStr);
                        if (todayWeather != null) {
                            Log.d("myWeather1111", todayWeather.toString());
                        }
                    }
                    //方法一：使用handler发送消息
                    Message message = new Message();
                    message.obj = todayWeather;
                    message.what = UPDATE_TODAY_WEATHER;
                    handler.sendMessage(message);

                    //方法二：runOnUiThread在子线程调用ui更新方法
                    final TodayWeather finalTodayWeather = todayWeather;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //updateTodayWeather(finalTodayWeather);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("myWeather", e.toString());
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }
            }
        }).start();
    }

    /**
     * 处理选择城市activity穿过来的数据onActivityResult(int	requestCode,	int	resultCode,	Intent	intent）
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200 && resultCode == 1) {
            //获取到城市名字后
            //查询这个城市的天气
            String cityname = data.getStringExtra("cityname");
            String cityID = data.getStringExtra("cityID");
            queryWeatherCode(cityname, 2);//2代表根据名字选择


            //使用sharedPreference保存常用地点。   这里先默认用户选择的保存
            PreferenceService service = new PreferenceService(context);
            service.save(cityname, cityID);
        }
    }
}
