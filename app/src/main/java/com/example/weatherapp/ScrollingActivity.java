package com.example.weatherapp;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ScrollingActivity extends AppCompatActivity implements OnRequestCompleted {

    public static Context mContext;
    List<OpenWeatherMapAPI> taskGroup;//TODO 持久化保存用户添加的城市
    Map<String, ArrayList<List>> WeathersMap;
    ExpandableListView expandableListView;
    ExpandableAdapter expandableAdapter;

    String currLocation = "当前位置";
    String defaultLocation = "佛山";
    List<String> supportCities = Arrays.asList("佛山","北京","上海","广州","深圳","长春");
    int City = 0;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getApplicationContext();
        WeathersMap = new HashMap<>();
        taskGroup = new ArrayList<>();
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        expandableListView = findViewById(R.id.expandable_listview);
        expandableListView.setGroupIndicator(null);

        expandableAdapter = new ExpandableAdapter(this, R.layout.list_group, R.layout.list_child);
        expandableListView.setAdapter(expandableAdapter);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION) || !hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                reqPermissions();
            } else {
                setLocationListener();
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 弹出下拉菜单选择地点
                String cityName = supportCities.get(City++%supportCities.size());
                LatLng latLng = Util.getLatAndLng(cityName);
                if(latLng != null){
                    if(addCity(cityName, latLng)){
                        Snackbar.make(view, "添加" + cityName, Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                } else {
                    //TODO 定位失败则使用城市名请求天气数据
                    Snackbar.make(view, "获取经纬度失败,请检查 GPS 设置", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        });

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


        //Child点击事件
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {
                List oneDay = (List)(expandableAdapter.getChild(groupPosition, childPosition));
                return false;
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

    private void setLocationListener(){
        try {
            if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) || hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {

                LocationListener locationListener = new LocationListener() {
                    @Override //TODO 地理位置改变时更新天气
                    public void onLocationChanged(Location location) {}
                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {}
                    @Override
                    public void onProviderEnabled(String provider) {}
                    @Override
                    public void onProviderDisabled(String provider) {}
                };
                locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
                boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                Location location = null;
                if (!isGPSEnabled && !isNetworkEnabled) {
                    Snackbar.make(getWindow().getDecorView(), "已获得定位权限，请打开GPS或移动数据，以便定位，添加默认城市", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    addCity(defaultLocation, Util.getLatAndLng(defaultLocation));
                } else {
                    if (isGPSEnabled) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 2, locationListener);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }

                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 2, locationListener);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }
                    }

                    if (location != null) {
                        addCity(currLocation, new LatLng(location.getLatitude(),location.getLongitude()));
                    } else {
                        Snackbar.make(getWindow().getDecorView(), "GPS或移动数据定位失败，添加默认城市", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                        addCity(defaultLocation, Util.getLatAndLng(defaultLocation));
                    }
                }
            } else {
                Snackbar.make(getWindow().getDecorView(), "未获得定位权限，添加默认城市", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                addCity(defaultLocation, Util.getLatAndLng(defaultLocation));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean hasPermission (String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void reqPermissions () {
        String[] PERMISSIONS = {ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION};
        ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        setLocationListener();
    }

}
