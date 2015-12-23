package com.lantern.install;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * 构造一个借助Android无障碍服务的智能安装模块
 * @Author Zhenpeng Chen
 * @Date 2015/12/23
 * @EMail linuxkey007@gmail.com
 *
 * 说明:本程序参考郭霖大神的博客:http://blog.csdn.net/guolin_blog/article/details/47803149
 */
public class SmartAccessibilityService extends AccessibilityService {
    Map<Integer, Boolean> handledMap = new HashMap<>();

    public SmartAccessibilityService(){

    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo accessibilityNodeInfo=event.getSource();
        if(accessibilityNodeInfo!=null){
            int eventType=event.getEventType();
            if(eventType==AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED||
                    eventType==AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
                if(handledMap.get(event.getWindowId())==null){
                    boolean handled=iterateNodesAndHandle(accessibilityNodeInfo);
                    if (handled){
                        handledMap.put(event.getWindowId(),true);
                    }
                }
            }
        }

    }

    private boolean iterateNodesAndHandle(AccessibilityNodeInfo accessibilityNodeInfo){
        if(accessibilityNodeInfo!=null){
            int childCount= accessibilityNodeInfo.getChildCount();
            if("android.widget.Button".equals(accessibilityNodeInfo.getClassName())){
                String nodeContent=accessibilityNodeInfo.getText().toString();
                if("安装".equals(nodeContent)||"完成".equals(nodeContent)||"确定".equals(nodeContent)){
                    accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return true;
                }
            }else if("android.widget.ScrollView".equals(accessibilityNodeInfo.getClassName())){
                accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            }
            for(int i=0;i<childCount;i++){
                AccessibilityNodeInfo accessibilityNodeInfo1=accessibilityNodeInfo.getChild(i);
                if(iterateNodesAndHandle(accessibilityNodeInfo1)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onInterrupt() {

    }
}
