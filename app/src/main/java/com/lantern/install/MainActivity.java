package com.lantern.install;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
/**
 * 构造程序的主界面模块
 * @Author Zhenpeng Chen
 * @Date 2015/12/23
 *@EMail linuxkey007@gmail.com
 *
 * 说明:本程序参考郭霖大神的博客:http://blog.csdn.net/guolin_blog/article/details/47803149
 */
public class MainActivity extends AppCompatActivity {
    private TextView apkPathText;
    private String apkPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        apkPathText=(TextView)findViewById(R.id.apkPathText);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0&&resultCode==RESULT_OK){
            apkPath=data.getStringExtra("apk_path");
            System.out.println("获取的路径:"+apkPath);
            apkPathText.setText(apkPath);
        }
    }

    public void onChooseApkFile(View view){
        Intent intent=new Intent(this,FileExplorerActivity.class);
        startActivityForResult(intent, 0);
    }

    public void onSilentInstall(View view){
        if(!isRoot()){
            Toast.makeText(this,"没有root权限,不能使用静默安装功能",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(apkPath)){
            Toast.makeText(this, "请选择安装包", Toast.LENGTH_SHORT).show();
            return;
        }
        final Button button=(Button)view;
        button.setText("安装中...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                SilentInstall silentInstall=new SilentInstall();
                final boolean result=silentInstall.install(apkPath);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(result){
                            Toast.makeText(MainActivity.this, "安装成功", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this, "安装失败", Toast.LENGTH_SHORT).show();
                        }
                        button.setText("静默安装");
                    }
                });
            }
        }).start();
    }


    public void onForwardToAccessibility(View view){
        Intent intent=new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    public void onSmartInstall(View view){
        if(TextUtils.isEmpty(apkPath)){
            Toast.makeText(this, "请选择安装包", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri uri=Uri.fromFile(new File(apkPath));
        Intent localIntent=new Intent(Intent.ACTION_VIEW);
        localIntent.setDataAndType(uri,"application/vnd.android.package-archive");
        startActivity(localIntent);
    }


    /**
     * 判断手机是否拥有root权限
     * @return 有root权限返回true,否则返回false
     */
    public boolean isRoot(){
        boolean flag=false;
        try{
            flag=(new File("/system/bin/su").exists())||(new File("system/xbin/su").exists());
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }
}
