package com.zividig.ziv.function;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bm.library.PhotoView;
import com.zividig.ziv.R;

/**
 * 展示图片
 * Created by Administrator on 2016-06-04.
 */
public class ShowPicture extends Activity {

    private PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_picture);

        //返回按钮
        Button btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        String picUrl = bundle.getString("pic_url");

        photoView = (PhotoView) findViewById(R.id.pv_picture);
        photoView.enable();
        photoView.setImageBitmap(BitmapFactory.decodeFile(picUrl));
    }
}
