package com.whw.vehiclenet;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.whw.pubclass.PublicClass;
import com.whw.pubclass.PublicLocModeClass;
import com.whw.pubclass.PublicZoomClass;


/**
 * Created by 10938 on 2016/5/3.
 */
public class OpenMapActivity extends Activity {

    //标题
    private TextView textView_head_title;

    //返回按钮
    private ImageView imageView_route_back;

    // 定位控件
    private ImageButton btn_location;
    //实时交通按钮
    private ImageButton btn_traffic;

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Context context;

    MyClickListener clickListener = new MyClickListener();

    //定位相关
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;//定位的监听器
    private boolean isFirstIn = true;//判断是否为第一次的定位
    private double mLatitude;//记录经度
    private double mLongtitude;//记录纬度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_openmap);

        this.context = this;

        initView();//初始化地图
        initLocation();//初始化定位

        textView_head_title = (TextView) findViewById(R.id.textView_head_title);
        textView_head_title.setText("地图");

        // 返回按钮
        imageView_route_back = (ImageView) findViewById(R.id.imageView_head_back);
        imageView_route_back.setOnClickListener(clickListener);
        // 定位按钮
        btn_location = (ImageButton) findViewById(R.id.btn_location);
        btn_location.setOnClickListener(clickListener);
        // 实时交通按钮
        btn_traffic = (ImageButton) findViewById(R.id.traffic);
        btn_traffic.setOnClickListener(clickListener);
    }

    private void initLocation() {
        mLocationClient = new LocationClient(this);
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener((BDLocationListener) mLocationListener);//注册Listener
        //设置Client
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setScanSpan(1000);

        mLocationClient.setLocOption(option);
    }

    private void initView() {
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16.0f);//设置地图比例尺100米
        mBaiduMap.setMapStatus(msu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBaiduMap.setMyLocationEnabled(true);
        if(!mLocationClient.isStarted())
            mLocationClient.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // activity 暂停时同时暂停地图控件
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // activity 恢复时同时恢复地图控件
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // activity 销毁时同时销毁地图控件
        mMapView.onDestroy();
    }

    //定位到我的位置
    private void centerToMyLocation() {
        LatLng latLng = new LatLng(mLatitude,mLongtitude);//获得经纬度
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);//设置经纬度
        mBaiduMap.animateMapStatus(msu);
    }


    // button 点击事件监听器
    private class MyClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                //返回控件
                case R.id.imageView_head_back:
                    OpenMapActivity.this.finish();
                    break;

                //定位控件
                case R.id.btn_location:
                    centerToMyLocation();
                    break;

                //点击实时交通
                case R.id.traffic:
                    if(mBaiduMap.isTrafficEnabled())//判断是否已经打开实时交通
                    {
                        mBaiduMap.setTrafficEnabled(false);//关闭实时交通
                        PublicClass.showToast(context, "实时交通(OFF)");
                    }else
                    {
                        mBaiduMap.setTrafficEnabled(true);//打开实时交通
                        PublicClass.showToast(context, "实时交通(ON)");
                    }
                    break;

                default:
                    break;
            }
        }
    }

    private class MyLocationListener implements BDLocationListener
    {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //转化BDLocation
            MyLocationData data = new MyLocationData.Builder()//
                    .accuracy(location.getRadius())//
                    .latitude(location.getLatitude())//
                    .longitude(location.getLongitude())//
                    .build();
            mBaiduMap.setMyLocationData(data);


            mLatitude = location.getLatitude();
            mLongtitude = location.getLongitude();

            if(isFirstIn)
            {
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());//获得经纬度
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);//设置经纬度
                mBaiduMap.animateMapStatus(msu);
                isFirstIn = false;
                //弹出一个定位位置框
                Toast.makeText(context, location.getAddrStr(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
