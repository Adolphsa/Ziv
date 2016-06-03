package com.zividig.ziv.function;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bm.library.PhotoView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.zividig.ziv.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * 实时预览
 * Created by Administrator on 2016-05-30.
 */
public class RealTimeShow extends Activity {

    private static String[] mImgUrls = {"http://120.25.80.80/~adolph/zivApp/picture/test_pic0.jpg",
            "http://120.25.80.80/~adolph/zivApp/picture/test_pic1.jpg",
            "http://120.25.80.80/~adolph/zivApp/picture/test_pic2.jpg",
            "http://120.25.80.80/~adolph/zivApp/picture/test_pic3.jpg",
            "http://120.25.80.80/~adolph/zivApp/picture/test_pic4.jpg"};
    private int i;

    private PhotoView photoView;
    private ProgressBar progressBar;
    private ImageOptions options;

    private String[] urls;
    private String url; //图片的地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_real_time_show);

        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.tv_title);
        txtTitle.setText("实时预览");

        //返回按钮
        Button btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        photoView = (PhotoView) findViewById(R.id.ph_img); //图片显示控件
        photoView.enable();

        progressBar = (ProgressBar) findViewById(R.id.pb_img); //进度条


        //按钮
        BtnListener listener = new BtnListener();
        Button btRefresh = (Button) findViewById(R.id.bt_refresh); //图片刷新
        Button btDownImage = (Button) findViewById(R.id.bt_downImage); //图片下载
        btRefresh.setOnClickListener(listener);
        btDownImage.setOnClickListener(listener);

        showImage(); //显示图片
    }

    private void showImage(){
        progressBar.setVisibility(View.VISIBLE);
        System.out.println("获取图片");
        //获取图片链接
        org.xutils.http.RequestParams params = new org.xutils.http.RequestParams("http://192.168.1.2:9501/");
        x.http().get(params, new Callback.CommonCallback<JSONObject>() {

            @Override
            public void onSuccess(JSONObject result) {
                System.out.println("返回的数组是：" + result.toString());
                try {
                    int picnum = result.getInt("picnum");
                    JSONArray picinfo = result.getJSONArray("picinfo");
                    for (int i = 0; i < picnum ; i++) {
                        JSONObject temp = (JSONObject) picinfo.get(i);
                        url = temp.getString("url");
                        System.out.println(url);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                //显示图片
                options = new ImageOptions.Builder()
                        .setImageScaleType(ImageView.ScaleType.FIT_XY)
                        .build();
                x.image().bind(photoView, url, options, new Callback.CommonCallback<Drawable>() {

                    @Override
                    public void onSuccess(Drawable result) {
                        progressBar.setVisibility(View.INVISIBLE);
                        System.out.println("加载成功");
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {

                        System.out.println("加载错误" + ex);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        System.out.println("加载取消");
                    }

                    @Override
                    public void onFinished() {

//                System.out.println("加载完成");
//                if (i >= 4){
//                    i = i%4;
//                    System.out.println("if语句运行" + i);
//                }else {
//                    System.out.println("----" + i);
//                    i++;
//                }
                    }
                });
            }
        });


    }

    //下载图片到本地
    private void downImage(){
        HttpUtils httpUtils = new HttpUtils();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){

            File file = new File(Environment.getExternalStorageDirectory(),"Ziv"); //创建Ziv文件夹
            if (!file.exists()){
                System.out.println("创建");
                file.mkdirs();
            }
            System.out.println(file);
            String target = file  + "/" +getDateAndTime() + ".jpg";
            System.out.println(target);
            httpUtils.download(mImgUrls[i], target, false, new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    Toast.makeText(RealTimeShow.this,"图片已下载",Toast.LENGTH_SHORT).show();
                    System.out.println(mImgUrls[i] + "---" + i);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(RealTimeShow.this,"下载失败",Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    /***
     * 获取时间和日期
     * @return string
     */
    public String getDateAndTime(){
        SimpleDateFormat sDateFormat =  new SimpleDateFormat("yyyyMMddHHmmss");
        String date = sDateFormat.format(new java.util.Date());
        System.out.println(date);
        return date;
    }
    class BtnListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bt_refresh:
                    photoView.setImageResource(R.mipmap.default_white);
                    photoView.setBackgroundColor(Color.WHITE);
                    showImage();
                    break;
                case R.id.bt_downImage:
                    downImage();
                    break;
            }
        }
    }
}
