package com.matatalab.matatacode;

import android.bluetooth.BluetoothAdapter;

import com.inuker.bluetooth.library.BluetoothClient;
import com.matatalab.matatacode.model.ChaquoPython;
import com.matatalab.matatacode.model.MyBluetooth;

public class AppConst {



    /**
     * 童金吕使用的几个变量
     *
     */
    public static final String BT_APP_NAME_ALL = "Matalab|MatataCon|MatataBot";
    public static final String BT_DFU_NAME_ALL = "MataDfu|mdfubot|mdfutow|mdfucon";
    public static final String BT_APP_NAME_OLD="Matalab";
    public static final String BT_APP_NAME_CON="MatataCon";
    public static final String BT_APP_NAME_BOT="MatataBot";
    public static final String BT_DFU_NAME_OLD="MataDfu";
    public static final String BT_DFU_NAME_TOWER="mdfutow";
    public static final String BT_DFU_NAME_BOT="mdfubot";
    public static final String BT_DFU_NAME_CON="mdfucon";
    public static final String BT_BE_NAME_ALL[]={"MatataCon|mdfucon","MatataBot|mdfubot","Matalab|mdfubot"};
    public static final String BT_BE_PNAME_ALL[]={"MatataCon|mdfucon","MatataBot|mdfubot","Matalab|mdfubot"};
    public static final String BT_BE_NAME_BOT="MatataBot|mdfubot";
    public static final String BT_BE_NAME_OLD="Matalab|mdfubot";
    public static String EXAMPLE_PATH="";
    public static final String UNTITLED_NAME="     ";
    public static String APP_DOWNLOAD_URL="http://typeecho.trtos.com/MatataCode_release_v";
    public static String All_RIGHT_TEXT="This application is built on Blockly. All rights reserved \n @Matatalab.";
    public static String versionName="";
    public static boolean NeedUpdate=false;
    public static BluetoothAdapter bluetoothAdapter;
    public  static String  devMac;
    public static Boolean ConnectOk=false;
    public  static final byte[] car_header=new byte[]{(byte)0xfe,0x06,0x7e,0x01,0x00,0x00,0x52,(byte)0xc6};
    public  static final byte[] con_header=new byte[]{(byte)0xfe,0x06,0x7e,0x02,0x00,0x00,0x52,(byte)0xc6};
    public  static String lang;
    public static String runDirPath;
    public static BluetoothClient mClient;
    public static String DeviceName;
    public static String DeviceMac;
    public static ChaquoPython chaquoPython;

    public static final String CODE_HEARD= "# -*- coding: utf-8 -*-\n" +
            "from java import jclass\n" +
            "import sys\n" +
            "Python2Java = jclass(\"com.matatalab.matatacode.model.Python2Java\")\n" +
            "android = Python2Java(\"python\")\n" +
            "sys.stdout=android\n" +
            "#################################################################\n" +
            "import math\n" +
            "import time\n" +
            "import imp\n" +
            "import threading\n" +
            "from matatabot.matatabot import matatabot\n" +
            "from controller.controller import controller\n" +
            "imp.load_source('matatabot', '/data/data/com.matatalab.matatacode/run/matatabot/matatabot.py')\n" +
            "imp.load_source('controller', '/data/data/com.matatalab.matatacode/run/controller/controller.py')\n" +
            "print(\"...................main start.......................\")\n" +
            "matatabot  = matatabot()\n" +
            "controller  = controller()\n";
            //"matatabot.start()\n";
    public static final String UUID_SERVICE = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String UUID_NOTIFY = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String UUID_WRITE = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
    public static byte[] onNotify;
    public static Boolean pythonRun=false;
    public static Boolean pythonRunflag=false;

    public static final int UPGRADE_RAW_ID_BOT = R.raw.car_update;
    public static final int UPGRADE_RAW_ID_TOWER = R.raw.tower_update;
    public static final int UPGRADE__RAW_ID_CONTROLLER = R.raw.mata_con;
    public static final String VIDEO_URL_YOUTU = "https://youtu.be/zBRkFO3qnN8/";
    public static final String VIDEO_URL_BILIBILI = "https://www.bilibili.com/video/av68882667/";


}
