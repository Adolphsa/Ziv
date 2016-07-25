package com.zividig.ziv.main;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.zividig.ziv.R;

public class Register extends Activity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
    }

    private void initView(){
        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.tv_title);
        txtTitle.setText("注册");

        //返回按钮
        Button btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listView = (ListView) findViewById(R.id.register_lv);
        RegisterListViewAdapter adapter = new RegisterListViewAdapter();
        listView.setAdapter(adapter);
    }

    class RegisterListViewAdapter extends BaseAdapter{

        ViewHolder holder;

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = View.inflate(Register.this,R.layout.layout_register_item,null);
                holder = new ViewHolder();
                holder.itemTv = (TextView) convertView.findViewById(R.id.register_item_tv);
                holder.itemEt = (EditText) convertView.findViewById(R.id.register_item_et);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            switch (position){
                case 0:
                    holder.itemTv.setText("用户名        ");
                    holder.itemEt.setHint("请输入手机号码");
                    holder.itemEt.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
                case 1:
                    holder.itemTv.setText("密码            ");
                    holder.itemEt.setHint("请输入密码");
                    break;
                case 2:
                    holder.itemTv.setText("确定密码    ");
                    holder.itemEt.setHint("请再次确认密码");
                    break;
                case 3:
                    holder.itemTv.setText("验证码");
                    holder.itemEt.setHint("请输入验证码");
                    holder.itemEt.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
            }
            return convertView;
        }
    }

    static class ViewHolder{
        TextView itemTv;
        EditText itemEt;
    }
}
