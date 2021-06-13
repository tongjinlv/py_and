package com.matatalab.matatacode;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.matatalab.matatacode.interfaces.MainViewInterface;
import com.matatalab.matatacode.model.MyQueue;
import com.matatalab.matatacode.utils.MLog;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.List;

public class FristActivity extends RxAppCompatActivity implements MainViewInterface {

    private static final String TAG = MainActivity.class.getSimpleName();
    /**
     * 保存页面信息
     */
    private static final String EXTRA_SELECTED_ID = "SELECTED_INDEX";

    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;
    private static final int REQUEST_CODE_ACCESS_COARSE_STORAGE = 2;

    private TextView textview_af12,textview_af18,textview_af19;
    private ImageButton imagebutton1;
    private ImageButton imagebutton2;
    private int exitcount=0;
    private RelativeLayout mRlLoading;
    private void onClickWatchGuideVideo() {
        try {
            Global.NetRecord("matatacode update");
            String urlStr = AppConst.APP_DOWNLOAD_URL;
            MLog.td("tjl", "onClickWatchGuideVideo --- urlStr = " + urlStr);
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(urlStr);
            intent.setData(content_url);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            MLog.e("tjl", "onClickWatchGuideVideo failed error =  " + e.getCause());
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Global.applyNotchAfterPie(this);
        setContentView(R.layout.activity_frist);
        initView(savedInstanceState);
        checkGps();
        exitcount=0;
        checkStorageWrite();
        AppConst.bluetoothAdapter=null;
        if(AppConst.mClient!=null) {
            AppConst.mClient.disconnect(AppConst.DeviceMac);
            AppConst.mClient = null;
            AppConst.DeviceName=null;
            MLog.e("tjl", "释放对象 =  " + "AppConst.myBluetooth ");
        }
    }
    private void showTextView(String title,int msg)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final TextView tv1 = new TextView(this);
        tv1.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv1.setText(Html.fromHtml(getString(msg)));
        tv1.setTextSize(14);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.addView(tv1);
        setMargins(tv1,10, 10, 10, 10);
        builder.setView(linearLayout);
        builder.setMessage(title);
        builder.show();
    }
    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    @SuppressLint("CutPasteId")
    private void initView(@Nullable Bundle savedInstanceState) {
        imagebutton1=findViewById(R.id.imagebutton1);
        imagebutton2 = findViewById(R.id.imagebutton2);
        textview_af12=findViewById(R.id.textview_af12);
        Global.setHeightAsWidth(this,imagebutton1,20,100*393/371);
        Global.setHeightAsWidth(this,imagebutton2,20,100*152/533);
        textview_af12.setText("v "+AppConst.versionName+"\n"+AppConst.All_RIGHT_TEXT);
        textview_af18=findViewById(R.id.textview_af18);
        textview_af19=findViewById(R.id.textview_af19);

        if(AppConst.lang.indexOf("zh-")>-1)
        {
            textview_af19.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            textview_af18.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            textview_af19.setText(getString(R.string.privacy_policy_title));
            textview_af18.setText(getString(R.string.user_agreement_title));
            textview_af19.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTextView(getString(R.string.privacy_policy_title),R.string.privacy_policy);
                }
            });
            textview_af18.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTextView(getString(R.string.user_agreement_title),R.string.user_agreement);
                }
            });
        }
        imagebutton1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    //重新设置按下时的背景图片
                    ((ImageButton)view).getBackground().setAlpha(128);
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    //再修改为抬起时的正常图片
                    ((ImageButton)view).getBackground().setAlpha(255);
                    try{
                        if(AppConst.NeedUpdate)
                        {
                            AppConst.NeedUpdate=false;
                            final Mdialog mdialog = new Mdialog(FristActivity.this);
                            mdialog.setTitle(getString(R.string.ALERT_TITLE));
                            mdialog.setMessage("findnewversion");
                            mdialog.setBa(getString(R.string.ALERT_BUTTON_CANCEL));
                            mdialog.setBb(getString(R.string.ALERT_BUTTON_OK));
                            mdialog.setOnButtonAListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mdialog.isShowing()) {
                                        mdialog.dismiss();
                                    }
                                }
                            });
                            mdialog.setOnButtonBListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (mdialog != null && mdialog.isShowing()) {
                                        onClickWatchGuideVideo();
                                        mdialog.dismiss();
                                    }
                                }
                            });
                            mdialog.setCancelable(false);                //用户可以点击后退键关闭 Dialog
                            mdialog.setCanceledOnTouchOutside(false);   //用户不可以点击外部来关闭 Dialog
                            mdialog.show();
                        }else
                        {
                            startActivity(new Intent(FristActivity.this,FileActivity.class));//启动MainActivity
                            finish();
                        }

                    }catch (Exception e){
                        MLog.td("tjl", String.valueOf(e.getMessage()));
                    }
                }
                return false;
            }
        });
        imagebutton2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    //重新设置按下时的背景图片
                    ((ImageButton)view).getBackground().setAlpha(128);
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    //再修改为抬起时的正常图片
                    ((ImageButton)view).getBackground().setAlpha(255);
                    try{
                        MLog.td("tjl", "UpgradeActivity start");
                        if(AppConst.NeedUpdate)
                        {
                            AppConst.NeedUpdate=false;
                            final Mdialog mdialog = new Mdialog(FristActivity.this);
                            mdialog.setTitle(getString(R.string.ALERT_TITLE));
                            mdialog.setMessage("findnewversion");
                            mdialog.setBa(getString(R.string.ALERT_BUTTON_CANCEL));
                            mdialog.setBb(getString(R.string.ALERT_BUTTON_OK));
                            mdialog.setOnButtonAListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mdialog.isShowing()) {
                                        mdialog.dismiss();
                                    }
                                }
                            });
                            mdialog.setOnButtonBListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (mdialog != null && mdialog.isShowing()) {
                                        onClickWatchGuideVideo();
                                        mdialog.dismiss();
                                    }
                                }
                            });
                            mdialog.setCancelable(false);                //用户可以点击后退键关闭 Dialog
                            mdialog.setCanceledOnTouchOutside(false);   //用户不可以点击外部来关闭 Dialog
                            mdialog.show();
                        }else {
                            startActivity(new Intent(FristActivity.this, UpgradeActivity.class));//启动MainActivity
                            finish();
                        }
                        MLog.td("tjl", "UpgradeActivity end");
                    }catch (Exception e){
                        MLog.td("tjl", String.valueOf(e.getMessage()));
                    }
                }
                return false;
            }
        });

    }
    public void killAppProcess()
    {
        //注意：不能先杀掉主进程，否则逻辑代码无法继续执行，需先杀掉相关进程最后杀掉主进程
        ActivityManager mActivityManager = (ActivityManager)FristActivity.this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> mList = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : mList)
        {
            if (runningAppProcessInfo.pid != android.os.Process.myPid())
            {
                android.os.Process.killProcess(runningAppProcessInfo.pid);
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
    public void onBackPressed() {
        //  CrashReport.testJavaCrash();

        MLog.td("tjl" , "onBackPressed()");
        //if(exitcount==0) ToastUtils.show(getString(R.string.exitshow));
        if(exitcount==1)
        {
            moveTaskToBack(true);
            exitcount=0;
            return;
        }
        exitcount++;
        return;
    }



    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }


    /**
     * 开启位置权限
     */
    private void checkGps() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ACCESS_COARSE_LOCATION);
            }
        }
    }

    /**
     * 开启SD卡写权限
     */
    private void checkStorageWrite() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ACCESS_COARSE_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               // ToastUtils.show(R.string.location_premission_granted);
            } else {
              //  ToastUtils.show(R.string.location_premission_denied);
            }
        } else if (requestCode == REQUEST_CODE_ACCESS_COARSE_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void setMainButtonEnable(boolean enable) {
        MLog.d(TAG, "setMainButtonEnable ---  enable = " + enable);
    }

    @Override
    public void showLoading(boolean isShow) {
        MLog.d(TAG, "showLoading --- isShow = " + isShow);
        if (mRlLoading != null) {
            if (isShow == true) {
                mRlLoading.setVisibility(View.VISIBLE);
            } else {
                mRlLoading.setVisibility(View.GONE);
            }
        }
    }
}
