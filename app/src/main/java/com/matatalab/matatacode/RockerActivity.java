package com.matatalab.matatacode;

import android.app.Activity;
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
import android.widget.Toast;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.matatalab.matatacode.model.FileStorageHelper;
import com.matatalab.matatacode.model.MyQueue;
import com.matatalab.matatacode.model.MyRockerView;
import com.matatalab.matatacode.utils.MLog;
import com.matatalab.matatacode.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RockerActivity extends AppCompatActivity{
    private MyRockerView myRockerView;
    private String directionXY;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rocker);
        initView(savedInstanceState);
    }
    private void initView(@Nullable Bundle savedInstanceState) {
        myRockerView=(MyRockerView) findViewById(R.id.rockerZ_View);
        myRockerView.setOnShakeListener(MyRockerView.DirectionMode.DIRECTION_8, new MyRockerView.OnShakeListener() {
            @Override
            public void onStart() {
                MLog.td("tjl", "onStart");
            }

            @Override
            public void direction(MyRockerView.Direction direction) {
                if (direction == MyRockerView.Direction.DIRECTION_CENTER){
                    directionXY = ("当前方向：中心");
                }else if (direction == MyRockerView.Direction.DIRECTION_DOWN){
                    directionXY = ("当前方向：下");
                }else if (direction == MyRockerView.Direction.DIRECTION_LEFT){
                    directionXY = ("当前方向：左");
                }else if (direction == MyRockerView.Direction.DIRECTION_UP){
                    directionXY = ("当前方向：上");
                }else if (direction == MyRockerView.Direction.DIRECTION_RIGHT){
                    directionXY = ("当前方向：右");
                }else if (direction == MyRockerView.Direction.DIRECTION_DOWN_LEFT){
                    directionXY = ("当前方向：左下");
                }else if (direction == MyRockerView.Direction.DIRECTION_DOWN_RIGHT){
                    directionXY = ("当前方向：右下");
                }else if (direction == MyRockerView.Direction.DIRECTION_UP_LEFT){
                    directionXY = ("当前方向：左上");
                }else if (direction == MyRockerView.Direction.DIRECTION_UP_RIGHT){
                    directionXY = ("当前方向：右上");
                }
                MLog.td("tjl", "XY轴"+directionXY);
                MLog.td("tjl", "-----------------------------------------------" );
            }

            @Override
            public void onFinish() {
                MLog.td("tjl", "onFinish");
            }
        });
        myRockerView.setOnAngleChangeListener(new MyRockerView.OnAngleChangeListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void angle(double angle) {
                MLog.td("tjl","angle:"+angle);
            }

            @Override
            public void onFinish() {

            }
        });
        myRockerView.setOnDistanceLevelListener(new MyRockerView.OnDistanceLevelListener() {
            @Override
            public void onDistanceLevel(int level) {
                MLog.td("tjl","level:"+level);
            }
        });
    };


    @Override
    public void onBackPressed() {
        MLog.td("tjl" , "onBackPressed()");

    }
    @Override
    protected void onDestroy() {
        MLog.td("tjl" , "onDestroy()");
        super.onDestroy();
    }
}


