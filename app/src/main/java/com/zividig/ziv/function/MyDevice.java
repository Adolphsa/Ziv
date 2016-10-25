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
import com.zividig.ziv.utils.ToastShow;
import com.zividig.ziv.utils.Urls;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class MyDevice extends BaseActivity {

    private static List<DeviceInfoBean.DevinfoBean> devinfoList;
    private List<String> mStrings;
    private static MenuAdapter mMenuAdapter;
    private SwipeMenuRecyclerView swipeMenuRecyclerView;
    private SharedPreferences spf;
    private String devid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_device);

        spf = getSharedPreferences("config",MODE_PRIVATE);
        devid = spf.getString("devid","");

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

        mMenuAdapter = new MenuAdapter(mStrings,spf);
        mMenuAdapter.setOnItemClickListener(onItemClickListener);
        swipeMenuRecyclerView.setAdapter(mMenuAdapter);
    }

    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int size = getResources().getDimensionPixelSize(R.dimen.item_height);

            SwipeMenuItem deleteItem = new SwipeMenuItem(MyDevice.this)
                    .setBackgroundDrawable(R.drawable.selector_red)
                    .setImage(R.mipmap.ic_action_close) // 图标。
                    .setText("解绑设备") // 文字。
                    .setTextColor(Color.WHITE) // 文字颜色。
                    .setTextSize(15) // 文字大小。
                    .setWidth(size)
                    .setHeight(size);
            swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。
        }
    };

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            System.out.println(mStrings.get(position));
//            Login.setDevid(mStrings.get(position));
            spf.edit().putString("devid",mStrings.get(position)).apply();
            devid = spf.getString("devid","");
//            mMenuAdapter = new MenuAdapter(mStrings,devid);
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

                //配置请求参数
                String user = spf.getString(Login.ET_USER,"");
                String devid = mStrings.get(adapterPosition);
                System.out.println("devid---" + devid);
                RequestParams params = new RequestParams(Urls.URL_BIND_DEVICE + user + "/" + devid);
                System.out.println(params.toString());
                //请求
                x.http().request(HttpMethod.DELETE, params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        System.out.println("解绑设备---" + result);
                        try {
                            JSONObject json = new JSONObject(result);
                            String status = json.getString("status");
                            if (status.equals("ok")){
                                ToastShow.showToast(MyDevice.this,"解绑成功");
//                                mMenuAdapter.notifyItemRemoved(adapterPosition);
//                                swipeMenuRecyclerView.setAdapter(mMenuAdapter);
                                mStrings.remove(adapterPosition);
                                mMenuAdapter.notifyItemRemoved(adapterPosition);

                            }else {
                                ToastShow.showToast(MyDevice.this,"解绑失败");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        System.out.println("解绑失败" + ex);
                        ToastShow.showToast(MyDevice.this,"解绑失败");
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {
                        closeable.smoothCloseMenu();// 关闭被点击的菜单。
                    }
                });

            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
                Toast.makeText(MyDevice.this, "list第" + adapterPosition + "; 左侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * 获取设备ID列表
     */
    private void getDeviceList(){
        mStrings = new ArrayList<>();
        String deviceInfo = spf.getString("device_info","");
        System.out.println("我的设备中的" + deviceInfo);
        if (!deviceInfo.equals("")){
            Gson gson = new Gson();
            DeviceInfoBean deviceInfoBean =  gson.fromJson(deviceInfo, DeviceInfoBean.class);
            devinfoList = deviceInfoBean.getDevinfo();

            for (DeviceInfoBean.DevinfoBean devinfoBean: devinfoList){
                mStrings.add(devinfoBean.getDevid());
                System.out.println("列表--" + devinfoBean.getDevid());
            }
        }else {
            ToastShow.showToast(MyDevice.this,"出错啦");
        }

    }
}
