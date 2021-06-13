package com.matatalab.matatacode;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;

import android.provider.Settings;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.matatalab.matatacode.model.MyAdapter;
import com.matatalab.matatacode.model.PublicWay;
import com.matatalab.matatacode.utils.MLog;
import com.matatalab.matatacode.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UpgradeActivity extends AppCompatActivity {
    private static final String TAG = UpgradeActivity.class.getSimpleName();
    private ImageButton imageButton3;
    private ImageButton imageButton4;
    private ImageButton imageButton8;
    private String devName;
    private String devMac;
    private LinearLayout linearLayout;
    private List<Map<String,Object>> devList;
    private ListView list_view1;
    private TextView textview_au11;
    private boolean startopen=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Global.applyNotchAfterPie(this);
        MLog.td("tjl", "setContentView(R.layout.activity_upgrade);");
        setContentView(R.layout.activity_upgrade);
        initView(savedInstanceState);
        initData();

    }

    private void initData() {

    }


    private BluetoothAdapter.LeScanCallback mBLEScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            String name=String.valueOf(device.getName());
            if(name.length()>1&&rssi>-75)
            {
                if(Global.isMatataDevice(AppConst.BT_APP_NAME_ALL,name)||Global.isMatataDevice(AppConst.BT_DFU_NAME_ALL,name))
                {
                    MLog.td("tjl" , "mac:" + device.getAddress() + " rssi=" + rssi + " name=" + name);
                    Map<String,Object> map1 = new HashMap<String,Object>();
                    map1.put("name" , name);
                    map1.put("rssi" , String.valueOf(rssi));
                    map1.put("mac", String.valueOf(device.getAddress()));
                    if(rssi>-60)map1.put("pic" , R.mipmap.rssi0);
                    else if(rssi>-70)map1.put("pic" , R.mipmap.rssi1);
                    else if(rssi>-80)map1.put("pic" , R.mipmap.rssi2);
                    else if(rssi>-90)map1.put("pic" , R.mipmap.rssi3);
                    devList.add(map1);
                    devList=Global.removeRepeatMapByKey(devList,"mac");

                }
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            MLog.td("tjl" , "handleMessage what=" +String.valueOf(msg.what));
            try
            {
                switch (msg.what) {
                    case 999:
                        final Mdialog mdialog1 = new Mdialog(UpgradeActivity.this);
                        mdialog1.setTitle(getString(R.string.ALERT_TITLE));
                        mdialog1.setMessage(getString(R.string.pleaseopengps));
                        mdialog1.setBa(getString(R.string.ALERT_BUTTON_CANCEL));
                        mdialog1.setBb(getString(R.string.ALERT_BUTTON_OK));
                        mdialog1.setOnButtonAListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mdialog1.isShowing()) {
                                    mdialog1.dismiss();
                                }
                            }
                        });
                        mdialog1.setOnButtonBListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                                if (mdialog1.isShowing()) {
                                    mdialog1.dismiss();
                                }
                            }
                        });
                        mdialog1.setCancelable(false);                //用户可以点击后退键关闭 Dialog
                        mdialog1.setCanceledOnTouchOutside(false);   //用户不可以点击外部来关闭 Dialog
                        mdialog1.show();
                        break;
                    case 998:

                        final Mdialog mdialog2 = new Mdialog(UpgradeActivity.this);
                        mdialog2.setTitle(getString(R.string.ALERT_TITLE));
                        mdialog2.setMessage(getString(R.string.pleaseopenbluetooth));
                        mdialog2.setBa(getString(R.string.ALERT_BUTTON_CANCEL));
                        mdialog2.setBb(getString(R.string.ALERT_BUTTON_OK));
                        mdialog2.setOnButtonAListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mdialog2.isShowing()) {
                                    mdialog2.dismiss();
                                }
                            }
                        });
                        mdialog2.setOnButtonBListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                                AppConst.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                if(!AppConst.bluetoothAdapter.isEnabled())AppConst.bluetoothAdapter.enable();
                                if (mdialog2.isShowing()) {
                                    mdialog2.dismiss();
                                }
                            }
                        });
                        mdialog2.setCancelable(false);                //用户可以点击后退键关闭 Dialog
                        mdialog2.setCanceledOnTouchOutside(false);   //用户不可以点击外部来关闭 Dialog
                        mdialog2.show();

                        break;
                    case 1001:
                        if(devList.size()>0) {
                            list_view1.setVisibility(View.VISIBLE);
                            linearLayout.setVisibility(View.INVISIBLE);
                            Collections.sort(devList, new Global.MapComparatorDesc());
                            SimpleAdapter simpleAdapter = new SimpleAdapter(UpgradeActivity.this , devList , R.layout.list_item ,
                                    new String[]{"name" , "mac" , "pic"} , new int[]{R.id.tv1 , R.id.tv2 , R.id.iv1});
                            list_view1.setAdapter(simpleAdapter);
                            list_view1.setVisibility(View.VISIBLE);
                            imageButton8.setVisibility(View.VISIBLE);
                            imageButton3.setVisibility(View.INVISIBLE);
                        }else
                        {
                            imageButton8.setVisibility(View.INVISIBLE);
                            imageButton3.setVisibility(View.VISIBLE);
                            linearLayout.setVisibility(View.INVISIBLE);
                            final MAlert mdialog = new MAlert(UpgradeActivity.this);
                            //退出
                            mdialog.setTitle(getString(R.string.ALERT_TITLE));
                            mdialog.setMessage(getString(R.string.ALERT_MESSAGE_BLE_FIND_NOTHING));
                            mdialog.setOk(getString(R.string.ALERT_BUTTON_OK));
                            mdialog.setButtonOkListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mdialog.isShowing()) {
                                        mdialog.dismiss();
                                    }
                                }
                            });
                            mdialog.setCancelable(false);                //用户可以点击后退键关闭 Dialog
                            mdialog.setCanceledOnTouchOutside(false);   //用户不可以点击外部来关闭 Dialog
                            mdialog.show();
                        }
                        break;
                    case 1002:break;
                    default:break;
                }
        }catch (Exception E){MLog.td("tjl","UpgradeActivity.mHandler.Error"+E.getMessage());}
        }
    };
    private void ShufflePlayback(){

        /*
        MediaPlayer mMediaPlayer;
        mMediaPlayer=MediaPlayer.create(this, R.raw.du);
        mMediaPlayer.start();*/
    }
    protected void onDestroy() {
        MLog.td("tjl" , "onDestroy()");
        super.onDestroy();
    }
    public void onBackPressed() {
        MLog.td("tjl" , "onBackPressed()");
        try {AppConst.bluetoothAdapter.stopLeScan(mBLEScanCallback); }catch (Exception E){}
        AppConst.bluetoothAdapter=null;
        startActivity(new Intent(UpgradeActivity.this,FristActivity.class));
        finish();
        return;
    }


    private void initView(@Nullable Bundle savedInstanceState) {

        imageButton3=findViewById(R.id.imageButton3);
        imageButton4=findViewById(R.id.imageButton4);
        imageButton8=findViewById(R.id.imageButton8);
        linearLayout=findViewById(R.id.LinearLayout1);
        list_view1=findViewById(R.id.list_view1);
        linearLayout.setVisibility(View.INVISIBLE);
        list_view1.setVisibility(View.INVISIBLE);
        imageButton8.setVisibility(View.INVISIBLE);
        textview_au11=findViewById(R.id.textview_au11);
        Global.setHeightAsWidth(this,imageButton3,20,100*152/532);
        Global.setHeightAsWidth(this,imageButton8,20,100*152/532);
        Global.setHeightAsWidth(this,imageButton4,14,100*189/532);
        textview_au11.setText("v "+AppConst.versionName+"\n"+AppConst.All_RIGHT_TEXT);
        this.getWindow().setBackgroundDrawable(getResources().getDrawable(R.mipmap.item25));
        imageButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!Global.gpsIsOpen(UpgradeActivity.this))
                {
                    mHandler.sendEmptyMessage(998);
                    return;
                }
                if(!Global.blueToothIsOpen())
                {
                    mHandler.sendEmptyMessage(999);
                    return;
                }
                MLog.td("tjl", "imagebutton3.down");
                imageButton3.setVisibility(View.INVISIBLE);
                imageButton8.setVisibility(View.INVISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
                devList=null;
                AppConst.bluetoothAdapter=null;
                devList = new ArrayList<Map<String,Object>>();
                AppConst.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if(!AppConst.bluetoothAdapter.isEnabled())AppConst.bluetoothAdapter.enable();
                if (AppConst.bluetoothAdapter == null )
                {
                    startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                }
                if (AppConst.bluetoothAdapter == null)
                {
                    ToastUtils.show( "Not Find Bluetooth Device");
                }
                AppConst.bluetoothAdapter.startLeScan(mBLEScanCallback);
                ShufflePlayback();
                Thread myThread=new Thread(){
                    public void run() {
                            try {
                                sleep(2000);
                                if(devList.size()==0)sleep(2000);
                                if(devList.size()==0)sleep(2000);
                                try {AppConst.bluetoothAdapter.stopLeScan(mBLEScanCallback); }catch (Exception E){}
                                sleep(300);
                                mHandler.sendEmptyMessageDelayed(1001, 1000);
                            } catch (Exception e) {
                                MLog.td("tjl" , String.valueOf(e.getMessage()));
                            }
                    }
                };
                myThread.start();
            }
        });
        imageButton8.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MLog.td("tjl", "imagebutton3.down");
                if(!Global.gpsIsOpen(UpgradeActivity.this))
                {
                    mHandler.sendEmptyMessage(999);
                    return;
                }
                if(!Global.blueToothIsOpen())
                {
                    mHandler.sendEmptyMessage(998);
                    return;
                }
                linearLayout.setVisibility(View.VISIBLE);
                imageButton8.setVisibility(View.INVISIBLE);
                list_view1.setVisibility(View.INVISIBLE);
                devList=null;
                AppConst.bluetoothAdapter=null;
                devList = new ArrayList<Map<String,Object>>();
                AppConst.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if(!AppConst.bluetoothAdapter.isEnabled())AppConst.bluetoothAdapter.enable();
                if (AppConst.bluetoothAdapter == null )
                {
                    startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                }
                if (AppConst.bluetoothAdapter == null)
                {
                    ToastUtils.show( "Not Find Bluetooth Device");
                }
                AppConst.bluetoothAdapter.startLeScan(mBLEScanCallback);
                ShufflePlayback();
                Thread myThread=new Thread(){
                    public void run() {
                        while(true) {
                            try {
                                sleep(2000);
                                if(devList.size()==0)sleep(2000);
                                if(devList.size()==0)sleep(2000);
                                try {AppConst.bluetoothAdapter.stopLeScan(mBLEScanCallback); }catch (Exception E){}
                                sleep(300);
                                mHandler.sendEmptyMessageDelayed(1001, 1000);
                                return;
                            } catch (Exception e) {
                                MLog.td("tjl" , String.valueOf(e.getMessage()));
                            }
                        }
                    }
                };
                myThread.start();
            }
        });
        list_view1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int i=0;

                for (Map<String, Object> m : devList) {
                    MLog.td("tjl" , String.valueOf(position) + "--," + m.get("name") + "," + m.get("rssi") + "," + m.get("mac"));
                    if(i++==position) {
                        MLog.td("tjl" , String.valueOf(position) + "+++," + m.get("name") + "," + m.get("rssi") + "," + m.get("mac"));
                        devName=String.valueOf(m.get("name"));
                        devMac=String.valueOf(m.get("mac"));
                        try {
                            if(Global.isMatataDevice(AppConst.BT_APP_NAME_ALL,devName))
                            {
                                startActivity(new Intent(UpgradeActivity.this , UpgradeReadyActivity.class).putExtra("mac" ,String.valueOf(m.get("mac"))).putExtra("name" ,String.valueOf(m.get("name"))));
                                finish();
                            }
                            if(Global.isMatataDevice(AppConst.BT_DFU_NAME_ALL,devName))
                            {
                                if(Global.isMatataDevice(AppConst.BT_DFU_NAME_OLD,devName))
                                {
                                    if(startopen==false)
                                    {
                                        startopen=true;
                                        AlertDialog.Builder builder = new AlertDialog.Builder(UpgradeActivity.this);
                                        builder.setTitle(getString(R.string.DFU_MATADFU_TYPE));
                                        devName="mdfutow";
                                        builder.setSingleChoiceItems(new String[]{"Tower","Bot"}, 0, new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int which) {
                                                MLog.td("tjl" ,"Select:"+String.valueOf(which));
                                                switch (which)
                                                {
                                                    case 0:devName="mdfutow";break;
                                                    case 1:devName="mdfubot";break;
                                                }
                                            }
                                        });
                                        builder.setPositiveButton(getString(R.string.ALERT_BUTTON_OK), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                startActivity(new Intent(UpgradeActivity.this ,UpgradeStartActivity.class).putExtra("mac" ,devMac).putExtra("name",devName));
                                                finish();
                                            }
                                        });
                                        builder.setNegativeButton(getString(R.string.ALERT_BUTTON_CANCEL),null);
                                        builder.setCancelable(false);
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                        startopen=false;
                                    }
                                }else
                                {
                                    startActivity(new Intent(UpgradeActivity.this , UpgradeStartActivity.class).putExtra("mac" ,devMac).putExtra("name" ,devName));
                                    finish();
                                }
                            }
                        } catch (Exception e) {
                            MLog.td("tjl" , String.valueOf(e.getMessage()));
                        }
                    }
                }
            }
        });
        imageButton4.setOnTouchListener( new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    //重新设置按下时的背景图片
                    ((ImageButton)view).getBackground().setAlpha(128);
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    //再修改为抬起时的正常图片
                    ((ImageButton)view).getBackground().setAlpha(255);
                    try{
                        try {AppConst.bluetoothAdapter.stopLeScan(mBLEScanCallback); }catch (Exception E){}
                        AppConst.bluetoothAdapter=null;
                        startActivity(new Intent(UpgradeActivity.this,FristActivity.class));//启动MainActivity
                        finish();
                    }catch (Exception e){
                        MLog.td("tjl", String.valueOf(e.getMessage()));
                    }
                }
                return false;
            }
        });
    }
}