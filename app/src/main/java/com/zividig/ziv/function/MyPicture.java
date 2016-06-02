package com.zividig.ziv.function;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.zividig.ziv.R;

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

        lvPivture = (ListView) findViewById(R.id.lv_picture);
        names = new ArrayList<String>();
        fileNames = new ArrayList<String>();

        getImage();
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
            byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            names.add(name);
            fileNames.add(new String(data,0,data.length-1));
        }

        List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
        for (int i = 0; i < names.size(); i++) {
            Map<String,Object> listItem = new HashMap<String,Object>();
            listItem.put("name",names.get(i));
            listItems.add(listItem);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(MyPicture.this,listItems,R.layout.line,
                new String[]{"name"},new int[]{R.id.name});

        lvPivture.setAdapter(simpleAdapter);

    }
}
