package cn.edu.nini.miniweather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.edu.nini.app.MyApplication;
import cn.edu.nini.domain.City;

/**
 * Created by nini on 2016/10/26.
 */
public class SelectCityActivity extends Activity implements View.OnClickListener {
    private Context context = this;
    private ImageView mBackBtn;
    private ListView lv;
    private MyApplication mApplication;
    private List<City> cityList;
    private EditText mEditText;
    private MyLetterIndexAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        initView();
        mBackBtn.setOnClickListener(this);

        setListViewAdapter();

        setListViewListener();

        //注册函数响应ontextchange事件
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cityList=((MyApplication)MyApplication.getInstance()).getmCityDB().getSelectCity(s);
                //排序
                sortData();
                //把List<City>转换成List<String>
                final List<String> mcityList =   new ArrayList<>();
                for (City city : cityList) {
                    mcityList.add(city.get_id()+"-"+city.getProvince()+"-"+city.getCity());
                }
                adapter=new MyLetterIndexAdapter(context, cityList);
                //设置适配器
                lv.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });


        //以下是侧边栏的实现。
        TextView textView = (TextView) findViewById(R.id.show_letter_in_center);
        final LetterIndexView letterIndexView = (LetterIndexView) findViewById(R.id.letter_index_view);
        letterIndexView.setTextViewDialog(textView);
        letterIndexView.setUpdateListView(new LetterIndexView.UpdateListView() {
            @Override
            public void updateListView(String currentChar) {
                int positionForSection = adapter.getPositionForSection(currentChar.charAt(0));
                lv.setSelection(positionForSection);
            }
        });
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
            //当滑动listview的时候  让侧边栏也跟着动
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int sectionForPosition = adapter.getSectionForPosition(firstVisibleItem);
                letterIndexView.updateLetterIndexView(sectionForPosition);
            }
        });

    }

    public void setListViewListener() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //设置listview的item的点击事件
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(context,"你点击的是"+view.getId()+"----"+position,Toast.LENGTH_SHORT).show();
                Intent intent=new Intent();
                intent.setClass(context,MainActivity.class);
                intent.putExtra("cityname",cityList.get(position).getCity());
                intent.putExtra("cityID",cityList.get(position).getNumber());
                //startActivity(intent);
                setResult(1,intent);

                finish();
            }

        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(context,"你长按的是"+view.getId()+"----"+position,Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    public void setListViewAdapter() {
        mApplication = (MyApplication) getApplication();
        //从程序唯一的application中取到城市列表
        cityList = (List<City>) mApplication.mCityList;
        sortData();
        adapter=new MyLetterIndexAdapter(context, cityList);
        //设置适配器
        lv.setAdapter(adapter);
    }


    public void initView(){
        mBackBtn = (ImageView) findViewById(R.id.title_back);
        lv = (ListView) findViewById(R.id.lv);
        mEditText=(EditText)findViewById(R.id.search_edit);
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.title_back) {
            finish();
        }
    }

    //排序
    private void sortData() {
        Collections.sort(cityList, new Comparator<City>() {
            @Override
            public int compare(City lhs, City rhs) {
                if (lhs.getFirstPY().contains("#")) {
                    return 1;
                } else if (rhs.getFirstPY().contains("#")) {
                    return -1;
                }else{
                    return lhs.getFirstPY().compareTo(rhs.getFirstPY());
                }
            }
        });
    }
}
