package com.matatalab.matatacode.model;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.*;
import android.media.SoundPool;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.matatalab.matatacode.AppConst;
import com.matatalab.matatacode.app.BaseApplication;
import com.matatalab.matatacode.utils.MLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyBluetooth {
    public static boolean fail=true;
    public static Boolean wait=true;
    public static void BleWrite(byte[] bytes)
    {
        if(AppConst.mClient==null)return;
        int length=bytes.length;
        int i=0;
        MyQueue.queue.clear();
        while(true) {
            if(AppConst.mClient==null)return;
            byte[] cd;
            if(i<(length-20)){cd=new byte[20];for(int n=0;n<cd.length;n++)cd[n]=bytes[n+i];i+=20;}
            else {cd=new byte[length-i];for(int n=0;n<cd.length;n++)cd[n]=bytes[n+i];i=length;}
            wait=true;
            AppConst.mClient.write(AppConst.DeviceMac, UUID.fromString(AppConst.UUID_SERVICE), UUID.fromString(AppConst.UUID_WRITE), cd, new BleWriteResponse() {
                @Override
                public void onResponse(int code) {
                    MLog.td("tjl", "write,succeed:" + code);
                    wait=false;
                }
            });
            while(wait)
            {
                try {
                    Thread.sleep(1);
                }catch (Exception E){}
                if(AppConst.mClient==null)return;
            }
            if(length==i)return;
        }
    }
    public static byte[] BleWait()
    {
        int count=0;
        while (MyQueue.queue.QueueEmpty()) {
            try {
                Thread.sleep(1);
            } catch (Exception E) {
                MLog.td("tjl", "error,code:" + E.getMessage());
            }
            if(count++>4000){ MLog.td("tjl", "wait time out:");return null;}
        }
        byte[] r=(byte[])MyQueue.queue.deQueue();
        return r;
    }
}
