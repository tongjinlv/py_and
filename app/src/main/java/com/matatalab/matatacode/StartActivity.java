package com.matatalab.matatacode;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.matatalab.matatacode.model.AssetsCopyer;
import com.matatalab.matatacode.model.ChaquoPython;
import com.matatalab.matatacode.model.FileStorageHelper;
import com.matatalab.matatacode.utils.MLog;
import com.matatalab.matatacode.utils.ToastUtils;

import java.io.File;
import java.net.URL;
import java.util.Locale;

public class StartActivity extends AppCompatActivity {
    private TextView textview_as1;
    private Thread myThread1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Global.applyNotchAfterPie(this);
        setContentView(R.layout.activity_start);
        //setLang(Locale.ENGLISH);
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        String local = Locale.getDefault().toString();
        String country =getResources().getConfiguration().locale.getCountry();
        MLog.td("tjl","tjCountry: language:"+ language+",local:"+local+",country:"+country);
        AppConst.lang="en";
        if (local.indexOf("zh_")>-1)AppConst.lang="zh-hans";//中文
        else if (language.indexOf("zh")>-1)AppConst.lang="zh-hans";//中文
        else if(language.indexOf("en")>-1)AppConst.lang="en";//英文
        else if(language.indexOf("fr")>-1)AppConst.lang="fr";//英文
        else if(language.indexOf("de")>-1)AppConst.lang="de";//德语
        else if(language.indexOf("es")>-1)AppConst.lang="es";//西班牙
        else if(language.indexOf("pt")>-1)AppConst.lang="pt-pt";//葡萄牙
        else if(language.indexOf("pt")>-1)AppConst.lang="pt-pt";//葡萄牙
        else if(language.indexOf("br")>-1)AppConst.lang="pt-br";//葡语（巴西）
        else if(language.indexOf("it")>-1)AppConst.lang="it";//意大利
        else if(language.indexOf("ko")>-1)AppConst.lang="ko";//韩国
        else if(language.indexOf("ja")>-1)AppConst.lang="ja";//日语
        else if(language.indexOf("tr")>-1)AppConst.lang="tr";//土耳其
        else if(language.indexOf("uk")>-1)AppConst.lang="uk";//乌克兰
        else if(language.indexOf("ru")>-1)AppConst.lang="ru";//俄国
        else if(language.indexOf("th")>-1)AppConst.lang="th";//泰国

        MLog.td("tjl","language blockly:"+AppConst.lang);
        MLog.td("tjl","开发公司:"+this.getString(R.string.ALERT_TITLE));
        AppConst.EXAMPLE_PATH="/data/data/"+getPackageName()+"/text/";
        MLog.td("tjl","EXAMPLE_PATH="+AppConst.EXAMPLE_PATH);

        myThread1=new Thread(){
            @Override
            public void run() {
                try
                {
                    PackageManager packageManager = getPackageManager();
                    PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
                    String version = packInfo.versionName;
                    AppConst.versionName=version;
                }catch (Exception E){AppConst.versionName="01.01.01";}
                try {
                    //"http://dm.trtos.com/php/value.php?name=matatacode&value=2.1.1"
                    URL url = new URL("http://dm.trtos.com/php/value.php?name=matacode");
                    String resultData=Global.readNetValue("matacode");
                    Global.NetRecord("matatacode start");
                    String[] a=resultData.split(",");
                    MLog.td("tjl","resultData:"+resultData);
                    MLog.td("tjl","version name:"+a[0]);
                    MLog.td("tjl","online version:"+a[1]);
                    MLog.td("tjl","downloard url:"+a[2]);
                    MLog.td("tjl","local version:"+AppConst.versionName);
                    int cp=Global.versionCompare(a[1],AppConst.versionName);
                     MLog.td("tjl","版本比较："+cp);
                    if(cp==0)MLog.td("tjl","已经是最新版本");
                    if(cp<0)MLog.td("tjl","这个版本比线上的更新");
                    if(cp>0)
                    {
                       AppConst.NeedUpdate=true;
                        MLog.td("tjl","有新版本");
                        AppConst.APP_DOWNLOAD_URL=a[2];
                    }
                }catch (Exception E)
                {
                    MLog.td("tjl","error"+E.getMessage());
                }
                String recordvs=Global.readconfig(StartActivity.this,"VersionName");
                if(!recordvs.equals(AppConst.versionName))
                {
                    File destDir1 = new File(AppConst.EXAMPLE_PATH);
                    if(!destDir1.exists()) destDir1.mkdirs();
                    FileStorageHelper.copyAssetsto(StartActivity.this, "example", AppConst.EXAMPLE_PATH );
                    Global.writeconfig(StartActivity.this,"VersionName",AppConst.versionName);
                }
                try{
                    sleep(1000);
                }catch (Exception e){
                    MLog.td("tjl", String.valueOf(e.getMessage()));
                }
                AppConst.runDirPath = "/data/data/" + getPackageName() + "/run/";
                AssetsCopyer.releaseAssets(StartActivity.this,"code", AppConst.runDirPath);
                AppConst.chaquoPython=new  ChaquoPython(StartActivity.this);
            checkGps();
            checkStorageWrite();
            startActivity(new Intent(StartActivity.this,FristActivity.class));//启动MainActivity
            finish();
           }
        };
        initFirst();
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
    private void initFirst() {
        if(AppConst.lang.indexOf("zh-")<0){ myThread1.start();return;}
        String isFirst=Global.readconfig(this,"isFirst");
        if(isFirst.equals("False")){ myThread1.start();return;}
        TextView tv1 = new TextView(this);
        tv1.setText(getString(R.string.privacy_policy_title));
        tv1.setTextSize(14);
        tv1.setTextColor(Color.BLUE);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTextView(getString(R.string.privacy_policy_title),R.string.privacy_policy);
            }
        });

        TextView tv2 = new TextView(this);
        tv2.setText(Html.fromHtml(getString(R.string.user_agreement_title)));
        tv2.setTextSize(14);
        tv2.setTextColor(Color.BLUE);
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTextView(getString(R.string.user_agreement_title),R.string.user_agreement);
            }
        });
        final CheckBox cb1=new CheckBox(this);
        cb1.setText(getString(R.string.alreadyread));
        cb1.setTextSize(14);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.addView(tv1);
        linearLayout.addView(tv2);
        linearLayout.addView(cb1);
        setMargins(tv1,50, 20, 0, 0);
        setMargins(tv2,50, 20, 0, 0);
        setMargins(cb1,50, 20, 0, 0);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(linearLayout);
        builder.setMessage(getString(R.string.please_readme));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.entry), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!cb1.isChecked())
                {
                    ToastUtils.show(getString(R.string.Please_read_and_agree));
                    initFirst();
                }
                else
                {
                    Global.writeconfig(StartActivity.this,"isFirst","False");
                    dialog.dismiss();
                    myThread1.start();
                }
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }
    private void setLang(Locale lang){
        Locale.setDefault(lang);//设置本地为英文环境
        Configuration configuration = getBaseContext().getResources().getConfiguration();
        configuration.locale = lang;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());

    }
    private void checkGps() {
        final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;


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
        final int REQUEST_CODE_ACCESS_COARSE_STORAGE = 2;
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
}
