package com.zividig.ziv.function;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.zividig.ziv.R;
import com.zividig.ziv.adapter.ListViewDecoration;
import com.zividig.ziv.adapter.MenuAdapter;
import com.zividig.ziv.adapter.OnItemClickListener;
import com.zividig.ziv.bean.DeviceInfoBean;
import com.zividig.ziv.main.BaseActivity;
import com.zividig.ziv.main.Login;
import com.zividig.ziv.utils.DialogUtils;
import com.zividig.ziv.utils.HttpParamsUtils;
import com.zividig.ziv.utils.SignatureUtils;
import com.zividig.ziv.utils.ToastShow;
import com.zividig.ziv.utils.Urls;
import com.zividig.ziv.utils.UtcTimeUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import static com.zividig.ziv.utils.SignatureUtils.SIGNATURE_TOKEN;

public class MyDevice extends BaseActivity {

    private DeviceInfoBean deviceInfoBean;
    private List<DeviceInfoBean.DevinfoBean> devinfoList;
    private DeviceInfoBean.DevinfoBean devinfoBean;
    private MenuAdapter mMenuAdapter;
    private SwipeMenuRecyclerView swipeMenuRecyclerView;

    private SharedPreferences spf;
    private String devid;
    private Login mLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_device);

        spf = getSharedPreferences("config",MODE_PRIVATE);
        devid = spf.getString("devid","");
        mLogin = new Login();

        devinfoList = new ArrayList<>();

        getDeviceList();

        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.tv_title);
        txtTitle.setText("我的设备");

        //返回按钮
        Button btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        swipeMenuRecyclerView = (SwipeMenuRecyclerView) findViewById(R.id.my_device_recycler_view);
        swipeMenuRecyclerView.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。
        swipeMenuRecyclerView.setHasFixedSize(false);
        swipeMenuRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        swipeMenuRecyclerView.addItemDecoration(new ListViewDecoration());// 添加分割线。

        // 设置菜单创建器。
        swipeMenuRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        // 设置菜单Item点击监听。
        swipeMenuRecyclerView.setSwipeMenuItemClickListener(menuItemClickListener);

        mMenuAdapter = new MenuAdapter(spf);
        mMenuAdapter.setDevinfoList(devinfoList);
        mMenuAdapter.setOnItemClickListener(onItemClickListener);
        swipeMenuRecyclerView.setAdapter(mMenuAdapter);
    }

    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.item_width);
            int height = getResources().getDimensionPixelSize(R.dimen.item_height);
            //解绑菜单
            SwipeMenuItem deleteItem = new SwipeMenuItem(MyDevice.this)
                    .setBackgroundDrawable(R.drawable.selector_red)
                    .setImage(R.mipmap.ic_action_close) // 图标。
                    .setText("解绑设备") // 文字。
                    .setTextColor(Color.WHITE) // 文字颜色。
                    .setTextSize(15) // 文字大小。
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。

            //设置车牌号和别名菜单
            SwipeMenuItem caridAndAliasItem = new SwipeMenuItem(MyDevice.this)
                    .setBackgroundDrawable(R.drawable.selector_blue)
                    .setImage(R.mipmap.ic_set_carid) // 图标。
                    .setText("设置车牌号") // 文字。
                    .setTextColor(Color.WHITE) // 文字颜色。
                    .setTextSize(15) // 文字大小。
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(caridAndAliasItem);//添加第二个右侧按钮
        }
    };

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            devinfoBean = devinfoList.get(position);
            System.out.println("devinfoBean---" + devinfoBean.toString());
            spf.edit().putString("devid",devinfoBean.getDevid()).apply();
            devid = spf.getString("devid","");
            ToastShow.showToast(MyDevice.this,"已切换");
            swipeMenuRecyclerView.setAdapter(mMenuAdapter);
        }
    };

    /**
     * 菜单点击监听。
     */
    private OnSwipeMenuItemClickListener menuItemClickListener = new OnSwipeMenuItemClickListener() {
        @Override
        public void onItemClick(final Closeable closeable, final int adapterPosition, int menuPosition, int direction) {

            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                System.out.println("adapterPosition---" + adapterPosition);
                //配置请求参数
                String user = spf.getString(Login.ET_USER,"");
                devinfoBean = devinfoList.get(adapterPosition);
                String devid = devinfoBean.getDevid();
                if (menuPosition == 0){     //解绑设备
                    System.out.println("解绑设备devid---" + devid);
                    //解绑设备
                    unBindDevice(user,devid,adapterPosition);
                }else if (menuPosition == 1){       //设置车牌号
                    System.out.println("设置devid---" + devid);
                    showFormDialog(user,devid);
                    closeable.smoothCloseMenu();
                }
            }
        }
    };

    /**
     * 解绑设备
     * @param username  用户名
     * @param devid     设备ID
     * @param adapterPosition   点击的行数
     */
    private void unBindDevice(final String username, String devid, final int adapterPosition){
        //配置json数据
        final JSONObject json = new JSONObject();
        try {
            json.put("username",username);
            json.put("devid", devid);
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
                username,
                devid,
                SignatureUtils.token);

        RequestParams params = HttpParamsUtils.setParams(Urls.UNBIND_DEVICE,timestamp,noncestr,signature);
        params.setBodyContent(json.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println("解绑设备---" + result);
                try {
                    JSONObject json = new JSONObject(result);
                    int status = json.getInt("status");
                    if (200 == status){
                        ToastShow.showToast(MyDevice.this,"解绑成功");
                        //删除devid
                        spf.edit().remove("devid").apply();

                        //删除列表项
                        devinfoList.remove(adapterPosition);
                        mMenuAdapter.notifyItemRemoved(adapterPosition);

                        //重新获取设备信息列表
                        mLogin.getDeviceInfo(username,spf);

                    }else if (400 == status){
                        ToastShow.showToast(MyDevice.this,"解绑失败，用户不存在或者设备不存在");
                    }else {
                        ToastShow.showToast(MyDevice.this,"解绑失败，用户不存在或者设备不存在");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastShow.showToast(MyDevice.this,"解绑失败，json解析或网络异常");
            }

            @Override
            public void onCancelled(CancelledException cex) {}

            @Override
            public void onFinished() {}
        });
    }

    /**
     * 弹出设置车牌号的表单对话框
     * @param usrname   用户名
     * @param devid     设备Id
     */
    private void showFormDialog(final String usrname, final String devid){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("设置车牌号和别名(可选)");//设置标题
        View view = LayoutInflater.from(this).inflate(R.layout.layout_form_dialog,null);
        TextView tvTips = (TextView) view.findViewById(R.id.et_tips);
        tvTips.setVisibility(View.GONE);    //设置小标题隐藏
        final EditText etCarid = (EditText) view.findViewById(R.id.et_carid);//车牌号
        final EditText etAlias = (EditText) view.findViewById(R.id.et_alias);//别名
        builder.setView(view);//给对话框设置布局
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //点击确定按钮的操作
                System.out.println("确定");
                String carid = etCarid.getText().toString().trim();
                if (carid == null){
                    carid = "";
                }
                System.out.println("carid---" + carid);
                String alias = etAlias.getText().toString().trim();
                if (alias == null){
                    alias = "";
                }
                System.out.println("alias---" + alias);
                //设置车牌号
                setCarId(usrname,devid,carid,alias);

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.out.println("设置车牌号和别名取消");
            }
        });
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("MyDevice ---onResume");
    }

    /**
     * 设置车牌号和别名
     * @param username  用户名
     * @param devid     设备ID
     * @param carid     车牌号
     * @param alias     别名
     */
    private void setCarId(final String username, final String devid, String carid, String alias){
        System.out.println("setCarId");
        //配置json数据
        final JSONObject json = new JSONObject();
        try {
            json.put("username",username);
            json.put("devid", devid);
            json.put("carid",carid);
            json.put("alias",alias);
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
                username,
                devid,
                carid,
                alias,
                SignatureUtils.token);

        RequestParams params = HttpParamsUtils.setParams(Urls.SETTING_CARID,timestamp,noncestr,signature);
        params.setBodyContent(json.toString());
        System.out.println("params---" + params.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    int status = json.getInt("status");
                    if (200 == status){
                        if (!MyDevice.this.isFinishing()){
                            DialogUtils.showPrompt(MyDevice.this, "提示", "设置车牌号和别名成功", "确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //获取设备列表信息
                                    getDeviceInfo(username);
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println("设置车牌号错误");
                if (!MyDevice.this.isFinishing()) {
                    DialogUtils.showPrompt(MyDevice.this, "提示", "设置车牌号失败", "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {}

            @Override
            public void onFinished() {}
        });
    }

    /**
     * 获取设备信息
     */
    public void getDeviceInfo(String user){
        System.out.println("执行获取设备信息");

        //配置json数据
        JSONObject json = new JSONObject();
        try {
            json.put("username",user);
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
                user,
                SignatureUtils.token);

        RequestParams params = HttpParamsUtils.setParams(Urls.GET_DEVICE_LIST,timestamp,noncestr,signature);
        params.setBodyContent(json.toString());

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                System.out.println("设备信息---" + result);
                Gson gson = new Gson();
                deviceInfoBean =  gson.fromJson(result, DeviceInfoBean.class);
                int status = deviceInfoBean.getStatus();

                if (status == Urls.STATUS_CODE_200){
                    devinfoList = deviceInfoBean.getDevinfo(); //设备列表
                    //保存设备列表信息
                    spf.edit().putString("device_info",result).apply();
                    mMenuAdapter.setDevinfoList(devinfoList);
                    mMenuAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println("错误" + ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {}

            @Override
            public void onFinished() {}
        });

    }

    /**
     * 获取设备ID列表
     */
    private List<DeviceInfoBean.DevinfoBean> getDeviceList(){
        String deviceInfo = spf.getString("device_info","");
        System.out.println("我的设备中的" + deviceInfo);
        if (!deviceInfo.equals("")){
            Gson gson = new Gson();
            DeviceInfoBean deviceInfoBean =  gson.fromJson(deviceInfo, DeviceInfoBean.class);
            devinfoList = deviceInfoBean.getDevinfo();
            return devinfoList;
        }else {
            ToastShow.showToast(MyDevice.this,"出错啦");
            return null;
        }
    }
}
