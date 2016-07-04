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

public class FindPassWord extends Activity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pass_word);

        initView();
    }

    private void initView(){
        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.tv_title);
        txtTitle.setText("重置密码");

        //返回按钮
        Button btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listView = (ListView) findViewById(R.id.finPwd_lv);
        FindPwdListViewAdapter adapter = new FindPwdListViewAdapter();
        listView.setAdapter(adapter);

    }

    class FindPwdListViewAdapter extends BaseAdapter {

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
                convertView = View.inflate(FindPassWord.this,R.layout.layout_register_item,null);
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
                    break;
                case 1:
                    holder.itemTv.setText("设备ID号    ");
                    holder.itemEt.setHint("请输入设备ID号");
                    holder.itemEt.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
                case 2:
                    holder.itemTv.setText("密码            ");
                    holder.itemEt.setHint("请输入新密码");
                    break;
                case 3:
                    holder.itemTv.setText("确定密码    ");
                    holder.itemEt.setHint("请再次确认新密码");
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
