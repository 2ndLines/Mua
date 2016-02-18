package com.baoshi.mua.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ThinkPad on 2014/11/3.
 */
public class Lg{
    private static  final boolean debug = true;
    private static final String TAG = "Mua";

   public static void d(String message){
       d(TAG,message);
   }
   public static void d(String tag, String message){
        if(debug){
            Log.d(tag,message);
        }
    }

   public static void e(String message){
       e(TAG,message);
   }
   public static void e(String tag, String message){
        if(debug){
            Log.e(tag,message);
        }
    }

    public static void toast(Context context, int resId){
        String message = context.getResources().getString(resId);
        toast(context,message);
    }

    public static void toast(Context cxt, String message){

        Toast.makeText(cxt,message,Toast.LENGTH_LONG).show();

    }
}
