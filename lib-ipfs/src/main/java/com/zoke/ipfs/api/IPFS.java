package com.zoke.ipfs.api;

import android.app.Application;

import com.google.gson.Gson;
import com.zoke.ipfs.Conf;
import com.zoke.ipfs.http.ProgressCallback;
import com.zoke.ipfs.http.SimpleCallback;
import com.zoke.ipfs.model.IPFSResult;
import com.zoke.ipfs.model.SimpleResult;

import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.http.body.MultipartBody;
import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wulijie on 2018/6/6.
 * 后期有时间再替换网络库吧 暂时就这样
 */
public class IPFS {
    private IPFS() {
    }

    private static class IPFSHolder {
        private static final IPFS INSTANCE = new IPFS();
    }

    public static final IPFS getInstance() {
        return IPFSHolder.INSTANCE;
    }

    /**
     * 初始化
     *
     * @param app
     */
    public void init(Application app) {
        x.Ext.init(app);
        x.Ext.setDebug(Conf.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
    }

    /**
     * 上传文件
     *
     * @param filePath
     * @param callback
     */
    public void postFile(String filePath, final ProgressCallback<String> callback) {
        postFile(new File(filePath), callback);
    }

    /**
     * 上传文件
     *
     * @param file
     * @param callback
     */
    public void postFile(File file, final ProgressCallback<String> callback) {
        RequestParams params = new RequestParams(Conf.IPFS_RPC_ADD);
        params.setAsJsonContent(true);
        List<KeyValue> list = new ArrayList<>();
        list.add(new KeyValue("file", file));
        MultipartBody body = new MultipartBody(list, "UTF-8");
        params.setRequestBody(body);
        x.http().post(params, new Callback.ProgressCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (callback != null) callback.onSuccess(change(result));
                LogUtil.i("onSuccess result =" + result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (callback != null) callback.onError(ex.getMessage());
                LogUtil.i("onError Throwable =" + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.i("onCancelled CancelledException =" + cex.toString());
            }

            @Override
            public void onFinished() {
                LogUtil.i("onFinished ");
            }

            @Override
            public void onWaiting() {
                if (callback != null) callback.onWaiting();
                LogUtil.i("onWaiting");
            }

            @Override
            public void onStarted() {
                if (callback != null) callback.onStarted();
                LogUtil.i("onStarted ");
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                if (callback != null) callback.onLoading(total, current, isDownloading);
                LogUtil.i("onLoading total=" + total + " current=" + current + " isDownloading=" + isDownloading);
            }
        });
    }

    /**
     * 上传文本信息
     *
     * @param text     文本信息
     * @param callback 回调
     */
    public void postText(String text, final SimpleCallback callback) {
        RequestParams params = new RequestParams(Conf.IPFS_RPC_ADD);
        params.setAsJsonContent(true);
        List<KeyValue> list = new ArrayList<>();
        list.add(new KeyValue("file", text));
        MultipartBody body = new MultipartBody(list, "UTF-8");
        params.setRequestBody(body);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (callback != null) callback.onSuccess(change(result));
                LogUtil.i("onSuccess result =" + result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (callback != null) callback.onError(ex.getMessage());
                LogUtil.i("onError Throwable =" + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    /**
     * 转换格式
     *
     * @param result
     * @return
     */
    private String change(String result) {
        try {
            IPFSResult ipRes = new Gson().fromJson(result, IPFSResult.class);
            SimpleResult simpleResult = new SimpleResult();
            simpleResult.hash = ipRes.Hash;
            simpleResult.name = ipRes.Name;
            simpleResult.size = ipRes.Size;
            simpleResult.url = Conf.IPFS_GATEWAY + "/ipfs/" + simpleResult.hash;
            String json = new Gson().toJson(simpleResult);
            return json;
        } catch (Exception e) {
            LogUtil.e(e.toString());
            return result;
        }
    }

    /**
     * 复制文件
     *
     * @param oldPath 需要复制的文件路径
     * @param newPath 复制后的文件路劲
     */
    private void copy(String oldPath, String newPath) {
        try {
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fileOfutputStream = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inStream.read(buffer)) != -1) {
                    fileOfutputStream.write(buffer, 0, length);
                }
                inStream.close();
                fileOfutputStream.close();
            }
        } catch (Exception e) {
            LogUtil.e(e.toString());
        }
    }


    /**
     * 根据hash获取文件 - 或者是获取文本 我封装处理一下
     *
     * @param hash
     */
    public void download(String hash, String savePath, final ProgressCallback<File> callback) {
        String url = Conf.IPFS_GATEWAY + "/ipfs/" + hash;
        LogUtil.i("download url = " + url);
        RequestParams params = new RequestParams(url);
        params.setAutoRename(false);
        params.setAutoResume(false);
        params.setSaveFilePath(savePath);
        x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(File result) {
                if (callback != null) callback.onSuccess(result);
                LogUtil.i("onSuccess result =" + result.getName());
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (callback != null) callback.onError(ex.getMessage());
                LogUtil.i("onError Throwable =" + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.i("onCancelled CancelledException =" + cex.toString());
            }

            @Override
            public void onFinished() {
                LogUtil.i("onFinished ");
            }

            @Override
            public void onWaiting() {
                if (callback != null) callback.onWaiting();
                LogUtil.i("onWaiting");
            }

            @Override
            public void onStarted() {
                if (callback != null) callback.onStarted();
                LogUtil.i("onStarted ");
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                if (callback != null) callback.onLoading(total, current, isDownloading);
                LogUtil.i("onLoading total=" + total + " current=" + current + " isDownloading=" + isDownloading);
            }
        });
    }

    /**
     * 根据hash获取你存储的文本信息
     *
     * @param hash     服务器返回的hash
     * @param callback 回调
     */
    public void getText(String hash, final SimpleCallback callback) {
        String url = Conf.IPFS_GATEWAY + "/ipfs/" + hash;
        LogUtil.i("get url = " + url);
        x.http().get(new RequestParams(url), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (callback != null) callback.onSuccess(result);
                LogUtil.i("onSuccess result =" + result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (callback != null) callback.onError(ex.getMessage());
                LogUtil.i("onError Throwable =" + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

}
