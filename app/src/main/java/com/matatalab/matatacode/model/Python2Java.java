package com.matatalab.matatacode.model;

import com.matatalab.matatacode.AppConst;
import com.matatalab.matatacode.CodingActivity;
import com.matatalab.matatacode.UpgradingActivity;
import com.matatalab.matatacode.utils.MLog;

import java.util.ArrayList;
import java.util.List;

public class Python2Java {

    public Python2Java(String n){

    }
    public void write(Object object){
        String msg=String.valueOf(object);
        Boolean show=true;
        if(msg.trim().length()<2)show=false;
        if(msg.indexOf(".......")>-1)show=false;
        if(msg.indexOf("sys.modules")>-1)show=false;
        if(show){
            msg=msg.replace("error:","");
            msg=msg.replace("\"","'");
            CodingActivity.runJsCode("javascript:alert(\""+msg+"\")",null);
        }
        MLog.td("tjl",msg);
    }
    public int crc16(int[] data)
    {
        int crc = 0xffff;
        for (int i = 0; i < data.length; i++)
        {
            crc  =(0xffff)&( (char)((0xffff)&crc >> 8) | (0xffff)&(crc << 8));crc=0xffff&crc;
            crc ^= 0xff&data[i];crc=0xffff&crc;crc=0xffff&crc;
            crc ^=(0xffff)& ((char)(crc & 0xFF) >> 4);crc=0xffff&crc;
            crc ^= (0xffff)&((0xffff)&((0xffff)&crc << 8) << 4);crc=0xffff&crc;
            crc ^=(0xffff)&(((0xffff)&((0xffff)&crc & 0xFF) << 4) << 1);crc=0xffff&crc;
        }
     //   MLog.td("tjl","crc:"+Integer.toHexString(crc));
        return crc;
    }
    public int [] correct(int[] data)//编码
    {
        int crc=crc16(data);
        int[] tmp=new int[data.length+2];
        for(int i=0;i<data.length;i++)tmp[i]=data[i];
        tmp[data.length]=crc>>8;
        tmp[data.length+1]=crc&0xff;
        data=tmp;

        int[] buf=new int[200];
        int n=0;
        if(data==null)return null;
        if(data.length<1)return null;
        buf[n++]=0xfe;
        for(int i=0;i<data.length;i++)
        {
            if(data[i]==0xfe){buf[n++]=0xfd;buf[n++]=0xde;}
            else if(data[i]==0xfd){buf[n++]=0xfd;buf[n++]=0xdd;}
            else buf[n++]=data[i];
        }
        data=new int[n];
        for(int i=0;i<n;i++)data[i]=buf[i];
        return data;
    }
    public void blewrite(int[] data)
    {
        int[] dat=new  int[data.length+1];
        dat[0]=(int)data.length+2;
        for(int i=0;i<data.length;i++)dat[i+1]=(byte)data[i];
        data=dat;
        data=correct(data);
        byte[] bytes=new  byte[data.length];
        for(int i=0;i<data.length;i++)bytes[i]=(byte)data[i];
        MLog.td("tjl","write:"+MLog.bytes2hex(bytes));
        MyBluetooth.BleWrite(bytes);
    }
    public byte[] export(byte[] data)
    {
        if(data.length<1){MLog.td("tjl","长度不够");return null;}
        if(data[0]!=(byte)0xfe){MLog.td("tjl","头不对");return null;}
        byte[] buf=new byte[data.length];
        int n=0;
        for(int i=1;i<data.length;i++)
        {
            if(data[i]==(byte)0xfd)
            {
                if((i+1)>=data.length){MLog.td("tjl","数据缺失");return null;}
                if(data[i+1]==(byte)0xDE)buf[n++]=(byte)0xfe;
                else if(data[i+1]==(byte)0xDD)buf[n++]=(byte)0xfd;
                else {MLog.td("tjl","协议未知");return null;}
                i++;
            }
            else buf[n++]=data[i];
        }
        if(n>2)n-=2;else {MLog.td("tjl","数据缺失");return null;}
        data=new byte[n];
        int pcrc=0xff&buf[n];
        pcrc=pcrc<<8;
        pcrc=pcrc|0xff&buf[n+1];
        for(int i=0;i<n;i++)data[i]=buf[i];
        int cbuf[]=new int[n];
        MLog.td("tjl","解析结果:"+MLog.bytes2hex((data)));
        for(int i=0;i<n;i++)cbuf[i]=0xff&data[i];
        int crc=crc16(cbuf);
        if(crc==pcrc) MLog.td("tjl","校验通过");
        else MLog.td("tjl","校验不通过");
        return data;
    }

    public int[] blewait()
    {
        int it=4;
        try {
            while(it-->0) {
                MLog.td("tjl", "wait");
                byte[] bytes = MyBluetooth.BleWait();
                MLog.td("tjl", "接收完:" + MLog.bytes2hex(bytes));
                if (bytes == null) return null;
                bytes = export(bytes);
                MLog.td("tjl", "接收解析完:" + MLog.bytes2hex(bytes));
                if(bytes[1]==(byte)0x88)break;
                int[] by=new int[bytes.length];
                for(int i=0;i<by.length;i++)by[i]=0xff&bytes[i];
                return by;
                //Thread.sleep(1000);
            }
        }catch (Exception E)
        {
            MLog.td("tjl","解析异常:"+E.getMessage());
        }
        return null;
    }
    public int[] blewait_until(int key)
    {
        try {
            while(AppConst.pythonRun) {
                MLog.td("tjl", "wait");
                byte[] bytes = MyBluetooth.BleWait();
                MLog.td("tjl", "接收完:" + MLog.bytes2hex(bytes));
                if (bytes == null) return null;
                bytes = export(bytes);
                MLog.td("tjl", "接收解析完:" + MLog.bytes2hex(bytes));
                if(bytes[1]==(byte)key) {
                    int[] by = new int[bytes.length];
                    for (int i = 0; i < by.length; i++) by[i] = 0xff & bytes[i];
                    return by;
                }else  MLog.td("tjl", "未匹配"+key);
            }
        }catch (Exception E)
        {
            MLog.td("tjl","解析异常:"+E.getMessage());
        }
        return null;
    }
    public int[] blewait(int key)
    {
        int it=4;
        try {
            while(it-->0) {
                MLog.td("tjl", "wait");
                byte[] bytes = MyBluetooth.BleWait();
                MLog.td("tjl", "接收完:" + MLog.bytes2hex(bytes));
                if (bytes == null) return null;
                bytes = export(bytes);
                MLog.td("tjl", "接收解析完:" + MLog.bytes2hex(bytes));
                if(bytes[1]==(byte)key) {
                    int[] by = new int[bytes.length];
                    for (int i = 0; i < by.length; i++) by[i] = 0xff & bytes[i];
                    return by;
                }else  MLog.td("tjl", "未匹配"+key);
            }
        }catch (Exception E)
        {
            MLog.td("tjl","解析异常:"+E.getMessage());
        }
        return null;
    }
    public void alert(String msg)
    {
        CodingActivity.Alert(msg);
    }
    public void runover()
    {
        MLog.td("tjl","run over");
       // CodingActivity.Pyexit();
        AppConst.pythonRunflag=true;
        AppConst.pythonRun=false;
    }
}