package cn.edu.nini.miniweather;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.List;

import cn.edu.nini.domain.City;

/**
 * Created by nini on 2016/10/27.
 */
public class MyLetterIndexAdapter extends BaseAdapter implements SectionIndexer {
    private List<City> list;
    private Context context;
    private LayoutInflater inflater;

    public MyLetterIndexAdapter(Context context, List<City> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_item, null);
            holder = new ViewHolder();
            holder.showLetter = (TextView) convertView.findViewById(R.id.show_letter);
            holder.cityname = (TextView) convertView.findViewById(R.id.cityname);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        City city = list.get(position);
        //这里设置的是城市列表的文字部分
        holder.cityname.setText(city.get_id()+"-"+city.getProvince()+"-"+city.getCity());
        //获得当前position是属于哪个分组
        int sectionForPosition = getSectionForPosition(position);
        //获得该分组第一项的position
        int positionForSection = getPositionForSection(sectionForPosition);
        //查看当前position是不是当前item所在分组的第一个item
        //如果是，则显示showLetter，否则隐藏
        if (position == positionForSection) {
            holder.showLetter.setVisibility(View.VISIBLE);
            holder.showLetter.setText(city.getFirstPY());
        } else {
            holder.showLetter.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    //传入一个分组值[A....Z],获得该分组的第一项的position
    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getFirstPY().charAt(0) == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

    //传入一个position，获得该position所在的分组
    @Override
    public int getSectionForPosition(int position) {
        return list.get(position).getFirstPY().charAt(0);
    }

    class ViewHolder {
        TextView cityname, showLetter;
    }
}
