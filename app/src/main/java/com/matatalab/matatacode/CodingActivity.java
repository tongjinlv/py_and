package com.matatalab.matatacode;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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
import android.widget.Toast;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.beacon.Beacon;
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
import com.matatalab.matatacode.model.FileStorageHelper;
import com.matatalab.matatacode.model.MyBluetooth;
import com.matatalab.matatacode.model.MyQueue;
import com.matatalab.matatacode.model.PublicWay;
import com.matatalab.matatacode.model.Python2Java;
import com.matatalab.matatacode.utils.MLog;
import com.matatalab.matatacode.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CodingActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String BLOCKLY_URL = "file:///android_asset/blockly/index_code2.html?lang=";
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
    public static ImageButton button_ac8;
    private Thread myThread1;
    private boolean first=true,runfrist=true;
    public static WebView webView;
    private String tempfile;
    private Thread my1Thread;
    private boolean pythonRun=false;
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
    @Override
    protected void onUserLeaveHint() {
        exitAction();
        super.onUserLeaveHint();
    }
    private void exitAction() {
        try {
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
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Global.applyNotchAfterPie(this);
        setContentView(R.layout.activity_coding);
        Intent intent=getIntent();
        Host=this;
        name=intent.getStringExtra("name");
        initView(savedInstanceState);
        tempfile =AppConst.runDirPath+"main.py";
        webView = findViewById(R.id.webview1);
        webView.getSettings().setCacheMode(webView.getSettings().LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        File file1 = new File("/data/data/"+getPackageName()+"/web/");
        if(file1.exists())MLog.td("tjl","文件夹存在");
        webView.getSettings().setAppCachePath("/data/data/"+getPackageName()+"/web/");
        webView.loadUrl(BLOCKLY_URL+AppConst.lang);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new AndroidtoJs(),"Android");
        my1Thread=new Thread(){
            public  int a;
            @Override
            public void run() {
                while(run_flag) {
                    try {
                        Thread.sleep(10);
                    } catch (Exception E) {
                    }
                    if(AppConst.pythonRunflag)
                    {
                        mHandler.sendEmptyMessage(10062);
                        AppConst.pythonRunflag=false;
                    }
                }
            }
        };
        my1Thread.start();
        //允许网页使用js
        webView.setWebChromeClient(new WebChromeClient() {//允许有alert弹出框
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            };
            @Override
            public boolean onJsConfirm(WebView view, String url, String message,final JsResult result)
            {
                MLog.td("tjl", "onJsConfirm");
                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(CodingActivity.this);
                normalDialog.setIcon(android.R.drawable.ic_dialog_alert);
                normalDialog.setTitle(getString(R.string.ALERT_TITLE));
                normalDialog.setMessage(message);
                normalDialog.setPositiveButton(getString(R.string.ALERT_BUTTON_OK),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                                return;
                            }
                        });
                normalDialog.setNegativeButton(getString(R.string.ALERT_BUTTON_CANCEL),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                                return;
                            }
                        });
                normalDialog.setCancelable(false);
                AlertDialog dialog = normalDialog.create();
                dialog.show();
                return true;
            }
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,final JsPromptResult result) {
             //   MLog.td("tjl" , "onJsPrompt url=" + url + ",message =[" + message + "], defaultValue=[" + defaultValue + "]");
                if(message.indexOf("type")>-1) {
                    Uri uri = Uri.parse(message);
                    String type = uri.getQueryParameter("type");
                    String msg = uri.getQueryParameter("msg");
                  //  MLog.td("tjl" , "onJsPrompt type=" + type + ",msg =[" + msg + "]");
                    if (type != null) switch (type) {
                        case "runcode":
                            int offset=message.indexOf("msg=");
                            if(offset>-1)offset+=4;
                            String msg1 =message.substring(offset);
                            MLog.td("tjl" ,"runcode:"+AppConst.CODE_HEARD+msg1);
                            Global.WriteFile(tempfile ,AppConst.CODE_HEARD+msg1);
                            mHandler.sendEmptyMessageAtTime(1006,1000);
                            result.confirm("js调用了Android的方法成功啦");
                            return true;
                        case "runcodePy":
                            int offsetpy=message.indexOf("msg=");
                            if(offsetpy>-1)offsetpy+=4;
                            String msgpy =message.substring(offsetpy);
                            MLog.td("tjl" ,"runcode:"+AppConst.CODE_HEARD+msgpy);
                            Global.WriteFile(tempfile ,AppConst.CODE_HEARD+msgpy);
                            mHandler.sendEmptyMessageAtTime(1006,1000);
                            result.confirm("js调用了Android的方法成功啦");
                            return true;
                        case "init":
                            if (first) {
                                File f=new File(AppConst.EXAMPLE_PATH + name + ".txt");
                                if(!f.exists())
                                {
                                    runJsCode("javascript:Code.setWorkSpaceMainBlock()" , null);
                                }else
                                try {
                                    String text = Global.readFileData(AppConst.EXAMPLE_PATH + name + ".txt");
                                    MLog.td("tjl",name+":read:"+text);
                                    text = text.replace("\"" , "\\\"");
                                    String js = "Blockly.Xml.domToWorkspace(Blockly.Xml.textToDom('" + text + "'), Code.workspace)";
                                    runJsCode("javascript:" + js , null);
                                } catch (Exception E) {
                                    MLog.td("tjl" , (E.getMessage()));
                                }
                                first = false;
                            }
                            result.confirm("js调用了Android的方法成功啦");
                            return true;
                        case "codesloaded":
                            result.confirm("js调用了Android的方法成功啦");
                            return true;
                        case "savecode":
                            String temp = AppConst.EXAMPLE_PATH + name + ".txt";
                            MLog.td("tjl" , "save " + msg + "\r\nwrite to" + temp);
                            result.confirm("js调用了Android的方法成功啦");
                            Global.writeFileData(temp , msg);
                            return true;
                        case "stopcode":
                          //  AppConst.cliPython.Stop();
                            AppConst.chaquoPython.Exit();
                            result.confirm("js调用了Android的方法成功啦");
                            return true;
                        case "codeend":
                            result.confirm("js调用了Android的方法成功啦");
                            return true;
                        default:
                            MLog.td("tjl" , "!@!@!@!@@!@!@!@!@!@!@!@1");

                    }
                }else
                {
                    final EditText inputServer = new EditText(CodingActivity.this);
                    inputServer.setText(defaultValue);
                    AlertDialog.Builder builder = new AlertDialog.Builder(CodingActivity.this);
                    builder.setTitle(message).setIcon(android.R.drawable.ic_dialog_alert).setView(inputServer)
                            .setNegativeButton(getString(R.string.ALERT_BUTTON_CANCEL), new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    MLog.td("tjl","内容:"+inputServer.getText().toString());
                                    result.cancel();
                                }
                            });
                    builder.setPositiveButton(getString(R.string.ALERT_BUTTON_OK), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            MLog.td("tjl","内容:"+inputServer.getText().toString());
                            result.confirm(inputServer.getText().toString());
                        }
                    });
                    builder.setCancelable(false);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
                return super.onJsPrompt(view, url, message, defaultValue, result);
            };
            @Override
            public boolean onJsAlert(WebView view, String url, String message,final JsResult result) {
                MLog.td("tjl" , "onJsAlert url:"+url+",message:"+message);
                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(CodingActivity.this);
                normalDialog.setIcon(android.R.drawable.ic_dialog_alert);
                normalDialog.setTitle(getString(R.string.ALERT_TITLE));
                normalDialog.setMessage(message);
                normalDialog.setPositiveButton(getString(R.string.ALERT_BUTTON_OK),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                                return;
                            }
                        });
                normalDialog.setCancelable(false);
                AlertDialog dialog = normalDialog.create();
                dialog.show();
                return true;
            }
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress==100)codesloaded=true;
                MLog.td("tjl", "onProgressChanged="+newProgress);
            }
        });
    }
    public static void runJsCode(final String js, final JsCallback listener){
        MLog.td("tjl", js);
        if (webView != null) {
            webView.post(new Runnable() {
                @Override
                public void run() {
                    webView.evaluateJavascript(js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                          //  MLog.td("tjl", "onReceiveValue == " + s);
                            if(listener!=null)listener.Click(s);
                        }
                    });
                }
            });
        }
    }


    private void initView(@Nullable Bundle savedInstanceState) {
        imageButton_ac1=findViewById(R.id.imageButton_ac1);
        imagebutton_ac2=findViewById(R.id.imagebutton_ac2);
        imagebutton_ac3=findViewById(R.id.imagebutton_ac3);
        imagebutton_ac4=findViewById(R.id.imagebutton_ac4);
        button_ac5=findViewById(R.id.imagebutton_ac5);
        button_ac6=findViewById(R.id.imagebutton_ac6);
        button_ac7=findViewById(R.id.imagebutton_ac7);
        button_ac8=findViewById(R.id.imagebutton_ac8);
        progressBar15=findViewById(R.id.progressBar15);
        button_ac5.setText(this.name);
        Global.setHeightAsWidth(this,imageButton_ac1,10,100*239/533);
        Global.setHeightAsWidth(this,imagebutton_ac2,6,100*208/311);
        Global.setHeightAsWidth(this,imagebutton_ac3,3.8,100*206/195);
        Global.setHeightAsWidth(this,imagebutton_ac4,6,100*208/311);
        Global.setHeightAsWidth(this,button_ac8,10,100*1128/1021);
        Global.setHeightAsHeight(this,button_ac6,25,100*293/981);
        Global.setHeightAsHeight(this,button_ac7,25,100*293/981);
        Global.setHeight(this,button_ac5,3);

        if(AppConst.mClient!=null)
        {
            SetBluetoothico(AppConst.DeviceName);
            AppConst.mClient.registerConnectStatusListener(AppConst.DeviceMac, mBleConnectStatusListener);
        }
        progressBar15.setVisibility(View.INVISIBLE);
        button_ac6.setEnabled(true);
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
                final EditText inputServer = new EditText(CodingActivity.this);
                inputServer.setText(button_ac5.getText());
                AlertDialog.Builder builder = new AlertDialog.Builder(CodingActivity.this);
                builder.setTitle(getString(R.string.ALERT_TITLE)).setIcon(android.R.drawable.ic_dialog_alert).setView(inputServer)
                        .setNegativeButton(getString(R.string.ALERT_BUTTON_CANCEL), null);
                builder.setPositiveButton(getString(R.string.ALERT_BUTTON_OK), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        MLog.td("tjl","内容:"+inputServer.getText().toString());
                        File file = new File(AppConst.EXAMPLE_PATH + name + ".txt");
                        file.renameTo(new File(AppConst.EXAMPLE_PATH + inputServer.getText().toString() + ".txt"));
                        button_ac5.setText(inputServer.getText().toString());
                        CodingActivity.this.name=inputServer.getText().toString();
                    }
                });
                builder.show();
            }
        });
        button_ac6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MLog.td("tjl","button_ac6.down");
                if(AppConst.mClient==null) {
                    if(!Global.gpsIsOpen(CodingActivity.this))
                    {
                        mHandler.sendEmptyMessage(998);
                        return;
                    }
                    if(!Global.blueToothIsOpen())
                    {
                        mHandler.sendEmptyMessage(999);
                        return;
                    }
                    Message tempMsg = new Message();
                    tempMsg.what = 1007;
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
                AlertDialog.Builder builder = new AlertDialog.Builder(CodingActivity.this);
                builder.setTitle(CodingActivity.this.getString(R.string.ALERT_TITLE));
                builder.setSingleChoiceItems(List, 0, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        MLog.td("tjl" ,"选择:"+String.valueOf(which));
                        SelectId=which;
                    }
                });
                builder.setPositiveButton(CodingActivity.this.getString(R.string.ALERT_BUTTON_OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MLog.td("tjl" ,"确认 i="+SelectId);
                        try {
                            String text = Global.readFileData(AppConst.EXAMPLE_PATH + List[SelectId] + ".txt");
                            MLog.td("tjl" , AppConst.EXAMPLE_PATH + List[SelectId] + ".txt"+"text:"+text);
                            text = text.replace("\"" , "\\\"");

//                            String js = "Blockly.Xml.domToWorkspace(Blockly.Xml.textToDom('" + text + "'), Code.workspace)";
                            String js = "Code.setWorkSpace('" + text + "')";
                            runJsCode("javascript:" + js , null);
                        } catch (Exception E) {
                            MLog.td("tjl" , (E.getMessage()));
                        }
                        button_ac7.setBackground(getResources().getDrawable(R.mipmap.pic_code_folder));
                    }
                });
                builder.setNegativeButton(CodingActivity.this.getString(R.string.ALERT_BUTTON_CANCEL),new DialogInterface.OnClickListener() {
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
                    mHandler.sendEmptyMessage(10062);
                    AppConst.pythonRun=false;
                    return;
                }else {
                }
               if(AppConst.ConnectOk)
                {
                    runJsCode("javascript:Code.runPython()", new JsCallback() {
                        public void Click(String b)
                        {
                            MLog.td("tjl","JsCallback:"+b);
                        }
                    });
                }

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
            default:Toast.makeText(CodingActivity.this,
                    view.getId()+"按钮被点击", Toast.LENGTH_SHORT).show();break;
        }

    }
    public void SetBluetoothico(String msg)
    {
        if(msg==null)
        {
            button_ac6.setBackground(getResources().getDrawable(R.mipmap.pic_code_blebreak));
            return;
        }
        if(msg.indexOf("MatataCon")>-1) button_ac6.setBackground(getResources().getDrawable(R.mipmap.pic_code_blelink_con));
        if(msg.indexOf("MatataBot")>-1) button_ac6.setBackground(getResources().getDrawable(R.mipmap.pic_code_blelink_bot));
        if(msg.indexOf("Matalab")>-1) button_ac6.setBackground(getResources().getDrawable(R.mipmap.pic_code_blelink_old));
    }
    public  void Click(View view){
        MLog.td("tjl","aaaaaaaaaaaaaa"+view.getId());
    }
    public  Handler mHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
          //  MLog.td("tjl" , "handleMessage what=" +String.valueOf(msg.what)+"msg="+String.valueOf(msg.obj));
            if(CodingActivity.this==null)return;
            switch (msg.what) {
                case 999:
                final Mdialog mdialog1 = new Mdialog(CodingActivity.this);
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

                    final Mdialog mdialog2 = new Mdialog(CodingActivity.this);
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
                    if(runfrist) {
                        runfrist=false;
                        if (mdialog == null) {

                            mdialog = new Mdialog(CodingActivity.this);
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
                case 10060:
                    button_ac8.setBackground(getResources().getDrawable(R.mipmap.start));
                    break;
                case 10070:
                    final MAlert mdialog = new MAlert(CodingActivity.this);
                    mdialog.setTitle(getString(R.string.ALERT_TITLE));
                    mdialog.setMessage(msg.obj.toString());
                    mdialog.setOk(getString(R.string.ALERT_BUTTON_OK));
                    mdialog.setButtonOkListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppConst.mClient.disconnect(AppConst.DeviceMac);
                            mdialog.dismiss();
                        }
                    });
                    mdialog.setCancelable(false);                //用户可以点击后退键关闭 Dialog
                    mdialog.setCanceledOnTouchOutside(false);   //用户不可以点击外部来关闭 Dialog
                    mdialog.show();
                    break;
                case 10061:button_ac8.setBackground(getResources().getDrawable(R.mipmap.close));break;
                case 10062:button_ac8.setBackground(getResources().getDrawable(R.mipmap.start));break;
                case 10063:
                    SetBluetoothico(AppConst.DeviceName);
                    progressBar15.setVisibility(View.INVISIBLE);
                    button_ac6.setEnabled(true);
                    break;
                case 1007://连接蓝牙
                    AppConst.DeviceName=null;
                    AppConst.mClient=null;
                    AppConst.mClient = new BluetoothClient(CodingActivity.this);
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
                        public void onDeviceFounded(SearchResult device) {//找到设备 可通过manufacture过滤
                           // Beacon beacon = new Beacon(device.scanRecord);
                            // MLog.td("tjl",String.format("beacon for %s\n%s", device.getAddress(), beacon.toString()));
                            if(Global.isMatataDevice(AppConst.BT_APP_NAME_ALL,device.getName())){
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
                        @Override
                        public void onSearchStopped() {//搜索停止
                            MLog.td("tjl","搜索停止："+run_flag);
                            MLog.td("tjl" , "devList.size()=" +devList.size());
                            if(run_flag==false)
                            {
                                AppConst.mClient=null;
                            }
                            if(devList.size()>0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(CodingActivity.this);
                                builder.setTitle(getString(R.string.ALERT_TITLE));
                                Collections.sort(devList, new Global.MapComparatorDesc());
                                final String[] List=new String[devList.size()];
                                SelectId=0;
                                for(int i=0;i<devList.size();i++)
                                {
                                    final Map<String,Object> map=devList.get(i);
                                    String mac=(String)map.get("mac");
                                    List[i]=(String) map.get("name")+"_"+mac.substring(12).replace(":","")+"("+String.valueOf(map.get("rssi"))+")";
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
    private int CheckSupped(byte[] bytes)
    {
        final byte[] header=new byte[]{(byte)0xfe,0x06,0x7e,0x01,0x00,0x00,0x52,(byte)0xc6};
        final byte[] swheader=new byte[]{(byte)0xfe,0x04,(byte)0x88,0x07,(byte)0xf2,(byte)0x8a};

        if(bytes.length==header.length)
        {
            Python2Java python2Java=new Python2Java("");
            byte[] a= python2Java.export(bytes);
            if(a[1]==0x7e)
            {
                MLog.td("tjl","CheckSupped(Hex):"+MLog.bytes2hex(a));
                Boolean devsuppeed =true;
                Message msg=new Message();
                msg.what=10070;
                if(a[3]!=0){return 1;}//小车不支持
                if(a[4]!=0){return 2;}//控制器不支持
                return 0;
            }
        }
        if(bytes.length==swheader.length)
        {
            boolean swerror = true;
            for (int i = 0; i < bytes.length; i++) {
                if (bytes[i] != swheader[i]) swerror = false;
            }
            if (swerror) {
                   return 3;
            }
        }
        return  4;
    }
    private final BleConnectStatusListener mBleConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            MLog.td("tjl","mac:"+mac+",status:"+status);
          if(status==16)
          {
              MyQueue.queue.clear();
              AppConst.ConnectOk=false;

              AppConst.mClient.notify(AppConst.DeviceMac,  UUID.fromString(AppConst.UUID_SERVICE), UUID.fromString(AppConst.UUID_NOTIFY), new BleNotifyResponse() {
                  @Override
                  public void onNotify(UUID service, UUID character, byte[] value) {
                      String s = new String(value);
                      AppConst.onNotify=value;
                      MyQueue.queue.enQueue(value);
                      if(value[0]==(byte)0xfe)MLog.td("tjl","notify(HEX):"+MLog.bytes2hex(value));
                      else MLog.td("tjl","notify(String):"+s);
                  }
                  @Override
                  public void onResponse(int code) {
                      MLog.td("tjl","notify:onResponse");
                  }
              });

              byte[] a=new byte[]{(byte)0xfe,0x07,0x7e,0x02,0x02,0x00,0x00,(byte)0x97,0x77};
              MLog.td("tjl", "AppConst.mClient.write:设置新协议" );
              AppConst.mClient.write(AppConst.DeviceMac, UUID.fromString(AppConst.UUID_SERVICE), UUID.fromString(AppConst.UUID_WRITE), a, new BleWriteResponse() {
                  @Override
                  public void onResponse(int code) {
                      MLog.td("tjl", "write,succeed:" + code);
                  }
              });
              new Thread(){
                  @Override
                  public void run() {
                      int countout=4;
                      Boolean notack=true;
                      while (run_flag)
                      {
                          try{Thread.sleep(1000);}catch (Exception E){}
                          while (!MyQueue.queue.QueueEmpty()) {
                              byte[] r=(byte[])MyQueue.queue.deQueue();
                              MLog.td("tjl","CheckSupped(HEX):"+MLog.bytes2hex(r));
                              int res=CheckSupped(r);
                              Message msg=new Message();
                              msg.what=10070;
                              msg.obj=getString(R.string.DEV_NOT_SUPPORTED);
                              MLog.td("tjl","CheckSupped(res):"+res);
                              switch (res)
                              {
                                  case 0:AppConst.ConnectOk=true;
                                      mHandler.sendEmptyMessage(10063);
                                  return;
                                  case 1:msg.obj=getString(R.string.BOT_NOT_SUPPORTED);notack=false;break;
                                  case 2:msg.obj=getString(R.string.DEV_NOT_SUPPORTED);notack=false;break;
                                  case 3:msg.obj=getString(R.string.CON_MODE_ERROR);notack=false;break;
                                  default:msg.obj = getString(R.string.DEV_NOT_SUPPORTED);break;
                              }
                              if(notack==false)
                              {
                                  mHandler.sendMessage(msg);
                                  return;
                              }
                          }
                          if(countout--<1)
                          {
                              MLog.td("tjl","CheckSupped TimeOut");
                              if(notack) {
                                  Message msg = new Message();
                                  msg.what = 10070;
                                  msg.obj = getString(R.string.DEV_NOT_SUPPORTED);
                                  mHandler.sendMessage(msg);
                              }
                              return;
                          }
                      }
                  }
              }.start();
          }
          if(status==32)
          {
              AppConst.ConnectOk=false;
              AppConst.chaquoPython.Exit();
              mHandler.sendEmptyMessage(10062);
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
            startActivity(new Intent(CodingActivity.this,FileActivity.class));
            finish();
            return;
        }
        Bitmap bitmap= captureScreen(CodingActivity.this);
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
                startActivity(new Intent(CodingActivity.this,FileActivity.class));
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
        webView.destroy();
        run_flag=false;
        if(AppConst.mClient!=null)AppConst.mClient.unregisterConnectStatusListener(AppConst.DeviceMac,mBleConnectStatusListener);
        super.onDestroy();
    }
}


