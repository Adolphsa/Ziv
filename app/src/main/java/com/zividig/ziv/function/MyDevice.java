package com.zividig.ziv.function;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

        swipeMenuRecyclerView = (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);
        swipeMenuRecyclerView.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。
        swipeMenuRecyclerView.setHasFixedSize(false);
        swipeMenuRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        swipeMenuRecyclerView.addItemDecoration(new ListViewDecoration());// 添加分割线。

        // 设置菜单创建器。
        swipeMenuRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        // 设置菜单Item点击监听。
        swipeMenuRecyclerView.setSwipeMenuItemClickListener(menuItemClickListener);

        mMenuAdapter = new MenuAdapter(devinfoList,spf);
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
                    .setImage(R.mipmap.ic_action_close) // 图标。
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
                 devinfoBean = devinfoList.get(adapterPosition);
                //配置请求参数
                String user = spf.getString(Login.ET_USER,"");
                String devid = devinfoBean.getDevid();
                System.out.println("devid---" + devid);
                //解绑设备
                unBindDevice(user,devid,adapterPosition);


            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
                Toast.makeText(MyDevice.this, "list第" + adapterPosition + "; 左侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            }
        }
    };

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
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 获取设备ID列表
     */
    private void getDeviceList(){
        String deviceInfo = spf.getString("device_info","");
        System.out.println("我的设备中的" + deviceInfo);
        if (!deviceInfo.equals("")){
            Gson gson = new Gson();
            DeviceInfoBean deviceInfoBean =  gson.fromJson(deviceInfo, DeviceInfoBean.class);
            devinfoList = deviceInfoBean.getDevinfo();
        }else {
            ToastShow.showToast(MyDevice.this,"出错啦");
        }

    }
}
