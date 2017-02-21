package com.zividig.ziv.function;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.bm.library.PhotoView;
import com.google.gson.Gson;
import com.zividig.ziv.R;
import com.zividig.ziv.bean.RealTimeBean;
import com.zividig.ziv.main.BaseActivity;
import com.zividig.ziv.main.ZivApp;
import com.zividig.ziv.utils.DialogUtils;
import com.zividig.ziv.utils.Others;
import com.zividig.ziv.utils.ToastShow;
import com.zividig.ziv.utils.Urls;
import com.zividig.ziv.utils.UtcTimeUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;

/**
 * 实时预览
 * Created by Administrator on 2016-05-30.
 */
public class RealTimeShow extends BaseActivity {

    private Context mContext;
    private PhotoView photoView;
    private ProgressBar progressBar;
    private ImageOptions options;

    private String url; //图片的地址
    private int ScreenWidth;
    private Button btRefresh;
    private Button btDownImage;
    private int errorCode; //错误码
    private String devid;

    Handler mHandler = new Handler();
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            getImageUrl();
            mHandler.postDelayed(this,1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(ZivApp.getInstance());
        setContentView(R.layout.acticity_real_time_show);

        mContext = RealTimeShow.this;
        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.tv_title);
        txtTitle.setText("图片抓拍");

        SharedPreferences spf = getSharedPreferences("config",MODE_PRIVATE);
        devid = spf.getString("devid","");


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

        //按钮监听
        BtnListener listener = new BtnListener();

        btRefresh = (Button) findViewById(R.id.bt_refresh); //图片刷新
        btDownImage = (Button) findViewById(R.id.bt_downImage);  //图片下载

        btRefresh.setOnClickListener(listener);
        btDownImage.setOnClickListener(listener);

        getImageUrl(); //显示图片

    }

    /**
     * 显示图片
     */
    private void getImageUrl() {

        progressBar.setVisibility(View.VISIBLE);
        btRefresh.setClickable(false);

        //获取图片链接
        RequestParams params = new RequestParams(Urls.URL_PIC_SNAP);
//        params.setConnectTimeout(120*1000); //超时120s
        params.addBodyParameter("devid", devid);
        System.out.println("实时预览" + devid);
        System.out.println("获取图片URL" + params);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                System.out.println("返回的数组是：" + result.toString());
                Gson gson = new Gson();
                RealTimeBean realTimeBean = gson.fromJson(result, RealTimeBean.class);

                if (realTimeBean.getPicinfo().size() > 0) {
                    url = realTimeBean.getPicinfo().get(0).getUrl();
                    System.out.println("url的值---" + url);
                }

                errorCode = realTimeBean.getError();
                System.out.println("error的值：---" + errorCode);

                switch (errorCode) {
                    case 200: //返回正常
                        if (!url.isEmpty()) {
                            System.out.println("url返回值正常");
                            getImageFromInternet();
                            mHandler.removeCallbacks(mRunnable);
                        }else{
                            System.out.println("url返回值为空");
                            mHandler.postDelayed(mRunnable,1000); //每秒请求一次图片链接
                        }
                        break;
                    case 400:
                        ToastShow.showToast(RealTimeShow.this,"错误请求-请求中有语法问题");
                        break;
                    case 401:
                        ToastShow.showToast(RealTimeShow.this,"未授权");
                        break;
                    case 501:
                        System.out.println("可以执行唤醒主机的工作");
                        if (!RealTimeShow.this.isFinishing()){
                            DialogUtils.showPrompt(RealTimeShow.this, "提示", "设备不在线", "确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                        }

                        break;
                    case 502:
                        ToastShow.showToast(RealTimeShow.this,"服务器内部出错");
                        break;
                    case 503:
                        ToastShow.showToast(RealTimeShow.this,"不支持此操作");
                        break;
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

                btRefresh.setClickable(true);
                System.out.println("返回json错误" + ex);
                progressBar.setVisibility(View.INVISIBLE);
//                ToastShow.showToast(RealTimeShow.this,"图片访问错误");
                if (!RealTimeShow.this.isFinishing()){
                    DialogUtils.showPrompt(RealTimeShow.this, "提示", "返回json错误", "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
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
            System.out.println("可用空间:" + freeSpace / 1000000 + "MB");
            if ((freeSpace / 1000000.0) < 1) {
                Toast.makeText(RealTimeShow.this, "存储空间不足", Toast.LENGTH_SHORT).show();
                return;
            }

            File zivFile = new File(Environment.getExternalStorageDirectory(), "Ziv"); //创建Ziv文件夹
            if (!zivFile.exists()){
                System.out.println("ziv创建");
                zivFile.mkdirs();
            }
            final File imageFile = new File(zivFile ,"images");
            if (!imageFile.exists()) {
                System.out.println("images创建");
                imageFile.mkdirs();
            }

            System.out.println(imageFile);
            final String target = UtcTimeUtils.getDateAndTime() + ".png";
            final File file = new File(imageFile,target);
            System.out.println(target);

            x.image().loadFile(url, options, new Callback.CacheCallback<File>() {
                @Override
                public boolean onCache(File result) {
                    System.out.println("图片的地址：" + result.getPath() + "---图片的名称：" + result.getName());
                    Bitmap bmp = android.graphics.BitmapFactory.decodeFile(result.getPath());
                    Others.saveImageToGallery(mContext,bmp,file,target);
                    updateImage();
                    ToastShow.showToast(RealTimeShow.this,"图片已保存");
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
                   ToastShow.showToast(RealTimeShow.this,"网络异常");
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {}
            });

        } else {
            ToastShow.showToast(RealTimeShow.this,"请先刷新图片");
        }

    }

    /**
     * 更新图片
     */
    private void updateImage() {
        System.out.println("realTimeShow---更新图片");
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        String path = Environment.getExternalStorageDirectory() + "/Ziv/images";
        Uri uri = Uri.fromFile(new File(path));
        intent.setData(uri);
        this.sendBroadcast(intent);
    }




    class BtnListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_refresh:
                    photoView.setImageResource(R.mipmap.default_white);
                    photoView.setBackgroundColor(Color.WHITE);
                    getImageUrl();
                    break;
                case R.id.bt_downImage:
                    downImage();
                    break;
            }
        }
    }



    /**
     * 主机唤醒
     */
    private void wakeupDevice(){
        RequestParams params = new RequestParams(Urls.DEVICE_WAKEUP);
        params.addQueryStringParameter("devid",devid);
        System.out.println("主机唤醒：" + params);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println("主机唤醒结果：" + result);
                try {
                    JSONObject json = new JSONObject(result);
                    int errorCode = json.getInt("error");
                    System.out.println("错误码是：" + errorCode);
                    if (errorCode == 200){
                        ToastShow.showToast(RealTimeShow.this,"主机正在唤醒中...");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println("主机唤醒错误：" + ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


}
