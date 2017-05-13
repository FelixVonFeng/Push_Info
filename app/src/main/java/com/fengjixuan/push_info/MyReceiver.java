package com.fengjixuan.push_info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ContentValues ;
import android.database.sqlite.SQLiteDatabase ;
import android.net.Uri ;
import android.os.Bundle;
import android.util.Log ;
import cn.jpush.android.api.JPushInterface;


public class MyReceiver extends BroadcastReceiver {

    private static final String TAG = "JPush";
    String title = null ;
    private Context context ;
    private boolean saveFlag = true ;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            this.context = context ;
            title = bundle.getString(JPushInterface.EXTRA_ALERT) ;

            Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                //send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                //processCustomMessage(context, bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 用户点击打开了通知");

                saveFlag = false ;
                Intent i = new Intent(context, MainActivity.class);
                //i.putExtras(bundle);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
                context.startActivity(i);

                String URIString = findURI(title) ;
                if(URIString != null) {
                    Uri uri = Uri.parse(URIString) ;
                    Intent intent2 = new Intent(Intent.ACTION_VIEW , uri) ;
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    context.startActivity(intent2) ;

                }

            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                Log.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
            } else {
                Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }

            if(title != null && title != "" && saveFlag)
                saveInfoToDatabase() ;

        } catch (Exception e){

        }

    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();

        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            }else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    private void saveInfoToDatabase() {
        ContentValues values = new ContentValues() ;
        values.put("valueStr" , title) ;
        DatabaseHelper database_helper = new DatabaseHelper(this.context , "pushinfo.db" , null , 1) ;
        SQLiteDatabase db = database_helper.getWritableDatabase() ;

        try {
            db.insert("manageinfo" , null , values) ;
        } catch (Exception e) {

        }

        db.close() ;
    }

    String findURI(String URI) {
        int index = URI.indexOf("http:") ;
        if(index >= 0) {
            String tempString = URI.substring(index , URI.length()) ;
            System.out.println(tempString) ;
            return tempString ;
        }
        else
            return null ;
    }

}