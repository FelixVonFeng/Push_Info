package com.fengjixuan.push_info;

/**
 * Created by felix on 17-5-13.
 */

import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;
import android.content.Context ;
import android.support.v4.widget.ViewDragHelper;
import android.view.LayoutInflater ;
import android.view.View ;
import android.view.ViewGroup ;
import android.widget.BaseAdapter ;
import android.widget.CheckBox ;
import android.widget.CompoundButton ;
import android.widget.CompoundButton.OnCheckedChangeListener ;
import android.widget.TextView ;

public class MyAdapter extends BaseAdapter {

    private Context context = null ;
    private List<DemoBean> datas = null ;
    private Map<Integer , Boolean> isCheckMap = new HashMap<Integer, Boolean>() ;

    public MyAdapter(Context context , List<DemoBean> datas) {
        this.datas = datas ;
        this.context = context ;
        configCheckMap(false) ;
    }

    public void configCheckMap(boolean bool) {
        for(int i = 0 ; i < datas.size() ; i++) isCheckMap.put(i , bool) ;
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size() ;
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position) ;
    }

    @Override
    public long getItemId(int position) {
        return 0 ;
    }

    @Override
    public View getView(final int position , View convertView , ViewGroup parent) {
        ViewGroup layout = null ;
        if(convertView == null) {
            layout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.listview_item_layout, parent , false) ;
        } else {
            layout = (ViewGroup) convertView ;
        }

        DemoBean bean = datas.get(position) ;
        boolean canRemove = bean.isCanremove() ;

        TextView tvTitle = (TextView) layout.findViewById(R.id.tvTitle) ;
        tvTitle.setText(bean.getTitle());

        CheckBox cbCheck = (CheckBox) layout.findViewById(R.id.cbCheckBox) ;
        cbCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCheckMap.put(position , isChecked) ;
            }
        });

        if(!canRemove) {
            cbCheck.setVisibility(View.GONE);
            cbCheck.setChecked(false) ;
        } else {
            cbCheck.setVisibility(View.VISIBLE);

            if(isCheckMap.get(position) == null) {
                isCheckMap.put(position , false) ;
            }

            cbCheck.setChecked(isCheckMap.get(position));
            ViewHolder holder = new ViewHolder() ;

            holder.cbCheck = cbCheck ;
            holder.tvTitle = tvTitle ;

            layout.setTag(holder);
        }
        return layout ;
    }

    public void add(DemoBean bean) {
        this.datas.add(0 , bean) ;
        configCheckMap(false);
    }

    public void remove(int position) {
        this.datas.remove(position) ;
    }

    public Map<Integer , Boolean> getCheckMap() {
        return this.isCheckMap ;
    }

    public static  class ViewHolder {
        public TextView tvTitle = null ;
        public CheckBox cbCheck = null ;
        public Object data = null ;
    }

    public List<DemoBean> getDatas() {
        return datas ;
    }

}
