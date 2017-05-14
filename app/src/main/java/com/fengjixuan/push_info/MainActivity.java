package com.fengjixuan.push_info;

import java.util.ArrayList ;
import java.util.List ;
import java.util.Map ;
import android.content.ContentValues ;
import android.content.Intent ;
import android.database.Cursor ;
import android.database.sqlite.SQLiteDatabase ;
import android.net.Uri ;
import android.support.v7.app.AppCompatActivity;
import android.view.View ;
import android.view.View.OnClickListener ;
import android.os.Bundle;
import android.widget.AdapterView ;
import android.widget.AdapterView.OnItemClickListener ;
import android.widget.Button ;
import android.widget.ListView ;
import android.widget.Toast ;
import cn.jpush.android.api.JPushInterface ;

public class MainActivity extends AppCompatActivity implements OnClickListener , OnItemClickListener {

    private Button btnSelectAll = null ;
    private Button btnDelete = null ;
    private ListView lvListView = null ;
    private MyAdapter adpAdapter = null ;
    private boolean editFlag = false ;
    private String string = "" ;
    private String[] infoStrings = new String[200] ;
    private int infoCount = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this) ;

        btnDelete = (Button) findViewById(R.id.btnDelete) ;
        btnDelete.setOnClickListener(this);

        btnSelectAll = (Button) findViewById(R.id.btnSelectAll) ;
        btnSelectAll.setOnClickListener(this);

        lvListView = (ListView) findViewById(R.id.lvListView) ;
        lvListView.setOnClickListener(this);

        readInfofromDb() ;
        initData() ;
    }

    private void initData() {
        List<DemoBean> demoDatas = new ArrayList<DemoBean>() ;
        for(int i = 0 ; i < infoCount ; i++) demoDatas.add(new DemoBean(infoStrings[i] , true)) ;
        adpAdapter = new MyAdapter(this , demoDatas) ;
        lvListView.setAdapter(adpAdapter);
    }

    @Override
    public void onClick(View v) {
        if(v == btnDelete) {
            Map<Integer , Boolean> map = adpAdapter.getCheckMap() ;

            int count = adpAdapter.getCount() ;
            int i = 0 ;
            for(; i < count ; i++) {
                if(map.get(i) != null && map.get(i)) {
                    DemoBean bean = (DemoBean) adpAdapter.getItem(i) ;
                    if(bean.isCanremove()) infoStrings[i] = null ;
                }
            }

            for(i = 0 ; i < count ; i++) {
                int position = i - (count - adpAdapter.getCount()) ;
                if(map.get(i) != null && map.get(i)) {
                    DemoBean bean = (DemoBean) adpAdapter.getItem(position) ;
                    if(bean.isCanremove()) {
                        adpAdapter.getCheckMap().remove(i) ;
                        adpAdapter.remove(position);
                    } else {
                        map.put(position , false) ;
                    }
                }
            }

            adpAdapter.notifyDataSetChanged();
            saveDataToDB() ;
        }

        if(v == btnSelectAll) {
            if(btnSelectAll.getText().toString().trim().equals("Select All")) {
                adpAdapter.configCheckMap(true);
                adpAdapter.notifyDataSetChanged();
                btnSelectAll.setText("Unselect All");
            } else {
                adpAdapter.configCheckMap(false);
                adpAdapter.notifyDataSetChanged();
                btnSelectAll.setText("Select All");
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> listView , View itemLayout , int position , long id) {
        if(itemLayout.getTag() instanceof MyAdapter.ViewHolder) {
            MyAdapter.ViewHolder holder = (MyAdapter.ViewHolder) itemLayout.getTag() ;
            Intent intent2 = null ;
            if(infoStrings[position] != null) {
                String URIString = findURI(infoStrings[position]) ;
                if(URIString != null) {
                    System.out.println("URIString:" + URIString) ;
                    Uri uri = Uri.parse(URIString) ;
                    intent2 = new Intent(Intent.ACTION_VIEW , uri) ;
                    this.startActivity(intent2);
                }
            }
            if(editFlag) holder.cbCheck.toggle();
        }
    }

    private boolean readInfofromDb() {
        try {
            DatabaseHelper database_helper = new DatabaseHelper(MainActivity.this , "pushinfo.db" , null , 1) ;
            SQLiteDatabase db = database_helper.getWritableDatabase() ;

            Cursor cursor = db.query("manageinfo" , null , null , null , null , null , null) ;

            while(cursor.moveToNext()) {
                infoStrings[infoCount++] = cursor.getString(cursor.getColumnIndex("valueStr")) ;
            }
            return true ;
        } catch (Exception e) {
            System.out.println(e.toString()) ;
            return false ;
        }
    }

    private void saveDataToDB() {
        DatabaseHelper database_helper = new DatabaseHelper(this , "pushinfo.db" , null , 1) ;
        SQLiteDatabase db = database_helper.getWritableDatabase() ;

        try {
            db.delete("manageinfo" , null , null) ;
        } catch (Exception e) {
            Toast.makeText(this , "delete table of info failed" , Toast.LENGTH_SHORT).show() ;
        }

        String[] temp = new String[200] ;
        int j = 0 ;
        try {
            for(int i = 0 ; i < 2000 ; i++)
                if(infoStrings[i] != null) {
                    temp[j++] = infoStrings[i] ;
                    saveInfoToDatabase(infoStrings[i]) ;
                }
        } catch (Exception e) {

        }

        infoStrings = temp ;
    }

    private void saveInfoToDatabase(String title) {
        ContentValues values = new ContentValues() ;
        values.put("valueStr" , title) ;
        DatabaseHelper database_helper = new DatabaseHelper(this , "pushinfo.db" , null , 1) ;
        SQLiteDatabase db = database_helper.getWritableDatabase() ;

        try {
            db.insert("manageinfo" , null , values) ;
        } catch (Exception e) {

        }
    }

    String findURI(String URI) {
        int index = URI.indexOf("http:") ;
        if(index > 1) {
            String tempString = URI.substring(index , URI.length()) ;
            System.out.println(tempString) ;
            return tempString ;
        } else return null ;
    }

}
