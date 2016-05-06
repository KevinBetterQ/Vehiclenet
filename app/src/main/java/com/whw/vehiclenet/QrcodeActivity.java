package com.whw.vehiclenet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xys.libzxing.zxing.activity.CaptureActivity;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

/**
 * Created by 10938 on 2016/5/6.
 */
public class QrcodeActivity extends Activity {

    private TextView mShowQrcode;
    private EditText mInput;
    private ImageView mMakeQrcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        mShowQrcode = (TextView) findViewById(R.id.tv_qrshow);
        mInput = (EditText) findViewById(R.id.et_input);
        mMakeQrcode = (ImageView) findViewById(R.id.iv_qrcode);
    }

    public void scan(View view){
        startActivityForResult(new Intent(QrcodeActivity.this, CaptureActivity.class),0);
    }
    public void make(View view){
        String input = mInput.getText().toString();
        if (input.equals("")){
            Toast.makeText(QrcodeActivity.this,"请输入文本",Toast.LENGTH_LONG).show();
        }else{
            Bitmap bitmap = EncodingUtils.createQRCode(input,500,500,null);
            mMakeQrcode.setImageBitmap(bitmap);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            String result = bundle.getString("result");
            mShowQrcode.setText(result);
        }
    }
}
