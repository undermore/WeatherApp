package com.example.weatherapp;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScrollingActivity extends AppCompatActivity implements OnRequestCompleted {

    Map<String, ArrayList<List>> WeathersMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        OpenWeatherMapAPI requestTask = new OpenWeatherMapAPI(this);
        requestTask.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGetData(String result){
        TextView itemTv = findViewById(R.id.city_item);
        ArrayList<List> weathersList = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(result);
            weathersList = Util.jsonParser(obj);
        }catch (Exception e){
            e.printStackTrace();
        }
        String str = "";
        for (int i=0; i < weathersList.size(); i++) {
            List<String> l = weathersList.get(i);
            str += l.get(0);
            str += " ";
            str += l.get(1);
            str += " ";

            str += "日期";
            str += l.get(2);
            str += " ";

            str += l.get(3);
            str += " ";

            str += "最高气温：";
            str += l.get(4);
            str += "℃ ";

            str += "最低气温：";
            str += l.get(5);
            str += "℃ ";

            str += "湿度：";
            str += l.get(6);
            str += "% ";

            str += "气压：";
            str += l.get(7);
            str += " ";

            str += "\r\n";
        }
        itemTv.setText(str);
    }
}
