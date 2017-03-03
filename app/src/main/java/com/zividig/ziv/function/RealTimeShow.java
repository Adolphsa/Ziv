package com.zividig.ziv.function;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.bm.library.PhotoView;
import com.zividig.ziv.R;
import com.zividig.ziv.bean.RealTimeBean;
import com.zividig.ziv.main.BaseActivity;
import com.zividig.ziv.main.ZivApp;
import com.zividig.ziv.utils.DialogUtils;
import com.zividig.ziv.utils.HttpParamsUtils;
import com.zividig.ziv.utils.JsonUtils;
import com.zividig.ziv.utils.Others;
import com.zividig.ziv.utils.SignatureUtils;
import com.zividig.ziv.utils.ToastShow;
import com.zividig.ziv.utils.Urls;
import com.zividig.ziv.utils.UtcTimeUtils;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import static com.zividig.ziv.utils.SignatureUtils.SIGNATURE_TOKEN;

/**
 * 实时预览
 * Created by Administrator on 2016-05-30.
 */
public class RealTimeShow extends BaseActivity {

    private static final int SNAP_FAIL = 30;
    private static final int DEVICE_NOT_ONLINE = 31;
    private static final int GET_IMAGE_URL_ERROR = 32;
    private static final int GET_IMAGE_SUCCESS = 33;
    private static final int GET_IMAGE_FAIL = 34;
    private static final int BEFORE_GET_IMAGE_URL = 35;

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
    private int getImageCount = 0;

    private long secondTime = 0;

    private Timer mTimer;

     Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SNAP_FAIL:     //抓图失败
                    if (!RealTimeShow.this.isFinishing()){
                        DialogUtils.showPrompt(RealTimeShow.this, "提示", "抓图失败", "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                    }
                    break;

                case DEVICE_NOT_ONLINE:     //设备不在线
                    if (!RealTimeShow.this.isFinishing()){
                        DialogUtils.showPrompt(RealTimeShow.this, "提示", "设备不在线", "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                    }
                  break;

                case GET_IMAGE_URL_ERROR:       //获取图片URL失败
                    btRefresh.setClickable(true);
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
                    break;

                case GET_IMAGE_SUCCESS:     //获取图片成功
                    progressBar.setVisibility(View.INVISIBLE);
                    btRefresh.setClickable(true);
                    getImageCount = 0;

                    break;

                case GET_IMAGE_FAIL:        //获取图片失败
                    btRefresh.setClickable(true);
                    getImageCount = 0;
                    break;

                case BEFORE_GET_IMAGE_URL:
                    progressBar.setVisibility(View.VISIBLE);
                    btRefresh.setClickable(false);
                    break;
            }

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

        //设置获取设备状态为真，以便在Activity销毁时能重新获取设备状态
        spf.edit().putBoolean("is_keeping_get_device_state",true).apply();

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

        starTimer();
    }

    private void starTimer(){
        mTimer = new Timer();
        //定时获取图片URL
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                getImageUrl(); //显示图片
            }
        },0,1000);

    }

    private void stopTimer(){
        if (mTimer != null){
            mTimer.cancel();
        }
    }

    /**
     * 发送抓图指令，获取图片链接
     */
    private void getImageUrl() {

        mHandler.sendEmptyMessage(BEFORE_GET_IMAGE_URL);
        getImageCount++;

        //配置json数据
        JSONObject json = new JSONObject();
        try {
            json.put("devid",devid);
            json.put(SIGNATURE_TOKEN, SignatureUtils.token);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //计算signature
        String timestamp = UtcTimeUtils.getTimestamp();
        String noncestr = HttpParamsUtils.getRandomString(10);
        String signature = SignatureUtils.getSinnature(timestamp,
                noncestr,
                Urls.APP_KEY,
                devid,
                SignatureUtils.token);
        //发起请求
        RequestParams params = HttpParamsUtils.setParams(Urls.URL_PIC_SNAP,timestamp,noncestr,signature);
        params.setBodyContent(json.toString());

//        params.setConnectTimeout(120*1000); //超时120s
        System.out.println("实时预览" + devid);
        System.out.println("获取图片URL" + params);
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                System.out.println("返回的数组是：" + result);
                RealTimeBean realTimeBean = JsonUtils.deserialize(result,RealTimeBean.class);

                url = realTimeBean.getUrl();
                System.out.println("url的值---" + url);

                errorCode = realTimeBean.getStatus();
                System.out.println("error的值：---" + errorCode);

                switch (errorCode) {
                    case 200: //返回正常
                        if (!url.isEmpty() && !url.equals("")) {
                            System.out.println("url返回值正常");
                            mTimer.cancel();
                            getImageFromInternet();
                        }else{
                            System.out.println("url返回值为空");
                        }
                        break;
                    case 404:
                        ToastShow.showToast(RealTimeShow.this,"设备不存在");
                        mTimer.cancel();
                        break;
                    case 403:
                        ToastShow.showToast(RealTimeShow.this,"token错误或者设备不在该用户名下");
                        mTimer.cancel();
                        break;
                    case 402:
                        System.out.println("设备不在线，无法抓取");
                        mTimer.cancel();
                        mHandler.sendEmptyMessage(DEVICE_NOT_ONLINE);
                        break;
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println("返回json错误" + ex);
                mHandler.sendEmptyMessage(GET_IMAGE_URL_ERROR);
            }

            @Override
            public void onCancelled(CancelledException cex) {}

            @Override
            public void onFinished() {}
        });

        if (getImageCount > 9){
            System.out.println("大于九了");
            getImageCount = 0;
            mTimer.cancel();
            mHandler.sendEmptyMessage(SNAP_FAIL);
        }
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
                mHandler.sendEmptyMessage(GET_IMAGE_SUCCESS);
                int intrinsicWidth = result.getIntrinsicWidth();
                int intrinsicHeight = result.getIntrinsicHeight();
                System.out.println("图片的宽度：" + intrinsicWidth + "，图片的高度：" + intrinsicHeight);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

                System.out.println("加载错误" + ex);
                mHandler.sendEmptyMessage(GET_IMAGE_FAIL);

            }

            @Override
            public void onCancelled(CancelledException cex) {
                System.out.println("加载取消");
            }

            @Override
            public void onFinished() {
//                btRefresh.setClickable(true);
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
                    if ((System.currentTimeMillis()- secondTime) > (2 * 1000)){
//                        photoView.setImageResource(R.mipmap.default_white);
//                        photoView.setBackgroundColor(Color.WHITE);
                        if (mTimer != null){
                            starTimer();
                        }
                    }
                    secondTime = System.currentTimeMillis();
                    break;
                case R.id.bt_downImage:
                    downImage();
                    break;
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
        mTimer = null;
    }

}
