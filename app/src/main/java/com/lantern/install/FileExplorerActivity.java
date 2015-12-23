package com.lantern.install;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 构造文件浏览模块
 * @Author Zhenpeng Chen
 * @Date 2015/12/23
 * @EMail linuxkey007@gmail.com
 *
 * 说明:本程序参考郭霖大神的博客:http://blog.csdn.net/guolin_blog/article/details/47803149
 */
public class FileExplorerActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private ListView listView;
    private SimpleAdapter simpleAdapter;
    private String rootPath;
    private String currentPath;
    private List<Map<String,Object>> list;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_explorer);
        init();
    }

    private void init(){
        rootPath= Environment.getExternalStorageDirectory().getPath();
        currentPath=rootPath;
        list=new ArrayList<>();
        listView=(ListView)findViewById(R.id.list_view);
        simpleAdapter=new SimpleAdapter(this,list,R.layout.list_item,new String[]{"name","img"},new int[]{R.id.name,R.id.img});
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(this);
        refreshListItems(currentPath);
    }

    private void refreshListItems(String path){
        setTitle(path);
        File[] files=new File(path).listFiles();
        list.clear();

        if(files!=null){
            for(File file:files){
                Map<String,Object> map=new HashMap<>();
                //通过不同的文件夹或者文件类型使用不同的图标,本处只判断了apk文件并使用了对应的图标,其他的类别均是文件夹和文件的类别
                if(file.isDirectory()){
                    map.put("img",R.drawable.directory);
                }else{
                    String fileName=file.getName();
                    String prefix=fileName.substring(fileName.lastIndexOf(".")+1);
                    if(prefix.contains("apk")){
                        map.put("img",R.drawable.file_apk);
                    }else{
                        map.put("img", R.drawable.file_doc);
                    }
                }
                map.put("name",file.getName());
                map.put("currentPath",file.getPath());
                list.add(map);
            }
        }
        simpleAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        currentPath=(String)list.get(position).get("currentPath");
        File file=new File(currentPath);
        if(file.isDirectory()){
            refreshListItems(currentPath);
        }else{
            //将得到的安装包路径
            String fileName=file.getPath();
            String prefix=fileName.substring(fileName.lastIndexOf(".") + 1);
            if(prefix.contains("apk")){
                Intent intent=new Intent();
                System.out.println("当前文件路径:"+file.getPath());
                intent.putExtra("apk_path",file.getPath());
                setResult(RESULT_OK, intent);
                finish();
            }else{
                Toast.makeText(FileExplorerActivity.this, "请选择Apk安装包", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(rootPath.equals(currentPath)){
        super.onBackPressed();
    }else{
        File file=new File(currentPath);
            currentPath=file.getParentFile().getPath();
            refreshListItems(currentPath);
        }
    }
}
