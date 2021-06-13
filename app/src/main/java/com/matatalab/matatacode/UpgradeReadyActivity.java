package com.matatalab.matatacode;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.matatalab.matatacode.model.PublicWay;
import com.matatalab.matatacode.utils.FunctionUtils;
import com.matatalab.matatacode.utils.MLog;
import com.matatalab.matatacode.utils.ToastUtils;

import java.util.List;
import java.util.UUID;


public class UpgradeReadyActivity extends AppCompatActivity {
    private static final String TAG = UpgradeReadyActivity.class.getSimpleName();
    private ImageButton imageButton5;
    private ImageButton imageButton6;
    private ImageButton imageButton7;
    private ImageView imageView1;
    private int devType,imgBg,imgLogo,imgStart,imgStartp,imgHelpp,imgHelp;
    private boolean dfuMode;
    private ProgressBar progressBar_au1;
    private static final int  DEV_OLD= 501;
    private static final int  DEV_BOT= 502;
    private static final int  DEV_CON= 503;
    private TextView textview_aur1;
    private TextView textview_name;
    private Boolean run=true;
    private static final String UUID_SERVICE = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    private static final String UUID_NOTIFY = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
    private static final String UUID_WRITE = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
    private static final byte[] BOT_DFU_CMD= new byte[]{(byte)0x80 ,(byte)0xaa ,(byte)0x55 ,(byte)0x81 , (byte)0x00};
    private static final byte[] CON_DFU_CMD= new byte[]{(byte)0x80 ,(byte)0xaa ,(byte)0x55 ,(byte)0x13 , (byte)0x00};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Global.applyNotchAfterPie(this);
        run=true;
        MLog.td("tjl", "setContentView(R.layout.activity_upgrade);");
        setContentView(R.layout.activity_upgrade_ready);
        Intent intent=getIntent();
        AppConst.DeviceMac=intent.getStringExtra("mac");
        AppConst.DeviceName= intent.getStringExtra("name");
        MLog.td("tjl" , "name="+AppConst.DeviceName+",mac="+AppConst.DeviceMac);
        if(AppConst.DeviceName.equals(AppConst.BT_APP_NAME_BOT))devType=DEV_BOT;
        if(AppConst.DeviceName.equals(AppConst.BT_APP_NAME_OLD))devType=DEV_OLD;
        if(AppConst.DeviceName.equals(AppConst.BT_APP_NAME_CON))devType=DEV_CON;
        if(AppConst.DeviceName.toLowerCase().indexOf("dfu")>-1)dfuMode=true;else dfuMode=false;
        MLog.td("tjl" , "devType:"+String.valueOf(devType)+"dfuMode:"+dfuMode);
        initView(savedInstanceState);
        initData();
    }

    private void initData() {

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            MLog.td("tjl" , "handleMessage --- msg = " + msg);
            switch (msg.what) {
                case 1000:
                    try {
                        Mdialog mdialog = new Mdialog(UpgradeReadyActivity.this);
                        mdialog.setTitle(getString(R.string.ALERT_TITLE));
                        mdialog.setMessage(getString(R.string.DFU_CONTROLLER_READY));
                        mdialog.setBa(getString(R.string.ALERT_BUTTON_CANCEL));
                        mdialog.setBb(getString(R.string.ALERT_BUTTON_OK));
                        mdialog.setOnButtonAListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MLog.td("tjl", "ab");
                                Message tempMsg = mHandler.obtainMessage();
                                tempMsg.what = 1004;
                                mHandler.sendMessage(tempMsg);
                            }
                        });
                        mdialog.setOnButtonBListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Message tempMsg = mHandler.obtainMessage();
                                tempMsg.what = 1005;
                                mHandler.sendMessage(tempMsg);
                            }
                        });
                        mdialog.setCancelable(false);                //用户可以点击后退键关闭 Dialog
                        mdialog.setCanceledOnTouchOutside(false);   //用户不可以点击外部来关闭 Dialog
                        mdialog.show();
                    }catch (Exception E){MLog.td("tjl","使用第二次就出现异常 加上try catch 就不出现:"+E.getMessage());}
                    break;
                case 1002:break;
                case 1004:
                    progressBar_au1.setVisibility(View.INVISIBLE);
                    AppConst.mClient=null;
                    run=false;
                    startActivity(new Intent(UpgradeReadyActivity.this,UpgradeActivity.class));
                    finish();
                    break;
                case 1005:
                    progressBar_au1.setVisibility(View.INVISIBLE);
                    MLog.td("tjl", "devMac="+AppConst.DeviceMac+",devName="+AppConst.DeviceName);
                    AppConst.mClient=null;
                    String DevName=Global.getDFUNameByName(AppConst.BT_BE_NAME_ALL,AppConst.DeviceName);
                    run=false;
                    startActivity(new Intent(UpgradeReadyActivity.this , UpgradeStartActivity.class).putExtra("mac" ,AppConst.DeviceMac).putExtra("name" ,DevName));
                    finish();
                    break;
                case 1003:
                    progressBar_au1.setVisibility(View.INVISIBLE);
                    ToastUtils.show(getString(R.string.DFU_RESULT_ERROR));
                    break;
                default:break;
            }
        }
    };
    public void onBackPressed() {
        MLog.td("tjl" , "onBackPressed()");
        AppConst.mClient=null;
        run=false;
        startActivity(new Intent(UpgradeReadyActivity.this,UpgradeActivity.class));
        finish();
        return;
    }
    protected void onDestroy() {
        super.onDestroy();
    }
    private void initView(@Nullable Bundle savedInstanceState) {

        imageButton5=findViewById(R.id.imageButton5);
        imageButton6=findViewById(R.id.imageButton6);
        imageButton7=findViewById(R.id.imageButton7);
        Global.setHeightAsWidth(this,imageButton5,14,100*189/532);
        Global.setHeightAsHeight(this,imageButton7,30,100*394/371);
        Global.setHeightAsHeight(this,imageButton6,30,100*394/371);
        imageView1=findViewById(R.id.imageView1);
        textview_name=findViewById(R.id.textView_name);
        progressBar_au1=findViewById(R.id.progressBar_au1);
        progressBar_au1.setVisibility(View.INVISIBLE);
        switch (devType)
        {
            case DEV_OLD:
                this.getWindow().setBackgroundDrawable(getResources().getDrawable(R.mipmap.pic_bg_bot));
                imgBg=R.mipmap.pic_bg_bot;
                imgStart=R.mipmap.pic_btn_upgrade_7;
                imgStartp=R.mipmap.pic_btn_upgrade_7p;
                imgHelp=R.mipmap.pic_btn_upgrade_9;
                imgHelpp=R.mipmap.pic_btn_upgrade_9p;
                imgLogo=R.mipmap.pic_logo_bot;
                textview_name.setText("MatataBot");
                break;
            case DEV_CON:
                this.getWindow().setBackgroundDrawable(getResources().getDrawable(R.mipmap.pic_bg_con));
                imgBg=R.mipmap.pic_bg_con;
                imgStart=R.mipmap.pic_btn_upgrade_2;
                imgStartp=R.mipmap.pic_btn_upgrade_2p;
                imgHelp=R.mipmap.pic_btn_upgrade_3;
                imgHelpp=R.mipmap.pic_btn_upgrade_3p;
                imgLogo=R.mipmap.pic_logo_con;
                textview_name.setText("MatataCon");
                break;
            case DEV_BOT:
                this.getWindow().setBackgroundDrawable(getResources().getDrawable(R.mipmap.pic_bg_bot));
                imgBg=R.mipmap.pic_bg_bot;
                imgStart=R.mipmap.pic_btn_upgrade_7;
                imgStartp=R.mipmap.pic_btn_upgrade_7p;
                imgHelp=R.mipmap.pic_btn_upgrade_9;
                imgHelpp=R.mipmap.pic_btn_upgrade_9p;
                imgLogo=R.mipmap.pic_logo_bot;
                textview_name.setText("MatataBot");
                break;
            default:break;
        }
        imageButton6.setBackground(getResources().getDrawable(imgStart));
        imageButton7.setBackground(getResources().getDrawable(imgHelp));
        imageView1.setImageDrawable(getResources().getDrawable(imgLogo));
        textview_aur1=findViewById(R.id.textview_aur1);
        textview_aur1.setText("v "+AppConst.versionName+"\n"+AppConst.All_RIGHT_TEXT);
        imageButton5.setOnTouchListener( new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    ((ImageButton)view).getBackground().setAlpha(128);
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    ((ImageButton)view).getBackground().setAlpha(255);
                    try{
                        AppConst.mClient=null;
                        run=false;
                        startActivity(new Intent(UpgradeReadyActivity.this,UpgradeActivity.class));//启动MainActivity
                        finish();
                    }catch (Exception e){
                        MLog.td("tjl", String.valueOf(e.getMessage()));
                    }
                }
                return false;
            }
        });
        imageButton6.setOnTouchListener( new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    ((ImageButton)view).getBackground().setAlpha(128);
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    progressBar_au1.setVisibility(View.VISIBLE);
                    ((ImageButton)view).getBackground().setAlpha(255);
                   BlueStartDFU();
                }
                return false;
            }
        });
        imageButton7.setOnTouchListener( new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    ((ImageButton)view).getBackground().setAlpha(128);
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    ((ImageButton)view).getBackground().setAlpha(255);
                    onClickWatchGuideVideo();
                }
                return false;
            }

        });

    }
    private void onClickWatchGuideVideo() {
        try {
            MLog.td("tjl", "--- onClickWatchGuideVideo ---");
            String urlStr = AppConst.VIDEO_URL_YOUTU;
            if (FunctionUtils.isZh(this) == true) {
                urlStr = AppConst.VIDEO_URL_BILIBILI;
            }
            MLog.td("tjl", "onClickWatchGuideVideo --- urlStr = " + urlStr);
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(urlStr);
            intent.setData(content_url);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            MLog.e("tjl", "onClickWatchGuideVideo failed error =  " + e.getCause());
        }
    }
    private void BlueStartDFU()
    {
        AppConst.mClient=null;
        AppConst.mClient = new BluetoothClient(this);
        if(!AppConst.mClient.isBluetoothOpened()) {
            AppConst.mClient.openBluetooth();
        }
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(2000, 2)   // 先扫BLE设备3次，每次3s
                .searchBluetoothClassicDevice(0) // 再扫经典蓝牙5s,在实际工作中没用到经典蓝牙的扫描
                .searchBluetoothLeDevice(2000)      // 再扫BLE设备2s
                .build();
        AppConst.mClient.registerConnectStatusListener(AppConst.DeviceMac, mBleConnectStatusListener);
        MLog.td("tjl" ,"AppConst.mClient.registerConnectStatusListener");
        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)   // 连接如果失败重试3次
                .setConnectTimeout(30000)   // 连接超时30s
                .setServiceDiscoverRetry(3)  // 发现服务如果失败重试3次
                .setServiceDiscoverTimeout(20000)  // 发现服务超时20s
                .build();
        AppConst.mClient.connect(AppConst.DeviceMac, options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile data) {
                MLog.td("tjl" ,"code:"+code);
            }
        });
    }
    private final BleConnectStatusListener mBleConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            if(run==false)return;
            MLog.td("tjl","mac:"+mac+",status:"+status);
            if(status==16)
            {
                byte[] CMD=BOT_DFU_CMD;
                switch (devType)
                {
                    case DEV_OLD:CMD=BOT_DFU_CMD;break;
                    case DEV_CON:CMD=CON_DFU_CMD;break;
                    case DEV_BOT:CMD=BOT_DFU_CMD;break;
                    default:break;
                }
                MLog.td("tjl","send:"+MLog.bytes2hex(CMD));
                AppConst.mClient.write(AppConst.DeviceMac, UUID.fromString(AppConst.UUID_SERVICE), UUID.fromString(AppConst.UUID_WRITE), CMD, new BleWriteResponse() {
                    @Override
                    public void onResponse(int code) {
                        MLog.td("tjl","write,code:"+code);
                        AppConst.mClient.disconnect(AppConst.DeviceMac);
                    }
                });
                if(devType==DEV_CON)
                {
                    Message tempMsg = mHandler.obtainMessage();
                    tempMsg.what = 1000;
                    mHandler.sendMessage(tempMsg);
                }else
                {
                    Message tempMsg = mHandler.obtainMessage();
                    tempMsg.what = 1005;
                    mHandler.sendMessage(tempMsg);
                }
                //AppConst.mClient.disconnect(AppConst.DeviceMac);
            }
            if(status==32)
            {
                AppConst.mClient = null;
            }
        }

    };
}