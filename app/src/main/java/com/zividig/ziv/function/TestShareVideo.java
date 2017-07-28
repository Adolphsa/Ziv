package com.zividig.ziv.function;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zivdigi.helloffmpeg.MyTestActivity;
import com.zividig.ziv.R;
import com.zividig.ziv.main.BaseActivity;

import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 测试分享视频
 * Created by Administrator on 2016-06-22.
 */
public class TestShareVideo extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_share_video);
    }

    public void shareVideo(View view){

        System.out.println("分享视频被点击");

        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题：微信、QQ（新浪微博不需要标题）
//        oks.setTitle("我是分享标题");  //最多30个字符

        // text是分享文本：所有平台都需要这个字段
        oks.setText("我是分享文本，http://120.25.80.80/~adolph/zivApp/testVideo.mp4");  //最多40个字符

        // imagePath是图片的本地路径：除Linked-In以外的平台都支持此参数
//        oks.setImagePath(picUrl);//确保SDcard下面存在此张图片

        //网络图片的url：所有平台
//        oks.setImageUrl("http://7sby7r.com1.z0.glb.clouddn.com/CYSJ_02.jpg");//网络图片rul

        // url：仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://120.25.80.80/~adolph/zivApp/testVideo.mp4");   //网友点进链接后，可以看到分享的详情

        // Url：仅在QQ空间使用
        oks.setTitleUrl("http://120.25.80.80/~adolph/zivApp/testVideo.mp4");  //网友点进链接后，可以看到分享的详情

        // 启动分享GUI
        oks.show(this);
    }

    public void ffmpeg(View view){
        startActivity(new Intent(TestShareVideo.this, MyTestActivity.class));
    }
}
