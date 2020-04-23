package com.example.weatherapp;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScrollingActivity extends AppCompatActivity implements OnRequestCompleted {

    public static Context mContext;
    List<OpenWeatherMapAPI> taskGroup;
    Map<String, ArrayList<List>> WeathersMap;
    ExpandableListView expandableListView;
    ExpandableAdapter expandableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        WeathersMap = new HashMap<>();
        taskGroup = new ArrayList<>();
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


        expandableListView = findViewById(R.id.expandable_listview);
        expandableListView.setGroupIndicator(null);

        expandableAdapter = new ExpandableAdapter(this, R.layout.list_group, R.layout.list_child);
        expandableListView.setAdapter(expandableAdapter);

        addCity("广州", new LatLng(23.106391, 113.27336));

        //Group点击事件
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, final int groupPosition, long id) {
                if ( expandableListView.isGroupExpanded(groupPosition) ) {
                    expandableListView.collapseGroup(groupPosition);
                }
                else {
                    expandableListView.expandGroup(groupPosition, true);
                }
                return true;
            }
        });
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
    public void onGetData(String result, String name){
        ArrayList<List> weathersList = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(result);
            weathersList = Util.jsonParser(obj);
            WeathersMap.put(name, weathersList);
        }catch (Exception e){
            e.printStackTrace();
        }
        expandableAdapter.updateWeather(name, weathersList);
    }

    private boolean addCity(String name, LatLng latLng){
        List<String>  existCity = expandableAdapter.getAllGroup();
        if(!existCity.contains(name)){
            if(taskGroup.add(new OpenWeatherMapAPI(this, name, latLng.longitude, latLng.latitude))){
                taskGroup.get(taskGroup.size()-1).execute();
                expandableAdapter.addGroup(name);
                expandableAdapter.notifyDataSetChanged();
                return true;
            }
        } else {
            Snackbar.make(getWindow().getDecorView(), "已添加" + name, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }
        return false;
    }

}
