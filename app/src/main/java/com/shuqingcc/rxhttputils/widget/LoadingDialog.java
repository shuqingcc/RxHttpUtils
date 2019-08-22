package com.shuqingcc.rxhttputils.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.shuqingcc.net.interfaces.ILoadingView;
import com.shuqingcc.rxhttputils.R;


/***
 * 自定义loadingView 任何实现ILoadingView的View都可以(突破以前只能使用dialog的限制)
 */
public class LoadingDialog extends Dialog implements ILoadingView {

    public LoadingDialog(Context context) {
        this(context, 0);
    }

    public LoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
    }


    @Override
    public void showLoadingView() {
        show();
    }

    @Override
    public void hideLoadingView() {
        dismiss();
    }
}