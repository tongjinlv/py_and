package com.matatalab.matatacode;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MAlert extends Dialog {

    private Button btn_ok;//定义取消与确认按钮
    private TextView title;//定义标题文字
    private TextView msg;//定义标题文字
    private Context context;

    //构造方法
    public MAlert(Context context) {

        //设置对话框样式
        super(context, R.style.mdialog);

        //通过LayoutInflater获取布局
        View view = LayoutInflater.from(getContext()).inflate(R.layout.malertlayout, null);
        title = view.findViewById(R.id.diglog_title);   //获取显示标题的文本框控件
        msg = view.findViewById(R.id.diglog_msg);   //获取显示标题的文本框控件
        btn_ok = view.findViewById(R.id.btn_ok);    //获取取消按钮
        this.context=context;
        setContentView(view);  //设置显示的视图

    }

    //设置标题
    public void setTitle(String content) {
        title.setText(content);
    }
    public void setMessage(String content) {
        msg.setText(content);
    }
    public void setOk(String content) {
        btn_ok.setText(content);
    }
    //取消监听
    public void setButtonOkListener(View.OnClickListener listener) {
        btn_ok.setOnClickListener(listener);
    }

}