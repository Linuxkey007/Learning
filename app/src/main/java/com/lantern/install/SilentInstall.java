package com.lantern.install;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * 静默安装的主要逻辑模块
 * @Author Zhenpeng Chen
 * @Date 2015/12/23
 * @EMail linuxkey007@gmail.com
 *
 * 说明:本程序参考郭霖大神的博客:http://blog.csdn.net/guolin_blog/article/details/47803149
 */
public class SilentInstall {
    /**
     *执行静默安装逻辑,需要手机root
     * @param apkPath 要安装的apk文件的路径
     * @return 安装成功返回true,安装失败返回false;
     */
    public boolean install(String apkPath){
        boolean result=false;
        DataOutputStream dataOutputStream=null;
        BufferedReader bufferedReader=null;


        try {
            //申请su权限
            Process process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());

            //执行pm install命令,如果pm install口令格式有误也将导致安装失败
            String command = "pm install -r " + apkPath + "\n";
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
            System.out.println("安装完整口令:" + command);
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();
            bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String msg = "";
            String line;
            //读取命令执行结果
            while ((line = bufferedReader.readLine()) != null) {
                msg += line;
            }
            System.out.println("静默安装操作状态:"+"msg");
            Log.d("TAG","install msg is"+msg);
            //如果结果中包含Failure字样就认为是安装失败,否则认为安装成功
            if (!msg.contains("Failure")) {
                result = true;
            }
        }catch (Exception e){
            Log.e("TAG",e.getMessage(),e);
        }finally {
            try{
                if(dataOutputStream!=null){
                    dataOutputStream.close();
                }
                if(bufferedReader!=null){
                    bufferedReader.close();
                }

            }catch (IOException e){
               Log.e("TAG",e.getMessage(),e);
            }
        }
        return result;
    }
}
