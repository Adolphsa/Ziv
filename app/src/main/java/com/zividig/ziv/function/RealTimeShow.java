package com.zividig.ziv.function;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.bm.library.PhotoView;
import com.zividig.ziv.R;
import com.zividig.ziv.customView.LoadingProgressDialog;
import com.zividig.ziv.main.BaseActivity;
import com.zividig.ziv.main.ZivApp;
import com.zividig.ziv.rxjava.ZivApiManage;
import com.zividig.ziv.rxjava.model.DeviceStateBody;
import com.zividig.ziv.rxjava.model.DeviceStateResponse;
import com.zividig.ziv.rxjava.model.SnapBody;
import com.zividig.ziv.rxjava.model.SnapResponse;
import com.zividig.ziv.utils.DialogUtils;
import com.zividig.ziv.utils.HttpParamsUtils;
import com.zividig.ziv.utils.JsonUtils;
import com.zividig.ziv.utils.Others;
import com.zividig.ziv.utils.SignatureUtils;
import com.zividig.ziv.utils.ToastShow;
import com.zividig.ziv.utils.Urls;
import com.zividig.ziv.utils.UtcTimeUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.sharesdk.onekeyshare.OnekeyShare;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 实时预览
 * Created by Administrator on 2016-05-30.
 */
public class RealTimeShow extends BaseActivity {

    private static final int SNAP_COUNT = 60;

    private Context mContext;
    private PhotoView photoView;
    private ProgressBar progressBar;

    private Button btRefresh;
    private Button btDownImage;
    private Button btShraeImage;

    private String devid;

    private long secondTime = 0;
    private String imageKey = "new";
    private Map<String, String> mapOptions;
    private SnapBody body;
    private RequestBody jsonBody;
    private Bitmap mBitmap;

    private Subscription mSubscription;
    private int count;

    private SharedPreferences mSpf;
    private Dialog mDialog;

    private String shareImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(ZivApp.getInstance());
        setContentView(R.layout.acticity_real_time_show);

