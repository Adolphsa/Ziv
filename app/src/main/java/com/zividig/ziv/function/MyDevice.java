package com.zividig.ziv.function;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

public class MyDevice extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_device);

        List<String> mStrings = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            mStrings.add("我是第" + i + "个。");
            System.out.println(i);
        }

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

        SwipeMenuRecyclerView swipeMenuRecyclerView = (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);
        swipeMenuRecyclerView.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。
        swipeMenuRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        swipeMenuRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        swipeMenuRecyclerView.addItemDecoration(new ListViewDecoration());// 添加分割线。

        // 设置菜单创建器。
        swipeMenuRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        // 设置菜单Item点击监听。
        swipeMenuRecyclerView.setSwipeMenuItemClickListener(menuItemClickListener);

        MenuAdapter mMenuAdapter = new MenuAdapter(mStrings);
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
                    .setImage(R.mipmap.ic_action_delete) // 图标。
                    .setText("删除") // 文字。
                    .setTextColor(Color.WHITE) // 文字颜色。
                    .setTextSize(16) // 文字大小。
                    .setWidth(size)
                    .setHeight(size);
            swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。
        }
    };

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            Toast.makeText(MyDevice.this, "我目前是第" + position + "条。", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 菜单点击监听。
     */
    private OnSwipeMenuItemClickListener menuItemClickListener = new OnSwipeMenuItemClickListener() {
        @Override
        public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, int direction) {
            closeable.smoothCloseMenu();// 关闭被点击的菜单。

            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                Toast.makeText(MyDevice.this, "list第" + adapterPosition + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
                Toast.makeText(MyDevice.this, "list第" + adapterPosition + "; 左侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            }
        }
    };
}
