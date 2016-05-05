package com.whw.vehiclenet;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;


/**
 * Created by 10938 on 2016/5/3.
 */
public class OpenMapActivity extends Activity {

    private TextView textView_head_title;
    private MapView mMapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_openmap);

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);

        textView_head_title = (TextView) findViewById(R.id.textView_head_title);
        textView_head_title.setText("地图");
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
}
