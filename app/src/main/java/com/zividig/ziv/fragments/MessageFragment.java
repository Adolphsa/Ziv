package com.zividig.ziv.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.zividig.ziv.adapter.MessageAdapter;
import com.zividig.ziv.adapter.OnItemClickListener;
import com.zividig.ziv.bean.MessageBean;
import com.zividig.ziv.function.MessageMapShow;
import com.zividig.ziv.main.MainActivity;
import com.zividig.ziv.utils.Urls;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 信息
 *
 */
public class MessageFragment extends Fragment {

    private MainActivity mContext;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private MessageAdapter mMenuAdapter;

    private List<MessageBean.DataBean> mDataBeanList;

    private SwipeMenuRecyclerView mSwipeMenuRecyclerView;

    private View view;
    private SharedPreferences spf;

    public static MessageFragment instance() {
        MessageFragment view = new MessageFragment();
        return view;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message, null);
        mContext = (MainActivity) getActivity();
        spf = mContext.getSharedPreferences("config", Context.MODE_PRIVATE);
        System.out.println("MessageFragment---onCreateView");

        //设置标题
        TextView title = (TextView) view.findViewById(R.id.tv_title);
        title.setText("信息");

        initSwipeRecycleView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initSwipeRecycleView(){
        System.out.println("initSwipeRecycleView");
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.message_swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);

        mSwipeMenuRecyclerView = (SwipeMenuRecyclerView) view.findViewById(R.id.message_recycler_view);
        mSwipeMenuRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));// 布局管理器。
        mSwipeMenuRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        mSwipeMenuRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        mSwipeMenuRecyclerView.addItemDecoration(new ListViewDecoration());// 添加分割线。

        // 添加滚动监听。
//        mSwipeMenuRecyclerView.addOnScrollListener(mOnScrollListener);

        // 设置菜单创建器。
//        mSwipeMenuRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        // 设置菜单Item点击监听。
        mSwipeMenuRecyclerView.setSwipeMenuItemClickListener(menuItemClickListener);
        mMenuAdapter = new MessageAdapter();
        mSwipeMenuRecyclerView.setAdapter(mMenuAdapter);
        mMenuAdapter.setOnItemClickListener(onItemClickListener);
    }

    /**
     * 刷新监听。
     */
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mSwipeMenuRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                    getVibrationAlarmMessage();
                }
            }, 2000);
        }
    };


    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.item_height);

            // MATCH_PARENT 自适应高度，保持和内容一样高；也可以指定菜单具体高度，也可以用WRAP_CONTENT。
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(mContext)
                        .setBackgroundDrawable(R.drawable.selector_red)
                        .setImage(R.mipmap.ic_action_delete)
                        .setText("删除") // 文字，还可以设置文字颜色，大小等。。
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。
            }
        }
    };

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            spf.edit().putBoolean("red_point" + position,false).apply();
            MessageBean.DataBean dataBean = mDataBeanList.get(position);
            Intent intent = new Intent(mContext, MessageMapShow.class);
            intent.putExtra("alarm_message_data",dataBean);
            mContext.startActivity(intent);
        }
    };

    /**
     * 菜单点击监听。
     */
    private OnSwipeMenuItemClickListener menuItemClickListener = new OnSwipeMenuItemClickListener() {
        /**
         * Item的菜单被点击的时候调用。
         * @param closeable       closeable. 用来关闭菜单。
         * @param adapterPosition adapterPosition. 这个菜单所在的item在Adapter中position。
         * @param menuPosition    menuPosition. 这个菜单的position。比如你为某个Item创建了2个MenuItem，那么这个position可能是是 0、1，
         * @param direction       如果是左侧菜单，值是：SwipeMenuRecyclerView#LEFT_DIRECTION，如果是右侧菜单，值是：SwipeMenuRecyclerView#RIGHT_DIRECTION.
         */
        @Override
        public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, int direction) {
            closeable.smoothCloseMenu();// 关闭被点击的菜单。

            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                Toast.makeText(mContext, "消息被删除", Toast.LENGTH_SHORT).show();
            }

            if (menuPosition == 0) {// 删除按钮被点击。
                mDataBeanList.remove(adapterPosition);
                mMenuAdapter.notifyItemRemoved(adapterPosition);
            }
        }
    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        System.out.println("信息可见");
        if (isVisibleToUser){
            getVibrationAlarmMessage();
            System.out.println("MessageFragment可见");
        }
    }

    /**
     * 获取震动报警消息
     */
    private void getVibrationAlarmMessage(){

        String devid = spf.getString("devid","");
        RequestParams params = new RequestParams(Urls.GET_VIBRATION_ALARM);
        params.addBodyParameter("devid", devid);
        System.out.println("获取震动消息" + params.toString());

        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                MessageBean mb = gson.fromJson(result, MessageBean.class);
                int status = mb.getStatus();
                int size = mb.getData().size();
                mDataBeanList = new ArrayList<MessageBean.DataBean>();
                if (200 == status && size > 0){
                    mDataBeanList = mb.getData();
                    System.out.println("震動報警消息---" + mDataBeanList.toString());
                    mMenuAdapter.setDataBeanList(mDataBeanList);
                    mMenuAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {}

            @Override
            public void onCancelled(CancelledException cex) {}

            @Override
            public void onFinished() {}
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}