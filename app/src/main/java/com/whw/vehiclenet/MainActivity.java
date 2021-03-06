package com.whw.vehiclenet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.baidu.mapapi.SDKInitializer;
import com.whw.pubclass.PublicClass;

public class MainActivity extends Activity {

    private Context context;
    private Button button_map ;
    private Button button_gas = null;
    private Button button_route = null;
    private Button button_cars = null;
    private Button button_llleagl = null;
    private Button button_weather = null;
    private Button button_person = null;
    private Button button_infocars = null;
    private Button button_order = null;
    private Button button_qrcode = null;

    private long exitTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        this.context = this;

        MyClickListener clickListener = new MyClickListener();

        button_cars = (Button) findViewById(R.id.button4_cars);
        button_cars.setOnClickListener(clickListener);

        button_gas = (Button) findViewById(R.id.button2_gas);
        button_gas.setOnClickListener(clickListener);

        button_infocars = (Button) findViewById(R.id.button8_infocars);
        button_infocars.setOnClickListener(clickListener);

        button_llleagl = (Button) findViewById(R.id.button5_llleagl);
        button_llleagl.setOnClickListener(clickListener);

        button_map = (Button) findViewById(R.id.button1_map);
        button_map.setOnClickListener(clickListener);

        button_order = (Button) findViewById(R.id.button9_order);
        button_order.setOnClickListener(clickListener);

        button_person = (Button) findViewById(R.id.button7_person);
        button_person.setOnClickListener(clickListener);

        button_route = (Button) findViewById(R.id.button3_route);
        button_route.setOnClickListener(clickListener);

        button_weather = (Button) findViewById(R.id.button6_weather);
        button_weather.setOnClickListener(clickListener);

        button_qrcode = (Button) findViewById(R.id.button10_qrcode);
        button_qrcode.setOnClickListener(clickListener);
    }

    private class MyClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId())
            {
			/* 地图服务模块 */
                case R.id.button1_map:
                    if (checkNet()) {
                        intent = new Intent(context, OpenMapActivity.class);
                    }
                    break;

			/* 预约加油模块 */
                case R.id.button2_gas:
                    if (checkNet())
                    {
                        intent = new Intent(context, ReserveGasActivity.class);
                    };
                    break;

			/* 路线规划模块 */
                case R.id.button3_route:
                    if (checkNet())
                    {
                        intent = new Intent(context, RoutePlanActivity.class);
                    }
                    break;

			/* 车辆维护模块 */
                case R.id.button4_cars:
                    if (checkNet())
                    {
                        intent = new Intent(context, CarsMaintainActivity.class);
                    }
                    break;

			/* 违章查询模块 */
                case R.id.button5_llleagl:
                    if (checkNet())
                    {
                        intent = new Intent(context, LlleaglVehicleActivity.class);
                    }
                    break;

			/* 个人信息模块 */
                case R.id.button7_person:
                    if (checkNet())
                    {
                        intent = new Intent(context, PersonActivity.class);
                    }
                    break;

                /* 汽车信息模块 */
                case R.id.button8_infocars:
                    if (checkNet())
                    {
                        intent = new Intent(context, InfoCarsActivity.class);
                    }
                    break;

                /* 我的订单模块 */
                case R.id.button9_order:
                    if (checkNet())
                    {
                        intent = new Intent(context, OrderActivity.class);
                    }
                    break;

                /* 天气模块 */
                case R.id.button6_weather:
                    if (checkNet())
                    {
                        intent = new Intent(context, WeatherActivity.class);
                    }
                    break;

                /* 二维码测试 */
                case R.id.button10_qrcode:
//                    if (checkNet())
//                    {
                        intent = new Intent(context, QrcodeActivity.class);
//                    }
                    break;

                default:
                    break;
            }
            if (intent != null)
            {
                startActivity(intent);
            }
        }

		/*
		 * SQLiteDatabase db = openOrCreateDatabase("db_charmkd.db",
		 * Context.MODE_PRIVATE, null);
		 * db.execSQL("DROP TABLE IF EXISTS table_charmkd"); db.execSQL(
		 * "CREATE TABLE IF NOT EXISTS table_charmkd (_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, image BLOB)"
		 * ); PublicClass.showToast(getApplicationContext(), "成功删除表！");
		 */
    }

    private boolean checkNet()
    {
        if (!PublicClass.isNetReachable(context))
        {
            PublicClass.showToast(context, "请检查网络连接");
            return false;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            if ((System.currentTimeMillis() - exitTime) > 2000)
            {
                PublicClass.showToast(context, "再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else
            {
                MainActivity.this.finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("已退出系统");
    }
}
