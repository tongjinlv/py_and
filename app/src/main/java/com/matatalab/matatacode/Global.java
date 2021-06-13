package com.matatalab.matatacode;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.*;
import android.media.SoundPool;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.matatalab.matatacode.app.BaseApplication;
import com.matatalab.matatacode.model.ProperTies;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Global {

    // -------------- 发布时要更改的相关属性定义开始 --------------
    /**
     * APP版本状态
     */

    // -------------- 发布时要更改的相关属性定义结束 --------------

    /**
     * 日志控制
     */
    public static final boolean APP_LOG_DEBUG = isDev();

    public static final String BUILD_NO = "{BuildNo}";
    public static final String RELEASE_DATE = "{ReleaseDate}";
    private static final String UN_DEFINED = "NA";

    /**
     * APP当前的版本状态 ：开发版本，测试版，灰度发布版，正式发布版
     */
    private static enum AppStatus {
        DEV, TEST, GRAY, OFFICIAL
    }

    /**
     * 系统model name
     */
    private static String mSystemModelName;
    /**
     * 系统版本号
     */
    private static String mSystemVersion;
    /**
     * 设备序列号
     */
    private static String mDeviceSerialNum;
    /**
     * app序列号
     */
    private static String mAppSerialNum;

    private static String mAppVersionName;

    private static int mAppVersionCode;

    private static String mClientIp = "";

    private static boolean hasInit = false;

    public static void init() {
        synchronized (UN_DEFINED) {
            if (hasInit) {
                return;
            }

            genAppVersionName();

            hasInit = true;
        }
    }

    public static boolean isDev() {
        return true;
    }

    public static boolean hasInit() {
        return hasInit;
    }

    public static String getBuildNo() {
        if (BUILD_NO.contains("BuildNo")) {
            return "0000";
        } else {
            return BUILD_NO;
        }
    }

    /**
     * 取Manifest.xml中配置的versionName
     *
     * @return
     */
    public static String getAppVersionName() {
        if (TextUtils.isEmpty(mAppVersionName)) {
            genAppVersionName();
        }
        return mAppVersionName;
    }


    public static String getAppVersionName4Crash() {
        StringBuffer sb = new StringBuffer();
        String packageName = BaseApplication.self().getPackageName();
        try {
            sb.append(BaseApplication.self().getPackageManager().getPackageInfo(packageName, 0).versionName);
            sb.append(".");
            sb.append(getBuildNo());
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String getAppReleaseDate() {
        return RELEASE_DATE;
    }

    private static String getAndroidVersion() {
        StringBuffer sb = new StringBuffer();
        String adrRelease = android.os.Build.VERSION.RELEASE;
        if (TextUtils.isEmpty(adrRelease)) {
            sb.append(UN_DEFINED);
        } else {
            sb.append(adrRelease);
        }

        sb.append("_");
        int sdk = android.os.Build.VERSION.SDK_INT;
        sb.append(sdk);
        return sb.toString();
    }

    /**
     * 字符过滤，过滤掉特殊字符
     *
     * @param text
     * @return
     */
    private static StringBuffer filter(String text) {
        StringBuffer result = new StringBuffer();
        if (TextUtils.isEmpty(text)) {
            result.append(UN_DEFINED);
            return result;
        }
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] > 0x20 && chars[i] != '/' && chars[i] != '_' && chars[i] != '&' && chars[i] != '|' && chars[i] != '-') {
                result.append(chars[i]);
            }
        }
        return result;
    }

    /**
     * 获取版本号。得到的是形如2.5.1.0123格式的版本号
     */
    private static void genAppVersionName() {
        String packageName = BaseApplication.self().getPackageName();
        try {
            // StringBuffer sb = new StringBuffer();
            // sb.append(PaoMoApplication.self().getPackageManager().getPackageInfo(packageName,
            // 0).versionName);
            // sb.append(" build");
            // sb.append(getBuildNo());
            StringBuffer sb = new StringBuffer();
            sb.append(BaseApplication.self().getPackageManager().getPackageInfo(packageName, 0).versionName);
            mAppVersionName = sb.toString();
        } catch (NameNotFoundException e) {
            mAppVersionName = "NA";
        }
    }

    /**
     * 获取版本号
     *
     * @return
     */
    public static int getVersionCode() {
        try {
            PackageManager manager = BaseApplication.self().getPackageManager();
            PackageInfo info = manager.getPackageInfo(BaseApplication.self().getPackageName(), 0);
            int vc = info.versionCode;
            return vc;
        } catch (Exception e) {
            return -1;
        }
    }
    public static void WriteFile(String file,String msg)
    {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos , "UTF-8");
            osw.write(msg);
            osw.flush();
        }catch (Exception E){}
    }
    /**
     * 获取设备序列号
     *
     * @return
     */
    public static String getDeviceSerialNum() {
        if (mDeviceSerialNum == null) {
            mDeviceSerialNum = android.os.Build.SERIAL;
        }
        return mDeviceSerialNum;
    }

    /**
     * 获取系统model name
     *
     * @return
     */
    public static String getSystemModelName() {
        if (mSystemModelName == null) {
            mSystemModelName = android.os.Build.MODEL;
        }
        return mSystemModelName;
    }
    /**
     * 获取系统model name
     *
     * @return
     */
    public static boolean isMatataDevice(String source,String name) {
        String[] all=source.split("\\|");
        for(int i=0;i<all.length;i++)
        {
            if(name.equals(all[i]))return true;
        }
        return false;
    }
    public static String getDFUNameByName(String[] source,String name) {
        for(int i=0;i<source.length;i++)
        {
            String[] all=source[i].split("\\|");
            if(name.equals(all[0]))return all[1];
        }
        return "";
    }
    public static List<Map<String, Object>> removeRepeatMapByKey(List<Map<String, Object>> list, String mapKey)
    {
        if (list.isEmpty()) return null;
        //把list中的数据转换成msp,去掉同一id值多余数据，保留查找到第一个id值对应的数据
        List<Map<String, Object>> listMap = new ArrayList<>();
        Map<String, Map> msp = new HashMap<>();
        for(int i = list.size()-1 ; i>=0; i--){
            Map map = list.get(i);
            String id = (String)map.get(mapKey);
            map.remove(mapKey);
            msp.put(id, map);
        }
        //把msp再转换成list,就会得到根据某一字段去掉重复的数据的List<Map>
        Set<String> mspKey = msp.keySet();
        for(String key: mspKey){
            Map newMap = msp.get(key);
            newMap.put(mapKey, key);
            listMap.add(newMap);
        }
        return listMap;
    }
    public static int CompareCount(String a,String b)
    {
        int i=0;
        byte[]  b_a = a.getBytes();
        byte[]  b_b = b.getBytes();
        for(int n=0;n<a.length();n++)
        {
            if(b_a[i]==b_b[i])i++;
        }
        return i;
    }
    public static byte[] getMacBytes(String mac) {
        byte[] macBytes = new byte[6];
        String[] strArr = mac.split(":");
        for (int i = 0; i < strArr.length; i++) {
            int value = Integer.parseInt(strArr[i], 16);
            macBytes[i] = (byte) value;
        }
        return macBytes;
    }
    public static String getMacString(byte[] macBytes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < macBytes.length; i++) {
            builder.append(':').append(Integer.toHexString(0xFF & macBytes[i]));
        }
        return builder.substring(1);
    }
    /**
     * 获取指定目录内所有文件路径
     * @param dirPath 需要查询的文件目录
     * @param _type 查询类型，比如mp3什么的
     */
    public static JSONArray getAllFiles(String dirPath, String _type) {
        File f = new File(dirPath);
        if (!f.exists()) {//判断路径是否存在
            MLog.td("tjl","路径不存在");
            return null;
        }

        File[] files = f.listFiles();

        if(files==null){//判断权限
            MLog.td("tjl","没有权限");
            return null;
        }
        MLog.td("tjl","访问目录");
        JSONArray fileList = new JSONArray();
        for (File _file : files) {//遍历目录
            if(_file.isFile() && _file.getName().endsWith(_type)){
                String _name=_file.getName();
                String filePath = _file.getAbsolutePath();//获取文件路径
                String fileName = _file.getName().substring(0,_name.length()-4);//获取文件名
                MLog.td("LOGCAT","fileName:"+fileName);
                MLog.td("LOGCAT","filePath:"+filePath);
                try {
                    JSONObject _fInfo = new JSONObject();
                    _fInfo.put("name", fileName);
                    _fInfo.put("path", filePath);
                    fileList.put(_fInfo);
                }catch (Exception e){
                }
            } else if(_file.isDirectory()){//查询子目录
                getAllFiles(_file.getAbsolutePath(), _type);
            } else{
            }
        }
        return fileList;
    }
    public static int TextEndCount(String[] text)
    {
        int n=0;
        for(int i=0;i<text.length;i++)
        {
            if(text[i].toLowerCase().endsWith(".txt"))n++;
        }
        return n;
    }
    public static String hexToString(byte[] bytes)
    {
        String msg="";
        for(int i=0;i<bytes.length;i++)
        {
            msg+=Integer.toHexString(bytes[i] & 0xFF);
        }
        return msg;
    }
    public static String readNetValue(String name)
    {
        try {
            URL url = new URL("http://dm.trtos.com/php/value.php?name="+name);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
            BufferedReader buffer = new BufferedReader(in);
            String inputLine = null;
            String resultData = "";
            int fc=0;
            while (((inputLine = buffer.readLine()) != null)) {
                resultData += inputLine + "\n";
                if(fc++>100)return "";
            }
            return resultData;
        }catch (Exception E)
        {
            MLog.td("tjl","error:"+E.getMessage());
            return "";
        }
    }
    @TargetApi(28)
    public static void applyNotchAfterPie(Activity activity) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）
        if(height/width>=2||width/height>=2)
            try {
                Window window=activity.getWindow();
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                window.setAttributes(attributes);
            }catch (Exception E){}
    }
    public static void NetRecord(final String msg)
    {
       Thread myThread1=new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL("http://trtos.com/php/recorduser.php?value="+msg);
                    HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                    InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
                    BufferedReader buffer = new BufferedReader(in);
                    String inputLine = null;
                    String resultData = "";
                    int fc=0;
                    while (((inputLine = buffer.readLine()) != null)) {
                        resultData += inputLine + "\n";
                        if(fc++>100)return;
                    }
                }catch (Exception E)
                {
                    MLog.td("tjl","NetRecord:"+E.getMessage());
                    return ;
                }
            }
        };
        myThread1.start();
    }
    public static String writeNetValue(String name,String text)
    {
        try {
            URL url = new URL("http://dm.trtos.com/php/value.php?name="+name+"&value="+text);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
            BufferedReader buffer = new BufferedReader(in);
            String inputLine = null;
            String resultData = "";
            int fc=0;
            while (((inputLine = buffer.readLine()) != null)) {
                resultData += inputLine + "\n";
                if(fc++>100)return "";
            }
            MLog.td("tjl",resultData);
            return resultData;
        }catch (Exception E)
        {
            MLog.td("tjl","error:"+E.getMessage());
            return "";
        }
    }
    public static void writeconfig(Context context,String key,String value)
    {
        Map<String, String> mqttinfomap = new HashMap<>();
        mqttinfomap.put(key, value);
        ProperTies.setPropertiesMap(context,mqttinfomap);
    }
    public static String readconfig(Context context,String key)
    {
        SharedPreferences share = ProperTies.getProperties(context);
        String defaultname=share.getString(key,"");
        return defaultname;
    }
    public static void writeFileData(String fileName, String message) {
        try {
            File file = new File(fileName);
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            ps.println(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String readFileData(String file) {
        BufferedReader bre = null;
        String str = "";
        String returnstr = "";
        String a;
        try {
            bre = new BufferedReader(new FileReader(file));//此时获取到的bre就是整个文件的缓存流
            while ((str = bre.readLine()) != null) { // 判断最后一行不存在，为空结束循环
                String[] arr = str.split("\\s+");
                for (String ss : arr) {
                    a = arr[0];
                }
                returnstr=str;
            }
        } catch (Exception e) {
            MLog.td("LZ", "readTxt: ---------------" + e.toString());
        }
        return returnstr;
    }
    public static boolean delete(String delFile) {

        File file = new File(delFile);
        file.getAbsoluteFile().delete();
        System.gc();
        return true;
    }
    public static int compareVersion(String version1, String version2) {
        version1=version1.replace('.','&');
        version2=version2.replace('.','&');
        String v1a[]=version1.split("&");
        String v2a[]=version2.split("&");
        for(int i=0;i<v1a.length;i++)
        {
            try {
                if(Integer.parseInt(v1a[i])>Integer.parseInt(v2a[i]))return 1;
            }catch (Exception E)
            {
                MLog.td("tjl","compareVersion:"+E.getMessage());
            }
        }
        return -1;
    }
    public static  int versionCompare(String version1, String version2) {
        String[] v1Arr = version1.split("\\.");
        String[] v2Arr = version2.split("\\.");

        int i = 0;
        int diff = 0;
        diff = v1Arr.length - v2Arr.length;

        while (diff == 0 && i < v1Arr.length
                && (diff = v1Arr[i].length() - v2Arr[i].length()) == 0
                && (diff = v1Arr[i].compareToIgnoreCase(v2Arr[i])) == 0) {
            ++i;
        }
        if (diff > 0) {
            return 1;
        } else if (diff < 0) {
            return -1;
        }
        return 0;
    }
    public static boolean underFilterVersionV2(String version,String triggerVersion) {
        String[] versionArr = version.split("\\.");
        String[] triggerVersionArr = triggerVersion.split("\\.");
        for (int i = 0; i < version.length(); ) {
            if (Integer.parseInt(versionArr[i]) > Integer.parseInt(triggerVersionArr[i])) {
                return true;
            } else if (Integer.parseInt(versionArr[i]) < Integer.parseInt(triggerVersionArr[i])) {
                break;
            } else {
                i++;
                if (Integer.parseInt(versionArr[i]) > Integer.parseInt(triggerVersionArr[i])) {
                    return true;
                } else if (Integer.parseInt(versionArr[i]) < Integer.parseInt(triggerVersionArr[i])) {
                    return false;
                } else {
                    i++;
                    if (i>=versionArr.length || i>=triggerVersionArr.length){
                        return true;
                    }
                    if (Integer.parseInt(versionArr[i]) >= Integer.parseInt(triggerVersionArr[i])) {
                        return true;
                    } else if (Integer.parseInt(versionArr[i]) < Integer.parseInt(triggerVersionArr[i])) {
                        return false;
                    }
                }
            }
        }
        return false;
    }
    public static void setHeightAsWidth(Context context, View view, double w, double h) {
        //w 屏幕宽度占比/100 h=w/100
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        if(width<height)width=height;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width =(int)(width*w/100);
        params.height = (int)(params.width*h/100);
       // MLog.td("tjl","set win.x"+params.width +",winy="+params.height);
        view.setLayoutParams(params);
    }
    public static  boolean gpsIsOpen(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps|| network) {
            return true;
        }
        return false;
    }
    public static boolean blueToothIsOpen()
    {
        BluetoothAdapter blueadapter=BluetoothAdapter.getDefaultAdapter();
        //支持蓝牙模块
        if (blueadapter!=null) {
            if (blueadapter.isEnabled()) {
                return true;
            } else return false;
        }
        return false;
    }
    public static void setHeightAsHeight(Context context, View view, int w, int h) {
        //w 屏幕宽度占比/100 h=w/100
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        // MLog.td("tjl","win.x"+width+",winy="+height);
        if(width<height)height=width;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width =(int)(height*w/100);
        params.height = (int)(params.width*h/100);
        view.setLayoutParams(params);
    }
    public static void setHeight(Context context, View view,int h) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        if(width<height)width=height;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = (int)(width*h/100);
        view.setLayoutParams(params);
    }
    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
    public static String hexStr2Str(String unicode) {
        if (unicode == null || "".equals(unicode)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int i = -1;
        int pos = 0;
        while ((i = unicode.indexOf("\\u", pos)) != -1) {
            sb.append(unicode.substring(pos, i));
            if (i + 5 < unicode.length()) {
                pos = i + 6;
                sb.append((char) Integer.parseInt(unicode.substring(i + 2, i + 6), 16));
            }
        }
        return sb.toString();
    }
    public static void savepic(Bitmap bitmap,String name)
    {
        try {
            FileOutputStream os = new FileOutputStream(name);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        }catch (Exception E){}

    }
    static class MapComparatorDesc implements Comparator<Map<String, Object>> {
        @Override
        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            Integer v1 = Integer.valueOf(m1.get("rssi").toString());
            Integer v2 = Integer.valueOf(m2.get("rssi").toString());
            if (v2 != null) {
                return v2.compareTo(v1);
            }
            return 0;
        }

    }
    static class MapComparatorAsc implements Comparator<Map<String, Object>> {
        @Override
        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            Integer v1 = Integer.valueOf(m1.get("rssi").toString());
            Integer v2 = Integer.valueOf(m2.get("rssi").toString());
            if(v1 != null){
                return v1.compareTo(v2);
            }
            return 0;
        }

    }
}
