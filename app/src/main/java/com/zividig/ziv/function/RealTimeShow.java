package com.zividig.ziv.function;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bm.library.PhotoView;
import com.google.gson.Gson;
import com.zividig.ziv.R;
import com.zividig.ziv.bean.RealTimeBean;
import com.zividig.ziv.main.Login;

import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private Drawable localDraw;

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
        Button btDownImage = (Button) findViewById(R.id.bt_downImage); //图片下载
        btRefresh.setOnClickListener(listener);
        btDownImage.setOnClickListener(listener);

        showImage(); //显示图片

    }

    private void showImage() {
        progressBar.setVisibility(View.VISIBLE);
        btRefresh.setClickable(false);
        System.out.println("获取图片");
        //获取图片链接
        org.xutils.http.RequestParams params = new org.xutils.http.RequestParams("http://120.24.174.213:9501/");
        String devid = Login.getDevId();
        params.addBodyParameter("devid", devid);
        System.out.println("实时预览" + Login.getDevId());
        System.out.println("请求连接" + params);
        x.http().get(params, new Callback.CommonCallback<String>() {

            private int imageHight;

            @Override
            public void onSuccess(String result) {
                System.out.println("返回的数组是：" + result.toString());
                Gson gson = new Gson();
                RealTimeBean realTimeBean = gson.fromJson(result, RealTimeBean.class);
                url = realTimeBean.getPicinfo().get(0).getUrl();

                //显示图片
                options = new ImageOptions.Builder()
                        .setImageScaleType(ImageView.ScaleType.FIT_CENTER)
                        .build();
                x.image().bind(photoView, url, options, new Callback.CommonCallback<Drawable>() {

                    @Override
                    public void onSuccess(Drawable result) {
                        progressBar.setVisibility(View.INVISIBLE);
                        System.out.println("图片URL加载成功");

                        //把得到的Drawable保存一份
                        localDraw = result;

                        int intrinsicWidth = result.getIntrinsicWidth();
                        int intrinsicHeight = result.getIntrinsicHeight();
                        System.out.println("图片的宽度：" + intrinsicWidth + "，图片的高度：" + intrinsicHeight);

                        imageHight = intrinsicHeight * ScreenWidth / intrinsicWidth;

                        if (!url.isEmpty()) {  //如果图片链接不为空，则将刷新按钮置为可点击状态
                            btRefresh.setClickable(true);
                        }


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
                    }
                });
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                btRefresh.setClickable(true);
                System.out.println("返回json错误" + ex);
                Toast.makeText(RealTimeShow.this, "网络异常,请检查网络连接", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });


    }

    //下载图片到本地
    private void downImage() {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && localDraw != null) {

            File file = new File(Environment.getExternalStorageDirectory(), "Ziv"); //创建Ziv文件夹
            if (!file.exists()) {
                System.out.println("创建");
                file.mkdirs();
            }
            System.out.println(file);
            String target = file + "/" + getDateAndTime() + ".jpg";
            System.out.println(target);

            Bitmap logoBitmap = drawable2Bitmap(localDraw);
            ByteArrayOutputStream logoStream = new ByteArrayOutputStream();
            boolean res = logoBitmap.compress(Bitmap.CompressFormat.PNG, 100, logoStream);
            //将图像读取到logoStream中
            byte[] logoBuf = logoStream.toByteArray();
            //将图像保存到byte[]中
            Bitmap temp = BitmapFactory.decodeByteArray(logoBuf, 0, logoBuf.length);
            //将图像从byte[]中读取生成Bitmap 对象 temp
            saveBitmapToSdCard(target, temp);

        }else {
            Toast.makeText(RealTimeShow.this,"请先刷新图片",Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * Drawable 转 bitmap
     *
     * @param drawable
     * @return
     */
    public  Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    /**
     * 保存图片到SD卡
     */
    private void saveBitmapToSdCard(String target,Bitmap mBitmap) {

        File f = new File(target);
        System.out.println("f的路径" + f.getAbsolutePath());
        try {
            f.createNewFile();
        } catch (IOException e) {

        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            System.out.println("图片保存完毕");
            updateImage();
            Toast.makeText(RealTimeShow.this,"图片已保存",Toast.LENGTH_SHORT).show();
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
}
