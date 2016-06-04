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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zividig.ziv.R;
import com.zividig.ziv.customView.NoScrollGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyPicture extends Activity{


    private ArrayList<String> names;
    private ArrayList<String> fileNames;
    private ListView lvPivture;

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

        names = new ArrayList<String>();
        fileNames = new ArrayList<String>();

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
        //定义selectionArgs：
        String[] selectionArgs = {path+"%"};
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                    null, selection,selectionArgs,null);
        while (cursor.moveToNext()){

            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            System.out.println("name:" + name);
            System.out.println(name.substring(0,4) + "年" + name.substring(4,6) + "月" + name.substring(6,8));
            String year = name.substring(0,4);
            String month = name.substring(4,6);
            String day = name.substring(6,8);
            String dateName = name.substring(0,4) + "年" + name.substring(4,6) + "月" + name.substring(6,8);

            names.add(dateName);
            byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            fileNames.add(new String(data,0,data.length-1));
            System.out.println("fileNames的长度" + fileNames.size());
        }

        List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
        for (int i = 0; i < names.size(); i++) {
            Map<String,Object> listItem = new HashMap<String,Object>();
            listItem.put("name",names.get(i));
            listItem.put("fileName",fileNames.get(i));
            listItems.add(listItem);
        }

        System.out.println("listMap的长度：" + listItems.size());
        List<ArrayList<String>> lists = new ArrayList<ArrayList<String>>();
        ArrayList<String> s1 = new ArrayList<String>();
        for (int i = 0; i < listItems.size(); i++) {
            for (int j = i+1; j<listItems.size(); j++){
                String a =(String) listItems.get(i).get("name");
                String a8 = a.substring(0, 8);

                String b = (String) listItems.get(j).get("name");
                String b8 = b.substring(0, 8);
                if (a8.equals(b8)){
                    System.out.println("相等");
                    s1.add(listItems.get(j).get("fileName").toString());

                }else {
                    System.out.println("不相等");
                    ArrayList<String> s2 = new ArrayList<String>();
                    s2.add(listItems.get(j).get("fileName").toString());

                }
            }
        }
        lists.add(s1);
        System.out.println("整理后的数组长度:---" + lists.size());

    }

    class MyListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 2;
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
            ViewHolder holder;
            if (convertView == null){
                convertView = View.inflate(MyPicture.this,R.layout.layout_picture_list_item,null);
                holder = new ViewHolder();
                holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                holder.gvPicture = (NoScrollGridView) convertView.findViewById(R.id.gv_picture);
                holder.gvPicture.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        System.out.println("被点击了" + position);
                        Intent intent = new Intent(MyPicture.this,ShowPicture.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("pic_url",fileNames.get(position));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvName.setText(names.get(0));
            holder.gvPicture.setAdapter(new BaseAdapter() {
                GridViewHolder gvHolder;
                @Override
                public int getCount() {
                    return fileNames.size();
                }

                @Override
                public Object getItem(int position) {
                    return fileNames.get(position);
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
                    gvHolder.gvImage.setImageBitmap(BitmapFactory.decodeFile(fileNames.get(position)));
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
