package com.zividig.ziv.customView;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zividig.ziv.R;

/**
 * Created by adolph
 * on 2016-11-02.
 */

public class LoadingProgressDialog {

    public static Dialog createLoadingDialog(Context context, String msg, boolean progress, boolean button, final boolean test){
        // 首先得到整个View
        View view = LayoutInflater.from(context).inflate(
                R.layout.layout_progress_dialog, null);
        // 获取整个布局
        final LinearLayout layout = (LinearLayout) view
                .findViewById(R.id.dialog_view);

        // 页面中的Img
        ImageView img = (ImageView) view.findViewById(R.id.pd_img);
        if (progress){
            img.setVisibility(View.VISIBLE);
        }else {
            img.setVisibility(View.GONE);
        }

        // 页面中显示文本
        TextView tipText = (TextView) view.findViewById(R.id.pd_text);

        //页面中显示按钮
        Button btn = (Button) view.findViewById(R.id.pd_btn);
        if (button){
            btn.setVisibility(View.VISIBLE);
        }else {
            btn.setVisibility(View.GONE);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("被点击");
                if (test){
                    layout.setVisibility(View.VISIBLE);
                }else {
                    layout.setVisibility(View.GONE);
                }
            }
        });

        // 加载动画，动画用户使img图片不停的旋转
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.dialog_load_animation);
        // 显示动画
        img.startAnimation(animation);
        // 显示文本
        tipText.setText(msg);

        // 创建自定义样式的Dialog
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);
        // 设置返回键无效
        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        return loadingDialog;

    }
}
