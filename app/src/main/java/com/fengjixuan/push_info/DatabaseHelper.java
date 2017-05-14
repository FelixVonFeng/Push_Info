package com.fengjixuan.push_info;

import android.content.Context ;
import android.database.sqlite.SQLiteDatabase ;
import android.database.sqlite.SQLiteDatabase.CursorFactory ;
import android.database.sqlite.SQLiteOpenHelper ;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context , String name ,CursorFactory factory , int version) {
        super(context , name , factory , version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("Create a sqlist database");
        db.execSQL("create table manageinfo(valueStr varchar(200))") ;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db , int oldVersion , int newVersion) {

    }

}
