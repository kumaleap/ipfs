package com.zoke.ipfs.http;

/**
 * Created by wulijie on 2018/6/6.
 * 处理文本的相关存储
 */
public interface SimpleCallback {
    void onSuccess(String result);

    void onError(String error);
}
