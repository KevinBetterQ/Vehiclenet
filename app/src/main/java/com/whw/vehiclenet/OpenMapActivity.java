package com.whw.vehiclenet;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by 10938 on 2016/5/3.
 */
public class OpenMapActivity extends Activity {

    private TextView textView_head_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_openmap);

        textView_head_title = (TextView) findViewById(R.id.textView_head_title);
        textView_head_title.setText("地图");
    }
}
