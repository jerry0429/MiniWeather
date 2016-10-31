package cn.edu.nini.miniweather;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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
import java.util.ArrayList;
import java.util.List;

import cn.edu.nini.Utils.NetUtils;
import cn.edu.nini.domain.IWeatherService;
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
    private ImageView mCitySelectBtn;
    private ImageView mSetupBtn, mShareBtn, mLocationBtn;
    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv, temperatureTv, climateTv, windTv, city_name_Tv;
    private ImageView weatherImg, pmImg;
    private ImageView[] dots;//导航小圆点
    private int[] ids = {R.id.iv1, R.id.iv2};//小圆点的iamgeview值
    private DrawerLayout mDrawerLayout;
    private ViewPagerAdapter vpAdapter;
    private ViewPager vp;
    private List<View> views = new ArrayList<>();
    private String Tag = "myWeather";
    private WeatherDataReceiver weatherDataReceiver;
    private IWeatherService iWeatherRemote;
    private IWeatherService iWeatherLocal;
    private RemoteWeatherServiceConnection connRemote = new RemoteWeatherServiceConnection();
    private LocalWeatherServiceConnection connLocal=new LocalWeatherServiceConnection();
    private SharedPreferences sp;
    private String current_city;
    private String current_cityCode;
    private  Intent intent_localService;
    private  Intent intent_remoteService;

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private class WeatherDataReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //使用序列化传递自定义对象
            TodayWeather todayWeather = (TodayWeather) intent.getSerializableExtra("todayweather");
            Message msg = handler.obtainMessage();
            msg.what = UPDATE_TODAY_WEATHER;
            msg.obj = todayWeather;
            //把收到的todayweather发送出去。
            handler.sendMessage(msg);
        }
    }

    @Override
    protected void onDestroy() {
        //unregisterReceiver(weatherDataReceiver);
        //unbindService(conn);
        stopService(intent_localService);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        current_city = sp.getString("cityname", "嘉兴");
        current_cityCode = sp.getString("cityID", "101010100");

        Log.d("MyAPP", "MainAcitivity->OnCreate");
        mUpdataBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdataBtn.setOnClickListener(this);
        mCitySelectBtn = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelectBtn.setOnClickListener(this);
        mSetupBtn = (ImageView) findViewById(R.id.title_setup);
        mSetupBtn.setOnClickListener(this);
        mLocationBtn.setOnClickListener(this);
        if (NetUtils.isConnected(context)) {
            Log.d("myWeather", "网络ok");
            Toast.makeText(context, "网络OK！", Toast.LENGTH_SHORT).show();
        } else {
            int d = Log.d("myWeather", "网络挂了");
            Toast.makeText(context, "网络挂了", Toast.LENGTH_SHORT).show();
        }


        //绑定服务
        intent_localService=new Intent();
        intent_localService.setClass(this, WeatherService.class);
        intent_localService.putExtra("flag",1);//1代表服务后台定期更新
        /*
        混合方式开启服务
        1、先调用startService
        2、再调用bindService，这时候可以获取到binder对象
        3、再unbindService，这时候binder仍然可以用
        4、最后再stopService
         */
        startService(intent_localService);
        bindService(intent_localService, connLocal, Context.BIND_AUTO_CREATE);//这里在运行程序的时候就混合开启服务。
        weatherDataReceiver = new WeatherDataReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WeatherService.BROADCASTACTION);
        registerReceiver(weatherDataReceiver, filter);


        /*左侧导航栏*/
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        //下方的一个Snackbar

        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //这里可以做些动作。
                            }
                        }).setActionTextColor(Color.BLUE).show();
            }
        });
    }


    private class RemoteWeatherServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //这里的目的得到中间代理人。
            iWeatherRemote = IWeatherService.Stub.asInterface(service);
            System.out.println("service connection");
            try {
                String myData = iWeatherRemote.getMyData();
                System.out.println(myData);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iWeatherRemote = null;
        }
    }

    private class LocalWeatherServiceConnection implements  ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //这里的目的得到中间代理人。
            iWeatherLocal = (IWeatherService) service;
            System.out.println("service connection");
            try {
                String myData = iWeatherLocal.getMyData();
                System.out.println(myData);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iWeatherLocal = null;
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
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mShareBtn = (ImageView) findViewById(R.id.title_share);
        mLocationBtn = (ImageView) findViewById(R.id.title_location);
        //以下是初始化viewPager
        LayoutInflater inflater = LayoutInflater.from(context);
        View one_page = inflater.inflate(R.layout.page1, null);
        View two_page = inflater.inflate(R.layout.page2, null);
        views.add(one_page);
        views.add(two_page);
        vpAdapter = new ViewPagerAdapter(views, context);
        vp = (ViewPager) findViewById(R.id.mViewpager);
        vp.setAdapter(vpAdapter);
        //增加页面变化的监听事件，动态修改导航小圆点的属性
        dots = new ImageView[views.size()];
        for (int i = 0; i < views.size(); i++) {
            dots[i] = (ImageView) findViewById(ids[i]);
        }

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

            if (NetUtils.isConnected(context)) {
                Log.d("myWeather", "网络ok");
                //执行异步操作
                aTask ak = new aTask();
                ak.execute();

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
        if (v.getId() == R.id.title_setup) {
            Intent i = new Intent(context, MyPreferenceActivity.class);
            startActivity(i);
        }
        if (v.getId() == R.id.title_location) {
            /*
            利用服务查询天气。主要是利用了广播的技术，在服务里调用网络访问等耗时的操作，然后通过广播的形式，把实体数据传递给主线程（一般用到序列化实体数据），
            主线程通过重写BroadcastReceiver的onReceive()方法，接收到实体数据后，交给ui线程的更新控件的方法。
             */
            //这里根据得到的binder对象调用了服务里的方法
            try {
                iWeatherRemote.callQueryWeather(current_cityCode,1);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }

    //可以在后台执行一些操作
    private class aTask extends AsyncTask {
        //后台线程执行时
        @Override
        protected Object doInBackground(Object... params) {
            // 耗时操作
            SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
            String current_cityCode = sp.getString("cityID", "101010100");
            TodayWeather todayWeather = queryWeatherCode(current_cityCode, 1);
//            TodayWeather todayWeather = iWeather.queryWeather(current_cityCode, 1);
            return todayWeather;
        }

        //后台线程执行结束后的操作，其中参数result为doInBackground返回的结果
        @Override
        protected void onPostExecute(Object result) {

            updateUI((TodayWeather) result);
        }
    }


    /**
     * 处理天气更新的handler
     */

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:     //todo
                    updateUI((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    private TodayWeather queryWeatherCode(String cityCode, int arg) {
        final int flag = arg;
        final String address1 = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        final String address2 = "http://wthrcdn.etouch.cn/WeatherApi?city=" + cityCode;

        Log.d("myWeather", address1);
        Log.d("myWeather", address2);

        //开启一个新线程  也可以用服务替代。

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
                todayWeather = iWeatherLocal.callParseXML(responseStr);

                if (todayWeather != null) {
                    Log.d("myWeather1111", todayWeather.toString());
                }
            }
            //方法一：使用handler发送消息
                    /*Message message = new Message();
                    message.obj = todayWeather;
                    message.what = UPDATE_TODAY_WEATHER;
                    handler.sendMessage(message);*/

            //方法二：runOnUiThread在子线程调用ui更新方法
                    /*final TodayWeather finalTodayWeather = todayWeather;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //updateUI(finalTodayWeather);
                        }
                    });*/
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("myWeather", e.toString());
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
            return  todayWeather;

    }

    /**
     * 从城市选择界面回来时。
     * 处理选择城市activity穿过来的数据onActivityResult(int	requestCode,	int	resultCode,	Intent	intent）
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200 && resultCode == 1) {
            //获取到城市名字后
            //查询这个城市的天气
            String cityname = data.getStringExtra("cityname");
            String cityID = data.getStringExtra("cityID");
            //2代表根据名字选择
            try {
                iWeatherRemote.callQueryWeather(cityname,2);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            //使用sharedPreference保存常用地点。   这里先默认用户选择的保存
            PreferenceService service = new PreferenceService(context);
            service.save(cityname, cityID);
        }
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

    //设置左侧边栏点击事件
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        Intent intent;
                        menuItem.setChecked(true);
                        switch (String.valueOf(menuItem.getTitle())) {
                            case "Home":
                                intent = new Intent(context, MainActivity.class);
                                startActivity(intent);
                                break;
                            case "City Location":
                                intent = new Intent();
                                intent.setClass(context, SelectCityActivity.class);
                                startActivityForResult(intent, 200);
                                break;
                            case "Friends":
                                intent_remoteService=new Intent();
                                intent_remoteService.setAction("cn.edu.nini.service");
                                intent_remoteService.addCategory("revicer");
                                intent_remoteService.setPackage("cn.edu.nini.remoteservice");

                                //intent1.setClass(MainActivity.this, WeatherService.class);
                                bindService(intent_remoteService, connRemote, Context.BIND_AUTO_CREATE);
                                break;
                            case "System settings ":
                                intent = new Intent(context, MyPreferenceActivity.class);
                                startActivity(intent);
                                break;
                            default:
                                break;
                        }
                        Log.d(Tag, String.valueOf(menuItem.getItemId()));
                        Log.d(Tag, String.valueOf(menuItem.getTitle()));
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    //更新控件
    private void updateUI(TodayWeather todayWeather) {
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
        //updateWeatherImg(todayWeather.getType());
        Toast.makeText(MainActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
    }

    /**
     * <菜单>生成菜单，只是在activity创建的时候执行一次
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }

    /**
     * <菜单>是每次点击menu键都会重新调用，所以，如果菜单需要更新的话，就用此方法
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (AppCompatDelegate.getDefaultNightMode()) {
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                menu.findItem(R.id.menu_night_mode_system).setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_AUTO:
                menu.findItem(R.id.menu_night_mode_auto).setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                menu.findItem(R.id.menu_night_mode_night).setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                menu.findItem(R.id.menu_night_mode_day).setChecked(true);
                break;
        }
        return true;
    }

    /**
     * <左滑出侧边栏>设置夜晚模式
     *
     * @param nightMode
     */
    private void setNightMode(@AppCompatDelegate.NightMode int nightMode) {
        AppCompatDelegate.setDefaultNightMode(nightMode);

        if (Build.VERSION.SDK_INT >= 11) {
            recreate();
        }
    }

    /**
     * <左滑出侧边栏>菜单选项选择事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.menu_night_mode_system:
                setNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case R.id.menu_night_mode_day:
                setNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case R.id.menu_night_mode_night:
                setNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case R.id.menu_night_mode_auto:
                setNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
