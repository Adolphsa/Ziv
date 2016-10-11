package com.zividig.ziv.function;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bm.library.PhotoView;
import com.zividig.ziv.R;
import com.zividig.ziv.main.BaseActivity;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 展示图片
 * Created by Administrator on 2016-06-04.
 */
public class ShowPicture extends BaseActivity {

    private PhotoView photoView;
    private String picUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_picture);

        ShareSDK.initSDK(this); //初始化ShareSDK

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
        picUrl = bundle.getString("pic_url");
        System.out.println("pic_url---" + picUrl);

        photoView = (PhotoView) findViewById(R.id.pv_picture);
        photoView.enable();
        photoView.setImageBitmap(BitmapFactory.decodeFile(picUrl));
    }

    public void share(View view){
        System.out.println("分享按钮被点击");

        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题：微信、QQ（新浪微博不需要标题）
//        oks.setTitle("我是分享标题");  //最多30个字符

        // text是分享文本：所有平台都需要这个字段
//        oks.setText("我是分享文本，啦啦啦~http://uestcbmi.com/");  //最多40个字符

        // imagePath是图片的本地路径：除Linked-In以外的平台都支持此参数
        oks.setImagePath(picUrl);//确保SDcard下面存在此张图片

        //网络图片的url：所有平台
//        oks.setImageUrl("http://7sby7r.com1.z0.glb.clouddn.com/CYSJ_02.jpg");//网络图片rul

        // url：仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl("http://sharesdk.cn");   //网友点进链接后，可以看到分享的详情

        // Url：仅在QQ空间使用
//        oks.setTitleUrl("http://www.baidu.com");  //网友点进链接后，可以看到分享的详情

        // 启动分享GUI
        oks.show(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        ShareSDK.stopSDK();
    }
}
