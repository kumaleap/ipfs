package com.zoke.ipfs.http;

/**
 * Created by wulijie on 2018/6/6.
 */
public interface ProgressCallback<T> {

    void onSuccess(T result);

    void onError(String error);

    void onWaiting();

    void onStarted();

    void onLoading(long total, long current, boolean isDownloading);

}