        mContext = RealTimeShow.this;

        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.rts_tv_title);
        txtTitle.setText(R.string.rts_title);

        mSpf = getSharedPreferences("config",MODE_PRIVATE);
        devid = mSpf.getString("devid","");

        //设置获取设备状态为真，以便在Activity销毁时能重新获取设备状态
        mSpf.edit().putBoolean("is_keeping_get_device_state",true).apply();

        //返回按钮
        Button btnBack = (Button) findViewById(R.id.rts_btn_back);
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

        //按钮监听
        BtnListener listener = new BtnListener();

        btRefresh = (Button) findViewById(R.id.bt_refresh); //图片刷新
        btDownImage = (Button) findViewById(R.id.bt_downImage);  //图片下载
        btShraeImage = (Button) findViewById(R.id.rts_share_image); //图片分享

        btRefresh.setOnClickListener(listener);
        btDownImage.setOnClickListener(listener);
        btShraeImage.setOnClickListener(listener);

        RxgetDeviceState();

    }

    /**
     * 配置options
     * @return options
     */
    private Map<String, String> setOp(){

        String token = mSpf.getString("token", null);
        String devid = mSpf.getString("devid", null);

        //计算signature
        String timestamp = UtcTimeUtils.getTimestamp();
        String noncestr = HttpParamsUtils.getRandomString(10);
        String signature = SignatureUtils.getSinnature(timestamp,
                noncestr,
                Urls.APP_KEY,
                devid,
                token);

        //配置请求头
        Map<String, String> options = new HashMap<>();
        options.put(SignatureUtils.SIGNATURE_APP_KEY, Urls.APP_KEY);
        options.put(SignatureUtils.SIGNATURE_TIMESTAMP, timestamp);
        options.put(SignatureUtils.SIGNATURE_NONCESTTR, noncestr);
        options.put(SignatureUtils.SIGNATURE_STRING, signature);

        return options;
    }

    /**
     * 配置jsonBody
     * @return  RequestBody
     */
    private RequestBody setBody(){

        String token = mSpf.getString("token", null);
        String devid = mSpf.getString("devid", null);

        //配置请求体
        DeviceStateBody deviceStateBody = new DeviceStateBody();
        deviceStateBody.devid = devid;
        deviceStateBody.token = token;
        String stringDeviceListBody = JsonUtils.serialize(deviceStateBody);
        RequestBody jsonBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), stringDeviceListBody);

        return jsonBody;
    }

    /**
     * 获取设备状态
     */
    private void RxgetDeviceState(){

        Map<String, String> options = setOp();
        RequestBody jsonBody = setBody();
        ZivApiManage.getInstance().getZivApiService()
                .getDeviceStateInfo(options,jsonBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DeviceStateResponse>() {
                    @Override
                    public void call(DeviceStateResponse deviceStateResponse) {
                        int status = deviceStateResponse.getStatus();
                        DeviceStateResponse.InfoBean infoBean =  deviceStateResponse.getInfo();
                        if (200 == status && infoBean != null){
                            String worlMode = infoBean.getWorkmode();
                            if (worlMode.equals("NORMAL")){
                                //执行获取图片
                                progressBar.setVisibility(View.VISIBLE);
                                if("Meizu".equals(android.os.Build.MANUFACTURER)){
                                    showProgressDialog();
                                }
                                RXgetImageUrl();
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (!RealTimeShow.this.isFinishing()){
                            DialogUtils.showPrompt(RealTimeShow.this, getString(R.string.add_device_tips), getString(R.string.rts_device_offline), getString(R.string.add_device_ensure), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                        }
                    }
                });
    }

    private Map<String, String> setOp(String imageKeyTest){

        String token = mSpf.getString("token", null);
        String devid = mSpf.getString("devid", null);

        //计算signature
        String timestamp = UtcTimeUtils.getTimestamp();
        String noncestr = HttpParamsUtils.getRandomString(10);
        String signature = SignatureUtils.getSinnature(timestamp,
                noncestr,
                Urls.APP_KEY,
                devid,
                token,
                imageKeyTest);

        //配置请求头
        mapOptions = new HashMap<>();
        mapOptions.put(SignatureUtils.SIGNATURE_APP_KEY, Urls.APP_KEY);
        mapOptions.put(SignatureUtils.SIGNATURE_TIMESTAMP, timestamp);
        mapOptions.put(SignatureUtils.SIGNATURE_NONCESTTR, noncestr);
        mapOptions.put(SignatureUtils.SIGNATURE_STRING, signature);

        return mapOptions;
    }

    private RequestBody setBody(String imageKeyTest){

        String token = mSpf.getString("token", null);
        String devid = mSpf.getString("devid", null);

        //配置请求体
        body = new SnapBody();
        body.devid = devid;
        body.imageKey = imageKeyTest;
        body.token = token;
        String snapBody = JsonUtils.serialize(body);
        jsonBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), snapBody);

        return jsonBody;
    }


    /**
     * 获取图片链接
     */
    private void RXgetImageUrl() {

        count = 0;
        String token = mSpf.getString("token", null);
        String devid = mSpf.getString("devid", null);

        //计算signature
        String timestamp = UtcTimeUtils.getTimestamp();
        String noncestr = HttpParamsUtils.getRandomString(10);
        String signature = SignatureUtils.getSinnature(timestamp,
                noncestr,
                Urls.APP_KEY,
                devid,
                token,
                imageKey);

        //配置请求头
        mapOptions = new HashMap<>();
        mapOptions.put(SignatureUtils.SIGNATURE_APP_KEY, Urls.APP_KEY);
        mapOptions.put(SignatureUtils.SIGNATURE_TIMESTAMP, timestamp);
        mapOptions.put(SignatureUtils.SIGNATURE_NONCESTTR, noncestr);
        mapOptions.put(SignatureUtils.SIGNATURE_STRING, signature);

        //配置请求体
        body = new SnapBody();
        body.devid = devid;
        body.imageKey = imageKey;
        body.token = token;
        String snapBody = JsonUtils.serialize(body);
        jsonBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), snapBody);

        System.out.println("访问之前的imageKey---" + imageKey);

        mSubscription =Observable.interval(1, TimeUnit.SECONDS)
                .take(SNAP_COUNT)
                .flatMap(new Func1<Long, Observable<SnapResponse>>() {
                    @Override
                    public Observable<SnapResponse> call(Long aLong) {
                        System.out.println("第一次变换imagekey---" + imageKey);
                        return ZivApiManage.getInstance().getZivApiService().getImageUrl(mapOptions,jsonBody);
                    }
                })
                .flatMap(new Func1<SnapResponse, Observable<SnapResponse>>() {
                    @Override
                    public Observable<SnapResponse> call(SnapResponse snapResponse) {
                        System.out.println("第二次变换imagekey---" + snapResponse.getKey());
                        Map<String, String> op = setOp(snapResponse.getKey());
                        RequestBody js = setBody(snapResponse.getKey());
                        return ZivApiManage.getInstance().getZivApiService().getImageUrl(op,js);
                    }
                })
                .takeUntil(new Func1<SnapResponse, Boolean>() {
                    @Override
                    public Boolean call(SnapResponse snapResponse) {
                        count++;
                        System.out.println("count---" + count);
                        String url = snapResponse.getUrl();
                        if (!TextUtils.isEmpty(url)){
                            System.out.println("停止轮询");
                            shareImageUrl = url;
                            return true;
                        }
                        return false;
                    }
                })
                .filter(new Func1<SnapResponse, Boolean>() {
                    @Override
                    public Boolean call(SnapResponse snapResponse) {
                        String url = snapResponse.getUrl();
                        imageKey = snapResponse.getKey();
                        System.out.println("过滤---" + imageKey);
                        if (!TextUtils.isEmpty(url)){
                            System.out.println("url不为空---" + url);
                            return true;
                        }
                        return false;
                    }
                })
                .flatMap(new Func1<SnapResponse, Observable<ResponseBody>>() {
                    @Override
                    public Observable<ResponseBody> call(SnapResponse snapResponse) {
                        return ZivApiManage.getInstance().getZivApiService().downLoadImage(snapResponse.getUrl());
                    }
                })
                .map(new Func1<ResponseBody, Bitmap>() {
                    @Override
                    public Bitmap call(ResponseBody responseBody) {
                        InputStream is = null;
                        try {
                            is = responseBody.byteStream();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (is != null) {
                                    is.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (is != null) {
                            return BitmapFactory.decodeStream(is);
                        }
                        return null;

                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        mBitmap = bitmap;
                        System.out.println("图片宽度---" + bitmap.getWidth() + "图片高度---" + bitmap.getHeight());
                        photoView.setImageBitmap(bitmap);
                        progressBar.setVisibility(View.INVISIBLE);
                        closeDialog();

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        progressBar.setVisibility(View.INVISIBLE);
                        closeDialog();
                        if (!RealTimeShow.this.isFinishing()) {
                            DialogUtils.showPrompt(RealTimeShow.this, getString(R.string.add_device_tips), getString(R.string.rts_snap_fail), getString(R.string.add_device_ensure), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                        }
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        System.out.println("完成了");
                        if (count > SNAP_COUNT-1){
                            progressBar.setVisibility(View.INVISIBLE);
                            closeDialog();
                            if (!RealTimeShow.this.isFinishing()) {
                                DialogUtils.showPrompt(RealTimeShow.this, getString(R.string.add_device_tips), getString(R.string.rts_snap_fail), getString(R.string.add_device_ensure), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                            }
                        }
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
                Toast.makeText(RealTimeShow.this, R.string.rts_no_storage, Toast.LENGTH_SHORT).show();
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

            if (mBitmap != null){
                Others.saveImageToGallery(mContext,mBitmap,file,target);
                ToastShow.showToast(RealTimeShow.this,getString(R.string.rts_image_has_saved));
                updateImage();
            }
        } else {
            ToastShow.showToast(RealTimeShow.this,getString(R.string.rts_refresh_image));
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

    /**
     * 显示进度条
     */
    private void showProgressDialog(){
        if (mDialog == null && !RealTimeShow.this.isFinishing()){
            mDialog = LoadingProgressDialog.createLoadingDialog(RealTimeShow.this,getString(R.string.rts_snaping),true,false,null);
            mDialog.show();
        }
    }

    /**
     * 关闭进度条
     */
    private void closeDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    class BtnListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_refresh:
                    if ((System.currentTimeMillis()- secondTime) > (1 * 1000)){
                        imageKey = "new";
                        RxgetDeviceState();
                    }
                    secondTime = System.currentTimeMillis();
                    break;
                case R.id.bt_downImage:
                    downImage();
                    break;
                case R.id.rts_share_image:
                    shareImage();
                    break;
            }
        }
    }


    private void shareImage(){
        if (TextUtils.isEmpty(shareImageUrl)){
            ToastShow.showToast(this,"图片不存在，无法分享");
        }else {
            System.out.println("分享链接---" + shareImageUrl);
            OnekeyShare oks = new OnekeyShare();
            //关闭sso授权
            oks.disableSSOWhenAuthorize();
            oks.setImageUrl(shareImageUrl);//网络图片rul
            // 启动分享GUI
            oks.show(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null){
            mSubscription.unsubscribe();
        }
    }
}
