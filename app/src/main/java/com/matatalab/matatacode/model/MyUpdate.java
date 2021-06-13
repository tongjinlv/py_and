package com.matatalab.matatacode.model;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.matatalab.matatacode.AppConst;
import com.matatalab.matatacode.Global;
import com.matatalab.matatacode.R;
import com.matatalab.matatacode.UpgradingActivity;
import com.matatalab.matatacode.dfu.DfuService;
import com.matatalab.matatacode.utils.MLog;

import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

public class MyUpdate {
    private UpgradingActivity upgradingActivity;
    private volatile static MyUpdate myUpdate = null;
    private Context mContext;
    private SearchResult mdevice;
    private int mFileRawId;
    public  MyUpdate(Context mContext)
    {
        this.mContext=mContext;
        DfuServiceListenerHelper.registerProgressListener(this.mContext, mDfuProgressListener);
        setUpgradeViewInterface(mContext);
    }
    public void exit()
    {
         DfuServiceListenerHelper.unregisterProgressListener(this.mContext, mDfuProgressListener);
    }
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    Integer mScope = DfuServiceInitiator.SCOPE_APPLICATION;
                    int numberOfPackets = 10;
                    final DfuServiceInitiator starter =
//				new DfuServiceInitiator(mSelectedDevice.getAddress())
                            new DfuServiceInitiator(mdevice.getAddress())
                                    .setDeviceName(mdevice.getName())

//				.setCustomUuidsForButtonlessDfuWithBondSharing(dfuServiceUUIDString, ANCSServiceUUIDString )
                                    .setKeepBond(true)
                                    .setForceDfu(false)
                                    .setPacketsReceiptNotificationsEnabled(true)
                                    .setPacketsReceiptNotificationsValue(numberOfPackets)
                                    .setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);

                    starter.setZip(mFileRawId);
                    starter.setScope(mScope);
                    starter.start(mContext, DfuService.class);
                    break;
                default:
                    break;
            }
        }
    };
    public void Start(int upgradeRawId)
    {
        mFileRawId=upgradeRawId;
        AppConst.mClient=null;
        AppConst.mClient = new BluetoothClient(this.mContext);
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
            }
            @Override
            public void onDeviceFounded(SearchResult device) {//找到设备 可通过manufacture过滤
                // Beacon beacon = new Beacon(device.scanRecord);
                // MLog.td("tjl",String.format("beacon for %s\n%s", device.getAddress(), beacon.toString()));
                if(Global.isMatataDevice(AppConst.BT_DFU_NAME_ALL,device.getName())){
                    if(Global.CompareCount(device.getAddress(),AppConst.devMac)>13)
                    {
                        mdevice=device;
                        AppConst.mClient.stopSearch();
                    }
                    MLog.td("tjl", "Name:" + device.getName() + ",Mac:" + device.getAddress() + ",Rssi:" + device.rssi);
                }
            }
            @Override
            public void onSearchStopped() {//搜索停止
                MLog.td("tjl","搜索停止");

                if(mdevice!=null) {
                    MLog.td("tjl" , "getAddress:" +mdevice.getAddress()+" getName:"+mdevice.getName());
                    mHandler.sendEmptyMessageDelayed(100, 1000);
                }else
                {
                    MLog.td("tjl","找不到升级设备");
                    upgradingActivity.showBtErrDialogMessage(mContext.getResources().getString(R.string.DFU_RESULT_ERROR));
                }
            }
            @Override
            public void onSearchCanceled() {//搜索取消
                MLog.td("tjl","搜索取消");
                if(mdevice!=null) {
                    MLog.td("tjl" , "getAddress:" +mdevice.getAddress()+" getName:"+mdevice.getName());
                    mHandler.sendEmptyMessageDelayed(100, 1000);
                }else
                {
                    MLog.td("tjl","找不到升级设备");
                    upgradingActivity.showBtErrDialogMessage(mContext.getResources().getString(R.string.DFU_RESULT_ERROR));
                }
            }
        });
    }
    public void setUpgradeViewInterface(Context context) {
        upgradingActivity =(UpgradingActivity)context;
    }
    private final DfuProgressListener mDfuProgressListener = new DfuProgressListenerAdapter() {
        @Override
        public void onDeviceConnecting(final String deviceAddress) {
            MLog.td("tjl", "onDeviceConnecting --- deviceAddress = " + deviceAddress);
            try{
                if (upgradingActivity != null) {
                    upgradingActivity.updateProgressIndeterminate(true);
                }} catch (Exception e) {
                MLog.td("tjl", e.getMessage());
            }
        }

        @Override
        public void onDfuProcessStarting(final String deviceAddress) {
            MLog.td("tjl", "onDfuProcessStarting --- deviceAddress = " + deviceAddress);
            try{
                if (upgradingActivity != null) {
                    upgradingActivity.updateProgressIndeterminate(true);
                }} catch (Exception e) {
                MLog.td("tjl", e.getMessage());
            }
        }

        @Override
        public void onEnablingDfuMode(final String deviceAddress) {
            MLog.td("tjl", "onEnablingDfuMode --- deviceAddress = " + deviceAddress);
            try{
                if (upgradingActivity != null) {
                    upgradingActivity.updateProgressIndeterminate(true);
                }} catch (Exception e) {
                MLog.td("tjl", e.getMessage());
            }
        }

        @Override
        public void onFirmwareValidating(final String deviceAddress) {
            MLog.td("tjl", "onFirmwareValidating --- deviceAddress = " + deviceAddress);
            try{
                if (upgradingActivity != null) {
                    upgradingActivity.updateProgressIndeterminate(true);
                    upgradingActivity.showMessage(mContext.getResources().getString(R.string.dfu_status_validating));
                }} catch (Exception e) {
                MLog.td("tjl", e.getMessage());
            }
        }

        @Override
        public void onDeviceDisconnecting(final String deviceAddress) {
            MLog.td("tjl", "onDeviceDisconnecting --- deviceAddress = " + deviceAddress);
            try{
                if (upgradingActivity != null) {
                    upgradingActivity.updateProgressIndeterminate(true);
                }} catch (Exception e) {
                MLog.td("tjl", e.getMessage());
            }
        }

        @Override
        public void onDfuCompleted(final String deviceAddress) {
            MLog.td("tjl", "onDfuCompleted --- deviceAddress = " + deviceAddress);
            try{
                if (upgradingActivity != null) {
                    upgradingActivity.showUpgradeMessage(mContext.getResources().getString(R.string.DFU_RESULT_SUCCESS));
                    upgradingActivity.updateProgressIndeterminate(false);
                    upgradingActivity.updateProgressPercent(100);
                }} catch (Exception e) {
                MLog.td("tjl", e.getMessage());
            }
        }

        @Override
        public void onDfuAborted(final String deviceAddress) {
            MLog.td("tjl", "onDfuAborted --- deviceAddress = " + deviceAddress);
            try{
                if (upgradingActivity != null) {
                    upgradingActivity.showUpgradeMessage(mContext.getResources().getString(R.string.dfu_status_aborted));
                    upgradingActivity.updateProgressIndeterminate(false);
                    upgradingActivity.updateProgressPercent(0);
                }} catch (Exception e) {
                MLog.td("tjl", e.getMessage());
            }
        }

        @Override
        public void onProgressChanged(final String deviceAddress, final int percent, final float speed, final float avgSpeed, final int currentPart, final int partsTotal) {
            MLog.td("tjl", "onProgressChanged --- deviceAddress = " + deviceAddress + ", percent = " + percent
                    + ", speed = " + speed + ", avgSpeed = " + avgSpeed + ", currentPart = " + currentPart + ", partsTotal = " + partsTotal);

            try {
                if (upgradingActivity != null) {
                    upgradingActivity.updateProgressIndeterminate(false);
                    upgradingActivity.updateProgressPercent(percent);
                }
            } catch (Exception e) {
                MLog.td("tjl", e.getMessage());
            }
        }

        @Override
        public void onError(final String deviceAddress, final int error, final int errorType, final String message) {
            MLog.td("tjl", "onError --- deviceAddress = " + deviceAddress + ", error = " + error + ", errorType = " + errorType + ", message = " + message);
            try {
                if (upgradingActivity != null) {
                    upgradingActivity.showUpgradeMessage(message);
                    upgradingActivity.updateProgressIndeterminate(false);
                    upgradingActivity.updateProgressPercent(0);
                }
            } catch (Exception e) {
                MLog.td("tjl", e.getMessage());
            }
        }
    };

}
