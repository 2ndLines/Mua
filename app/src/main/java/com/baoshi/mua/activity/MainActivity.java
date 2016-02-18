package com.baoshi.mua.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.baoshi.mua.R;
import com.baoshi.mua.model.Carrier;
import com.baoshi.mua.model.Creator;
import com.baoshi.mua.model.avos.AVPublication;
import com.baoshi.mua.model.avos.User;
import com.baoshi.mua.utils.Lg;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.tojc.ormlite.android.annotation.OrmLiteAnnotationAccessor;

import java.lang.reflect.Field;
import java.sql.SQLException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import roboguice.util.temp.Ln;


public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity" ;
    @InjectView(R.id.txt)
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_def);
        ButterKnife.inject(this);
        Ln.getConfig().setLoggingLevel(Log.VERBOSE);

        login();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void login() {
        if(AVUser.getCurrentUser() == null) {
            AVUser.logInInBackground("15889747041","marie8888",new LogInCallback<AVUser>() {
                @Override
                public void done(AVUser avUser, AVException e) {
                    if(e != null){
                        Lg.e("Fail to login, Cause = " + e.getMessage());
                    }else{
                        Lg.d("+++++Success to login lean cloud. Current user = " + avUser.getUsername());
                    }
                }
            });
        }

    }

    public void onButtonClick(View view) {
        if(view.getId() == R.id.buttonCreate) {

            AVPublication pub = new AVPublication(null);
            pub.setCaption("RoboSpice Test!");

            Carrier car = new Carrier();
            car.setForm(Carrier.CarForm.HORN);
//            car.setType(Publication.CarType.DISCLOSE);
            pub.setCarrier(car);
            User user  = AVUser.getCurrentUser(User.class);
            Creator creator = new Creator(user);

            RuntimeExceptionDao<AVPublication,String> dao = getHelper().getRuntimeExceptionDao(AVPublication.class);
            int id = dao.create(pub);

            Lg.d("___save result : " + id);
            try {
                long size = getHelper().countOfAllObjectsOfTable(AVPublication.class);
                Lg.d("___Data count : " + size);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            for(Field field : AVPublication.class.getDeclaredFields()){
                String columnName = OrmLiteAnnotationAccessor.getAnnotationColumnName(field);
                Lg.d("___get column Name = " + columnName+" ");
            }

        }else if(view.getId() == R.id.buttonQuery) {




        }
    }


}
