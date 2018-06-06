package com.zoke.ipfs.demo;

import android.app.Application;

import com.zoke.ipfs.api.IPFS;

/**
 * Created by wulijie on 2018/6/6.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化IPFS
        IPFS.getInstance().init(this);
    }
}
