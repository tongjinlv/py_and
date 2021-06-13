package com.matatalab.matatacode;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.matatalab.matatacode.model.MyQueue;
import com.matatalab.matatacode.model.Python2Java;
import com.matatalab.matatacode.utils.MLog;
import com.matatalab.matatacode.utils.ToastUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BleTestActivity extends AppCompatActivity implements View.OnClickListener{
    private List<Map<String,Object>> devList;
    private String name;
    private boolean run_flag=true;
    private ImageButton imageButton_ac1;
    private ImageButton imagebutton_ac2;
    private ImageButton imagebutton_ac3;
    private ImageButton imagebutton_ac4;
    private Mdialog mdialog;
    private int SelectId=0;
    private boolean codesloaded=false;
    private Button button_ac5;
    private Button button_ac6;
    private Button button_ac7;
    public static Context Host;
    public static Button button_ac8;
    private Thread myThread1;
    private TextView textView3;
    private boolean first=true,runfrist=true;
    private String tempfile;
    private String debug="";
    private int timespace=20;
    Map<String, String> map = new HashMap<String, String>();
    private ProgressBar progressBar15;
    public interface JsCallback{
        public void Click(String b);
    }

    private Bitmap captureScreen(Activity context) {
        View cv = context.getWindow().getDecorView();

        cv.setDrawingCacheEnabled(true);
        cv.buildDrawingCache();
        Bitmap bmp = cv.getDrawingCache();
        if (bmp == null) {
            return null;
        }

        bmp.setHasAlpha(false);
        bmp.prepareToDraw();
        return bmp;
    }
    public class AndroidtoJs extends Object {
        @JavascriptInterface
        public void hello(String msg) {
            MLog.td("tjl","JS调用了Android的hello方法");
        }
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Global.applyNotchAfterPie(this);
        setContentView(R.layout.activity_bletest);
        Intent intent=getIntent();
        Host=this;
        name=intent.getStringExtra("name");
        initView(savedInstanceState);
        tempfile =AppConst.runDirPath+"main.py";
        progressBar15=findViewById(R.id.progressBar15);
        progressBar15.setVisibility(View.INVISIBLE);

        File file1 = new File("/data/data/"+getPackageName()+"/web/");
        if(file1.exists())MLog.td("tjl","文件夹存在");
    }
    public static void runJsCode(final String js, final JsCallback listener){
        MLog.td("tjl", js);
    }
    private void initView(@Nullable Bundle savedInstanceState) {
        imageButton_ac1=findViewById(R.id.imageButton_ac1);
        imagebutton_ac2=findViewById(R.id.imagebutton_ac2);
        imagebutton_ac3=findViewById(R.id.imagebutton_ac3);
        imagebutton_ac4=findViewById(R.id.imagebutton_ac4);
        textView3=findViewById(R.id.textView3);
        button_ac5=findViewById(R.id.imagebutton_ac5);
        button_ac6=findViewById(R.id.imagebutton_ac6);
        button_ac7=findViewById(R.id.imagebutton_ac7);
        button_ac8=findViewById(R.id.imagebutton_ac8);
        button_ac5.setText("20");
        Global.setHeightAsWidth(this,imageButton_ac1,10,40);
        Global.setHeightAsWidth(this,imagebutton_ac2,6,65);
        Global.setHeightAsWidth(this,imagebutton_ac4,6,65);
        Global.setHeightAsWidth(this,imagebutton_ac3,5,77);
        Global.setHeight(this,button_ac5,3);
        button_ac6.setEnabled(true);
        if(AppConst.mClient!=null)
        {
            SetBluetoothico(AppConst.DeviceName);
            AppConst.mClient.registerConnectStatusListener(AppConst.DeviceMac, mBleConnectStatusListener);
        }
        imageButton_ac1.setOnTouchListener( new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    ((ImageButton)view).getBackground().setAlpha(128);
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    ((ImageButton)view).getBackground().setAlpha(255);
                    savecode();
                }
                return false;
            }

        });

        imagebutton_ac2.setOnTouchListener( new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    ((ImageButton)view).getBackground().setAlpha(128);
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    ((ImageButton)view).getBackground().setAlpha(255);
                    runJsCode("javascript:Code.Undo()",null);
                }
                return false;
            }

        });
        imagebutton_ac3.setOnTouchListener( new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    ((ImageButton)view).getBackground().setAlpha(128);
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    ((ImageButton)view).getBackground().setAlpha(255);
                    runJsCode("javascript:Code.discard()",null);
                }
                return false;
            }

        });
        imagebutton_ac4.setOnTouchListener( new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    ((ImageButton)view).getBackground().setAlpha(128);
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    ((ImageButton)view).getBackground().setAlpha(255);
                    runJsCode("javascript:Code.Redo()",null);
                }
                return false;
            }
        });

        button_ac5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MLog.td("tjl", "button_ac5.down");
                final EditText inputServer = new EditText(BleTestActivity.this);
                inputServer.setText(button_ac5.getText());
                AlertDialog.Builder builder = new AlertDialog.Builder(BleTestActivity.this);
                builder.setTitle(getString(R.string.ALERT_TITLE)).setIcon(android.R.drawable.ic_dialog_alert).setView(inputServer)
                        .setNegativeButton(getString(R.string.ALERT_BUTTON_CANCEL), null);
                builder.setPositiveButton(getString(R.string.ALERT_BUTTON_OK), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        MLog.td("tjl","内容:"+inputServer.getText().toString());
                        File file = new File(AppConst.EXAMPLE_PATH + name + ".txt");
                        file.renameTo(new File(AppConst.EXAMPLE_PATH + inputServer.getText().toString() + ".txt"));
                        button_ac5.setText(inputServer.getText().toString());
                        BleTestActivity.this.name=inputServer.getText().toString();
                    }
                });
                builder.show();
            }
        });
        button_ac6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(AppConst.mClient==null) {
                    Message tempMsg = new Message();
                    tempMsg.what = 1007;
                    String t=(String)button_ac5.getText();
                    timespace=Integer.parseInt(t);
                    mHandler.sendMessage(tempMsg);
                    progressBar15.setVisibility(View.VISIBLE);
                    button_ac6.setBackground(getResources().getDrawable(R.mipmap.pic_code_blebreak_p));
                    button_ac6.setEnabled(false);
                }else
                {
                    AppConst.mClient.disconnect(AppConst.DeviceMac);
                }
            }
        });
        button_ac7.setOnClickListener(new View.OnClickListener() {
            int SelectId=0;
            String[] lLists;
            String[] List;
            public void onClick(View v) {
                MLog.td("tjl", "button_ac7.down");
                button_ac7.setBackground(getResources().getDrawable(R.mipmap.pic_code_folder_p));
                File file=new File(AppConst.EXAMPLE_PATH);
                lLists=file.list();
                List=new String[Global.TextEndCount(lLists)];
                int n=0;
                for(int i=0;i<lLists.length;i++)
                {
                    if(lLists[i].toLowerCase().endsWith(".txt")) List[n++]=lLists[i].replace(".txt","");
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(BleTestActivity.this);
                builder.setTitle(BleTestActivity.this.getString(R.string.ALERT_TITLE));
                builder.setSingleChoiceItems(List, 0, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        MLog.td("tjl" ,"选择:"+String.valueOf(which));
                        SelectId=which;
                    }
                });
                builder.setPositiveButton(BleTestActivity.this.getString(R.string.ALERT_BUTTON_OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MLog.td("tjl" ,"确认 i="+SelectId);
                        try {
                            String text = Global.readFileData(AppConst.EXAMPLE_PATH + List[SelectId] + ".txt");
                            MLog.td("tjl" , AppConst.EXAMPLE_PATH + List[SelectId] + ".txt"+"text:"+text);
                            text = text.replace("\"" , "\\\"");
                            String js = "Blockly.Xml.domToWorkspace(Blockly.Xml.textToDom('" + text + "'), Code.workspace)";
                            runJsCode("javascript:" + js , null);
                        } catch (Exception E) {
                            MLog.td("tjl" , (E.getMessage()));
                        }
                        button_ac7.setBackground(getResources().getDrawable(R.mipmap.pic_code_folder));
                    }
                });
                builder.setNegativeButton(BleTestActivity.this.getString(R.string.ALERT_BUTTON_CANCEL),new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i){
                        button_ac7.setBackground(getResources().getDrawable(R.mipmap.pic_code_folder));
                    }
                });
                builder.show();
            }
        });

        button_ac8.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MLog.td("tjl", "button_ac8.down");
                runfrist=true;
                if(AppConst.mClient==null) {
                    mHandler.sendEmptyMessageDelayed(1001, 1000);
                    return;
                }
                if(AppConst.pythonRun==true)
                {
                    byte[] a=new byte[]{(byte)0xfe,0x03,(byte)0x84,(byte)0x99,0x50};
                    AppConst.mClient.write(AppConst.DeviceMac, UUID.fromString(AppConst.UUID_SERVICE), UUID.fromString(AppConst.UUID_WRITE), a, new BleWriteResponse() {
                        @Override
                        public void onResponse(int code) {
                            MLog.td("tjl", "send 0x84:" + code);
                        }
                    });
                    AppConst.chaquoPython.Exit();
                    Pyexit();
                    AppConst.pythonRun=false;
                    return;
                }
               if(true)
                {
                    final MAlert mdialog = new MAlert(BleTestActivity.this);
                    mdialog.setTitle(getString(R.string.ALERT_TITLE));
                    mdialog.setOk(getString(R.string.ALERT_BUTTON_OK));
                    mdialog.setButtonOkListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mdialog.dismiss();
                        }
                    });
                    mdialog.setCancelable(true);                //用户可以点击后退键关闭 Dialog
                    mdialog.setCanceledOnTouchOutside(true);   //用户不可以点击外部来关闭 Dialog
                    mdialog.show();
                }else
                runJsCode("javascript:Code.runPython()", new JsCallback() {
                    public void Click(String b)
                    {
                        MLog.td("tjl","JsCallback:"+b);
                    }
                });
            }
        });
        button_ac8.setOnTouchListener( new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                  //  ((Button)view).getBackground().setAlpha(128);
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                   // ((Button)view).getBackground().setAlpha(255);
                }
                return false;
            }

        });

    }
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageButton_ac1:break;
            default:Toast.makeText(BleTestActivity.this,
                    view.getId()+"按钮被点击", Toast.LENGTH_SHORT).show();break;
        }

    }
    public void SetBluetoothico(String msg)
    {
        if(msg==null)return;
        if(msg.indexOf("MatataCon")>-1) button_ac6.setBackground(getResources().getDrawable(R.mipmap.pic_code_blelink_con));
        if(msg.indexOf("MatataBot")>-1) button_ac6.setBackground(getResources().getDrawable(R.mipmap.pic_code_blelink_bot));
        if(msg.indexOf("Matalab")>-1) button_ac6.setBackground(getResources().getDrawable(R.mipmap.pic_code_blelink_old));
    }
    public  void Click(View view){
        MLog.td("tjl","aaaaaaaaaaaaaa"+view.getId());
    }
    public static void Pyexit()
    {
        button_ac8.setBackground(Host.getResources().getDrawable(R.mipmap.start));
    }
    private void debug(String msg)
    {
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy年MM月dd日 HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        debug=str+":"+msg+"\r\n"+debug;
        if(debug.split("\r").length>50)debug="";
        mHandler.sendEmptyMessageDelayed(52,52);
    }
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
          //  MLog.td("tjl" , "handleMessage what=" +String.valueOf(msg.what)+"msg="+String.valueOf(msg.obj));
            switch (msg.what) {
                case 52:textView3.setText(debug);break;
                case 1001:
                    if(runfrist) {
                        runfrist=false;
                        if (mdialog == null) {
                            mdialog = new Mdialog(BleTestActivity.this);
                            mdialog.setTitle(getString(R.string.ALERT_TITLE));
                            mdialog.setMessage(getString(R.string.ALERT_MESSAGE_CODE_LINK_BEFORE_RUN));
                            mdialog.setBa(getString(R.string.ALERT_BUTTON_CANCEL));
                            mdialog.setBb(getString(R.string.ALERT_BUTTON_LINK));
                            mdialog.setOnButtonAListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mdialog.isShowing()) {
                                        MLog.td("tjl" , "选择了取消链接");
                                        mdialog.dismiss();
                                        mdialog = null;
                                    }
                                }
                            });
                            mdialog.setOnButtonBListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mdialog != null && mdialog.isShowing()) {
                                        if(AppConst.mClient==null) {
                                            Message tempMsg = new Message();
                                            tempMsg.what = 1007;
                                            mHandler.sendMessage(tempMsg);
                                            progressBar15.setVisibility(View.VISIBLE);
                                            button_ac6.setBackground(getResources().getDrawable(R.mipmap.pic_code_blebreak_p));
                                            button_ac6.setEnabled(false);
                                        }
                                        mdialog.dismiss();
                                        mdialog = null;
                                    }
                                }
                            });
                            mdialog.setCancelable(false);                //用户可以点击后退键关闭 Dialog
                            mdialog.setCanceledOnTouchOutside(false);   //用户不可以点击外部来关闭 Dialog
                            mdialog.show();
                        }
                    }
                    break;
                case 1006:
                    button_ac8.setBackground(getResources().getDrawable(R.mipmap.close));
                    myThread1=new Thread(){
                        @Override
                        public void run() {
                            AppConst.chaquoPython.Start();
                            MLog.td("tjl","运行完毕");
                        }
                    };
                    myThread1.start();
                    break;
                case 10061:button_ac8.setBackground(getResources().getDrawable(R.mipmap.close));break;
                case 10062:button_ac8.setBackground(getResources().getDrawable(R.mipmap.start));break;
                case 1007://连接蓝牙
                    AppConst.mClient=null;
                    AppConst.mClient = new BluetoothClient(BleTestActivity.this);
                    if(!AppConst.mClient.isBluetoothOpened()) {
                        AppConst.mClient.openBluetooth();
                    }
                    SearchRequest request = new SearchRequest.Builder()
                            .searchBluetoothLeDevice(2000, 2)   // 先扫BLE设备3次，每次3s
                            .searchBluetoothClassicDevice(0) // 再扫经典蓝牙5s,在实际工作中没用到经典蓝牙的扫描
                            .searchBluetoothLeDevice(2000)      // 再扫BLE设备2s
                            .build();
                    AppConst.mClient.search(request, new SearchResponse() {
                        @Override
                        public void onSearchStarted() {//开始搜素
                            MLog.td("tjl","开始搜素");
                            devList = new ArrayList<Map<String,Object>>();
                        }
                        @Override
                        public void onDeviceFounded(SearchResult device) {
                            if(device.getName().length()>6&&device.rssi>-80)
                            {
                                debug("name:"+device.getName()+",mac:"+device.getAddress());//找到设备 可通过manufacture过滤
                               // Beacon beacon = new Beacon(device.scanRecord);
                                // MLog.td("tjl",String.format("beacon for %s\n%s", device.getAddress(), beacon.toString()));
                                if(Global.isMatataDevice(AppConst.BT_APP_NAME_ALL,device.getName()));
                                {
                                    MLog.td("tjl", "Name:" + device.getName() + ",Mac:" + device.getAddress() + ",Rssi:" + device.rssi);
                                    Map<String,Object> map1 = new HashMap<String,Object>();
                                    map1.put("name" , device.getName());
                                    map1.put("rssi" , device.rssi);
                                    if(device.rssi>-100&&device.rssi<0) {
                                        map1.put("mac", String.valueOf(device.getAddress()));
                                        devList.add(map1);
                                        devList = Global.removeRepeatMapByKey(devList, "mac");
                                    }
                                }
                            }
                        }
                        @Override
                        public void onSearchStopped() {//搜索停止
                            MLog.td("tjl","搜索停止");
                            MLog.td("tjl" , "devList.size()=" +devList.size());
                            if(devList.size()>0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(BleTestActivity.this);
                                builder.setTitle(getString(R.string.ALERT_TITLE));
                                final String[] List=new String[devList.size()];
                                SelectId=0;
                                for(int i=0;i<devList.size();i++)

                                {
                                    final Map<String,Object> map=devList.get(i);
                                    List[i]=(String) map.get("name")+"("+String.valueOf(map.get("rssi"))+")";
                                }
                                builder.setSingleChoiceItems(List, 0, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        MLog.td("tjl" ,"选择:"+String.valueOf(which));
                                        SelectId=which;
                                    }
                                });
                                builder.setPositiveButton(getString(R.string.ALERT_BUTTON_OK), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        MLog.td("tjl" ,"选中 i="+SelectId);
                                        Map<String,Object> map=devList.get(SelectId);
                                        AppConst.DeviceName=(String)map.get("name");
                                        AppConst.DeviceMac=(String)map.get("mac");
                                        BleConnectOptions options = new BleConnectOptions.Builder()
                                                .setConnectRetry(3)   // 连接如果失败重试3次
                                                .setConnectTimeout(12000)   // 连接超时30s
                                                .setServiceDiscoverRetry(3)  // 发现服务如果失败重试3次
                                                .setServiceDiscoverTimeout(10000)  // 发现服务超时20s
                                                .build();
                                       AppConst.mClient.registerConnectStatusListener(AppConst.DeviceMac, mBleConnectStatusListener);
                                       AppConst.mClient.connect((String)map.get("mac"), options, new BleConnectResponse() {
                                            @Override
                                            public void onResponse(int code, BleGattProfile data) {
                                                MLog.td("tjl" ,"code:"+code);
                                            }
                                        });
                                    }
                                });
                                builder.setNegativeButton(getString(R.string.ALERT_BUTTON_CANCEL),new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i){
                                        button_ac6.setEnabled(true);
                                        progressBar15.setVisibility(View.INVISIBLE);
                                        button_ac6.setBackground(getResources().getDrawable(R.mipmap.pic_code_blebreak));
                                        AppConst.mClient=null;
                                    }
                                });
                                builder.setCancelable(false);
                                builder.show();
                            }else
                            {
                                ToastUtils.show(getString(R.string.ALERT_MESSAGE_BLE_FIND_NOTHING));
                                AppConst.mClient=null;
                                button_ac6.setEnabled(true);
                                progressBar15.setVisibility(View.INVISIBLE);
                                button_ac6.setBackground(getResources().getDrawable(R.mipmap.pic_code_blebreak));
                            }
                        }
                        @Override
                        public void onSearchCanceled() {//搜索取消
                            MLog.td("tjl","搜索取消");
                        }
                    });
                    break;
                default:break;
            }
        }
    };
    private void CheckSupped(byte[] bytes)
    {
        final byte[] header=new byte[]{(byte)0xfe,0x06,0x7e,0x01,0x00,0x00,0x52,(byte)0xc6};
        if(bytes.length==header.length)
        {
            Python2Java python2Java=new Python2Java("");
            byte[] a= python2Java.export(bytes);
            if(a[1]==0x7e)
            {
            }
        }
    }
    private int sendcount=0;
    private int sendokcount=0;
    private int revicecount=0;

    private final BleConnectStatusListener mBleConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            MLog.td("tjl","mac:"+mac+",status:"+status);
          if(status==16)
          {
              sendcount=0;
              sendokcount=0;
              revicecount=0;
              run_flag=true;
              SetBluetoothico(AppConst.DeviceName);
              button_ac6.setEnabled(true);
              progressBar15.setVisibility(View.INVISIBLE);
              Python2Java python2Java=new Python2Java("");
              AppConst.mClient.notify(AppConst.DeviceMac,  UUID.fromString(AppConst.UUID_SERVICE), UUID.fromString(AppConst.UUID_NOTIFY), new BleNotifyResponse() {
                  @Override
                  public void onNotify(UUID service, UUID character, byte[] value) {
                      revicecount++;
                  }
                  @Override
                  public void onResponse(int code) {
                      MLog.td("tjl","notify:onResponse");
                  }
              });
              Thread myThread1=new Thread(){
                  @Override
                  public void run() {
                      while(run_flag)
                      {
                          byte[] a=new byte[]{(byte)0xfe,0x07,0x7e,0x02,0x02,0x00,0x00,(byte)0x97,0x77};
                          debug("sendcount:"+sendcount+"sendokcount:"+sendokcount+"revicecount:"+revicecount+"error:"+(sendcount-revicecount));
                          sendcount++;
                          AppConst.mClient.write(AppConst.DeviceMac, UUID.fromString(AppConst.UUID_SERVICE), UUID.fromString(AppConst.UUID_WRITE), a, new BleWriteResponse() {
                              @Override
                              public void onResponse(int code) {
                                  sendokcount++;
                              }
                          });
                          try
                          {
                              Thread.sleep(timespace);
                          }catch (Exception E){}
                      }
                  }
              };
              myThread1.start();

          }
          if(status==32)
          {
              run_flag=false;
              AppConst.chaquoPython.Exit();
              Pyexit();
              mHandler.sendEmptyMessageDelayed(10062,10);
              button_ac6.setEnabled(true);
              progressBar15.setVisibility(View.INVISIBLE);
              button_ac6.setBackground(getResources().getDrawable(R.mipmap.pic_code_blebreak));
              try {
                  AppConst.mClient.unregisterConnectStatusListener(AppConst.DeviceMac, mBleConnectStatusListener);
                  AppConst.mClient.unnotify(AppConst.DeviceMac,  UUID.fromString(AppConst.UUID_SERVICE), UUID.fromString(AppConst.UUID_NOTIFY), new BleUnnotifyResponse() {
                      @Override
                      public void onResponse(int code) {
                          MLog.td("tjl","notify:unnotify");
                      }
                  });
              }catch (Exception E){}
              AppConst.mClient=null;
          }
        }

    };
    public static void Alert(String msg)
    {
        runJsCode("javascript:alert('"+msg+"')",null);
    }
    public void savecode()
    {
        if(codesloaded==false)
        {
            startActivity(new Intent(BleTestActivity.this,FileActivity.class));
            finish();
            return;
        }
        Bitmap bitmap= captureScreen(BleTestActivity.this);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int cropWidth=h*4/5;
        if (bitmap != null)
        {
            Bitmap _icon=Bitmap.createBitmap(bitmap, w / 4, h/5, cropWidth, cropWidth, null, false);
            Global.savepic(_icon,AppConst.EXAMPLE_PATH + name+"_icon.jpg");
            Global.savepic(bitmap,AppConst.EXAMPLE_PATH + name+"_pic.jpg");
        }
        runJsCode("javascript:Code.getCode()", new JsCallback() {
            public void Click(String b)
            {
                b=b.replace("\\\\","\\");
                b=b.replace("\"","");
                MLog.td("tjl","JsCallback:"+b);
                String text=Global.hexStr2Str(b);
                String temp = AppConst.EXAMPLE_PATH + name + ".txt";
                Global.writeFileData(temp , text);
                MLog.td("tjl","写文件:"+temp);
                MLog.td("tjl","解析:"+text);
                startActivity(new Intent(BleTestActivity.this,FileActivity.class));
                finish();

            }
        });
    }
    @Override
    public void onBackPressed() {
        MLog.td("tjl" , "onBackPressed()");
        //if(AppConst.bluetoothGatt!=null)AppConst.bluetoothGatt.disconnect();
        savecode();
    }
    @Override
    protected void onDestroy() {
        MLog.td("tjl" , "onDestroy()");
        run_flag=false;
        if(AppConst.mClient!=null)AppConst.mClient.unregisterConnectStatusListener(AppConst.DeviceMac,mBleConnectStatusListener);
        super.onDestroy();
    }
}


