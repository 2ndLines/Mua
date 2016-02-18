package com.baoshi.mua.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.baoshi.mua.R;
import com.baoshi.mua.utils.Lg;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by ThinkPad on 2014/11/28.
 */
public class PubTypeHelper {
    private static final int MAX_COUNT_TYPE = 8;
    private static final int ERROR_CODE_OUT_OF_RANGE = -1;
    private static final int ERROR_CODE_FAIL_TO_SAVE = -2;

    private static final String file_name = "pub_type";
    private static PubTypeHelper instance = null;

    public static class SingletonHolder{

        public static final PubTypeHelper instance = new PubTypeHelper();
    }

    public static PubTypeHelper getInstance(){
        return SingletonHolder.instance;
    }

    @Deprecated
    public static PubTypeHelper getInstance(Context context){
        if(instance == null){
            synchronized (PubTypeHelper.class){
                PubTypeHelper tmp = instance;
                if(tmp == null){
                    tmp = new PubTypeHelper(context);
                    instance = tmp;
                }
            }
        }
        return instance;
    }


    private PubTypeHelper(){}

    private PubTypeHelper(Context appContext) {
        init(appContext);
    }

    public void init(Context context){
        final String KEY = "init";
        SharedPreferences sharedPreferences = context.getSharedPreferences(file_name, Context.MODE_PRIVATE);
        boolean init = sharedPreferences.getBoolean(KEY, false);
        if(!init){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String[] def = context.getResources().getStringArray(R.array.pub_type_array);
            for(String type : def){
                editor.putInt(type,0);
            }
            editor.putBoolean(KEY, true);
            editor.commit();
        }
    }

    public int addType(Context context,String type) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(file_name, Context.MODE_PRIVATE);

        if(sharedPreferences.getAll().size() > MAX_COUNT_TYPE){
            return ERROR_CODE_OUT_OF_RANGE;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(type, 0);
        if(editor.commit()){
            return 0;
        }else{
            return ERROR_CODE_FAIL_TO_SAVE;
        }
    }

    /**
     * Increase used time.
     *
     * @param key
     */
    public void increaseTime(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(file_name, Context.MODE_PRIVATE);

        int time = sharedPreferences.getInt(key, 0);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, time + 1);
        editor.commit();
    }

    public void removeType(Context context,String type) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(file_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(type);
        editor.commit();
    }

    public List<String> getAllTypes(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(file_name, Context.MODE_PRIVATE);

        Map<String, Integer> map = (Map<String, Integer>) sharedPreferences.getAll();
        map.remove("init");//remove initialization tag

        ValueComparator vc = new ValueComparator(map);
        TreeMap<String, Integer> sorted = new TreeMap<String, Integer>(vc);
        sorted.putAll(map);
        Lg.d("___pub type size = " + sorted.size());

        List<String> types = new ArrayList<String>(sorted.keySet());
        types.add(context.getResources().getString(R.string.customize));
        return types;
    }

    private class ValueComparator implements Comparator<String> {
        Map<String, Integer> base;

        public ValueComparator(Map<String, Integer> map) {
            base = map;
        }

        @Override
        public int compare(String a, String b) {

            if (base.get(a) >= base.get(b)) {
                return -1;
            } else {
                return 1;
            }
        }
    }

}
