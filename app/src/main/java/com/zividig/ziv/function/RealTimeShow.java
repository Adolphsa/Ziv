package com.zividig.ziv.function;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bm.library.PhotoView;
import com.google.gson.Gson;
import com.zivdigi.helloffmpeg.SurfaceActivity;
import com.zividig.ziv.R;
import com.zividig.ziv.bean.RealTimeBean;
import com.zividig.ziv.main.Login;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

/**
 * 实时预览
 * Created by Administrator on 2016-05-30.
 */
public class RealTimeShow extends Activity {


    private PhotoView photoView;
    private ProgressBar progressBar;
    private ImageOptions options;

    private String url; //图片的地址
    private int ScreenWidth;
    private Button btRefresh;
    private Button btDownImage;

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

        //获取屏幕宽高
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        ScreenWidth = size.x;

        //按钮
        BtnListener listener = new BtnListener();
        //图片刷新
        btRefresh = (Button) findViewById(R.id.bt_refresh);
        //图片下载
        btDownImage = (Button) findViewById(R.id.bt_downImage);
        btRefresh.setOnClickListener(listener);
        btDownImage.setOnClickListener(listener);

        showImage(); //显示图片

    }

    /**
     * 显示图片
     */
    private void showImage() {
        progressBar.setVisibility(View.VISIBLE);
        btRefresh.setClickable(false);
        System.out.println("获取图片");
        //获取图片链接
        RequestParams params = new RequestParams("http://120.24.174.213:9501/api/snap");
        String devid = Login.getDevId();
        params.addBodyParameter("devid", devid);
        System.out.println("实时预览" + Login.getDevId());
        System.out.println("请求连接" + params);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                System.out.println("返回的数组是：" + result.toString());
                Gson gson = new Gson();
                RealTimeBean realTimeBean = gson.fromJson(result, RealTimeBean.class);

                if (realTimeBean.getPicinfo().size() > 0){
                    url = realTimeBean.getPicinfo().get(0).getUrl();
                    System.out.println("url的值---" + url);
                }

                int  error = realTimeBean.getError();
                System.out.println("error的值：---" + error);

                switch (error) {
                    case 200: //返回正常
                        if (!url.isEmpty()){
                            getImageFromInternet();
                        }
                        break;
                    case 400:
                        showToast("错误请求-请求中有语法问题");
                        break;
                    case 401:
                        showToast("未授权");
                        break;
                    case 501:
                        showToast("设备不在线");
                        break;
                    case 502:
                        showToast("服务器内部出错");
                        break;
                    case 503:
                        showToast("不支持此操作");
                        break;
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                btRefresh.setClickable(true);
                System.out.println("返回json错误" + ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {}
        });


    }

    /**
     * 获取图片
     */
    private void getImageFromInternet() {

        //显示图片
        options = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.FIT_CENTER)
                .build();
        x.image().bind(photoView, url, options, new Callback.CommonCallback<Drawable>() {

            @Override
            public void onSuccess(Drawable result) {
                progressBar.setVisibility(View.INVISIBLE);
                System.out.println("图片URL加载成功");

                int intrinsicWidth = result.getIntrinsicWidth();
                int intrinsicHeight = result.getIntrinsicHeight();
                System.out.println("图片的宽度：" + intrinsicWidth + "，图片的高度：" + intrinsicHeight);

                btRefresh.setClickable(true);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

                System.out.println("加载错误" + ex);
                btRefresh.setClickable(true);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                System.out.println("加载取消");
            }

            @Override
            public void onFinished() {
                btRefresh.setClickable(true);
            }
        });

    }

    /**
     * 下载图片到本地
     */
    private void downImage() {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            long freeSpace = Environment.getExternalStorageDirectory().getFreeSpace();
            System.out.println("可用空间:" + freeSpace/1000000 + "MB");
//            freeSpace = (long) 0.5;
            if ((freeSpace/1000000.0) < 1 ){
                Toast.makeText(RealTimeShow.this,"存储空间不足",Toast.LENGTH_SHORT).show();
                return;
            }

            File file = new File(Environment.getExternalStorageDirectory(), "Ziv"); //创建Ziv文件夹
            if (!file.exists()) {
                System.out.println("创建");
                boolean mkdirs = file.mkdirs();
            }

            System.out.println(file);
            final String target = file + "/" + getDateAndTime() + ".png";

            System.out.println(target);

            x.image().loadFile(url, options, new Callback.CacheCallback<File>() {
                @Override
                public boolean onCache(File result) {
                    System.out.println("图片的地址：" + result.getPath() + "---图片的名称：" + result.getName());
                    try {
                        FileInputStream fsFrom = new FileInputStream(result);
                        FileOutputStream fsTo = new FileOutputStream(new File(target));

                        byte[] bt = new byte[1024 * 1024];
                        int c;
                        while ((c = fsFrom.read(bt)) > 0) {
                            fsTo.write(bt, 0, c);

                            System.out.println("c的值---" + c);
                        }

                        fsFrom.close();
                        fsTo.close();

                        updateImage();
                        Toast.makeText(RealTimeShow.this, "图片已保存", Toast.LENGTH_SHORT).show();
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return true;
                }

                @Override
                public void onSuccess(File result) {
                    if (result == null) {
                        System.out.println("结果为空");
                    } else {
                        System.out.println("结果不为空");
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Toast.makeText(RealTimeShow.this, "网络异常", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {
                    updateImage();
                    Toast.makeText(RealTimeShow.this, "图片已保存", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(RealTimeShow.this, "请先刷新图片", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 更新图片
     */
    private void updateImage() {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        String path = Environment.getExternalStorageDirectory() + "/Ziv";
        Uri uri = Uri.fromFile(new File(path));
        intent.setData(uri);
        this.sendBroadcast(intent);
    }

    /**
     * 显示提示信息
     * @param str  字符
     */
    private void showToast(String str){
        progressBar.setVisibility(View.INVISIBLE);
        btRefresh.setClickable(true);
        Toast toast = Toast.makeText(RealTimeShow.this, str, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /***
     * 获取时间和日期
     *
     * @return string
     */
    public String getDateAndTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = sDateFormat.format(new java.util.Date());
        System.out.println(date);
        return date;
    }

    class BtnListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
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

    /**
     * 开启实时视频
     * @param view
     */
    public void startVideo(View view){
        startActivity(new Intent(RealTimeShow.this, SurfaceActivity.class));
    }
}
