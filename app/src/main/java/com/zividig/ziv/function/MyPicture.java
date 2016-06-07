package com.zividig.ziv.function;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zividig.ziv.R;
import com.zividig.ziv.bean.PictureBean;
import com.zividig.ziv.customView.NoScrollGridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyPicture extends Activity{

    private ListView lvPivture;
    private PictureBean bean;
    private int n;
    private List<List<PictureBean>> sortList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_picture);

        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.tv_title);
        txtTitle.setText("我的图片");

        //返回按钮
        Button btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lvPivture = (ListView) findViewById(R.id.lv_picture);
        MyListAdapter listAdapter = new MyListAdapter();
        getImage();

        lvPivture.setAdapter(listAdapter);

    }

    //获取Ziv文件夹中的图片
    private void getImage(){
        //selection: 指定查询条件
        String selection = MediaStore.Images.Media.DATA + " like ?";
        System.out.println(selection);
        //设定查询目录
        String path= Environment.getExternalStorageDirectory() + "/Ziv";
        System.out.println("图片的地址:" + path);
        List<PictureBean> beans = new ArrayList<PictureBean>();
        //定义selectionArgs：
        String[] selectionArgs = {path+"%"};
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                    null, selection,selectionArgs,null);
        while (cursor.moveToNext()){

            //获取图片名称
            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            System.out.println("name:" + name);System.out.println(name.substring(0,4) + "年" + name.substring(4,6) + "月" + name.substring(6,8));
            bean = new PictureBean();
            bean.setPicNum(Integer.valueOf(name.substring(0,8)));

           //获取图片地址
            byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            bean.setPicUrl(new String(data,0,data.length-1));

            beans.add(bean);

        }

        //排序
        Collections.sort(beans);
        for (PictureBean p:beans) {
            System.out.println("排序后的序列" + p.getPicNum());
        }

        n = 0;
        List<PictureBean> temp = null;
        List<PictureBean> temp2 = new ArrayList<>();
        //存在整理好的图片地址
        sortList = new ArrayList<List<PictureBean>>();
        for (int i = n; n < beans.size(); n++) {
            System.out.println("n的值---" + n + "，i的值---" + i);
            for (int j = i+1; j<beans.size();j++){
                if (!beans.get(i).getPicNum().equals(beans.get(j).getPicNum())){ //判断到不相等为止
                    System.out.println("不相等");
                    temp = new ArrayList<>();
                    for (int k = i; k<j;k++){
                        temp.add(beans.get(k));
                    }
                    n = j;
                }else {
                     //全部相等
                    if (j==beans.size()-1){
                        System.out.println("全部相等" + "--j的值" + j);
                        for (int k=0;k<=j;k++){
                            temp2.add(beans.get(k));
                        }
                    }

                }
            }
            if (temp!=null){
                System.out.println("增加temp");
                sortList.add(temp);
            }else {
                System.out.println("增加temp2");
                sortList.add(temp2);
                n=beans.size();
            }

        }

        System.out.println("整理后的序列sortList长度---" + sortList.size());
    }

    class MyListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return sortList.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null){
                convertView = View.inflate(MyPicture.this,R.layout.layout_picture_list_item,null);
                holder = new ViewHolder();
                holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                holder.gvPicture = (NoScrollGridView) convertView.findViewById(R.id.gv_picture);

                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            final List<PictureBean> pictureBeans = sortList.get(position);
            String data = pictureBeans.get(0).getPicNum().toString();
            String data2 = data.substring(0,4) + "年" + data.substring(4,6) + "月" + data.substring(6,8) + "日";
            holder.tvName.setText(data2); //设置是哪一天的照片
            //图片的点击事件
            holder.gvPicture.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    System.out.println("被点击了" + position);
                    Intent intent = new Intent(MyPicture.this,ShowPicture.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("pic_url",pictureBeans.get(position).getPicUrl());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            holder.gvPicture.setAdapter(new BaseAdapter() { //设置具体的照片
                GridViewHolder gvHolder;
                @Override
                public int getCount() {
                    return pictureBeans.size();
                }

                @Override
                public Object getItem(int position) {
                    return pictureBeans.get(position);
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                   if (convertView == null){
                       convertView = View.inflate(MyPicture.this,R.layout.layout_picture_gridview,null);
                       gvHolder = new GridViewHolder();
                       gvHolder.gvImage = (ImageView) convertView.findViewById(R.id.iv_grid_view_image);
                       convertView.setTag(gvHolder);
                   }else {
                       gvHolder = (GridViewHolder) convertView.getTag();
                   }
                    gvHolder.gvImage.setScaleType(ImageView.ScaleType.FIT_XY);
                    gvHolder.gvImage.setImageBitmap(BitmapFactory.decodeFile(pictureBeans.get(position).getPicUrl()));
                    return convertView;
                }
            });
            return convertView;
        }
    }

    static class ViewHolder{
        TextView tvName;
        NoScrollGridView gvPicture;
    }

    static class GridViewHolder{
        ImageView gvImage;
    }
}
