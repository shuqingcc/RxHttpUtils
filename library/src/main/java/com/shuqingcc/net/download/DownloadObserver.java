package com.shuqingcc.net.download;

import android.annotation.SuppressLint;

import com.shuqingcc.net.base.BaseObserver;
import com.shuqingcc.net.manage.RxHttpManager;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by allen on 2017/6/13.
 * <p>
 *
 * @author Allen
 * 文件下载
 */

public abstract class DownloadObserver extends BaseObserver<ResponseBody> {

    private String fileName;
    private String destFileDir;

    public DownloadObserver(String fileName) {
        this.fileName = fileName;
    }

    public DownloadObserver(String fileName, String destFileDir) {
        this.fileName = fileName;
        this.destFileDir = destFileDir;
    }


    /**
     * 失败回调
     *
     * @param errorMsg errorMsg
     */
    protected abstract void onError(String errorMsg);


    /**
     * 成功回调
     *
     * @param filePath filePath
     */
    /**
     * 成功回调
     *
     * @param bytesRead     已经下载文件的大小
     * @param contentLength 文件的大小
     * @param progress      当前进度
     * @param done          是否下载完成
     * @param filePath      文件路径
     */
    protected abstract void onSuccess(long bytesRead, long contentLength, float progress, boolean done, String filePath);


    @Override
    public void doOnError(String errorMsg) {
        onError(errorMsg);
    }

    @Override
    public void doOnSubscribe(Disposable d) {
        RxHttpManager.get().add(setTag(), d);
    }

    @SuppressLint("CheckResult")
    @Override
    public void doOnNext(ResponseBody responseBody) {
        Observable
                .just(responseBody)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            new DownloadManager().saveFile(responseBody, fileName, destFileDir, new ProgressListener() {
                                @Override
                                public void onResponseProgress(final long bytesRead, final long contentLength, final int progress, final boolean done, final String filePath) {
                                    Observable
                                            .just(progress)
                                            .distinctUntilChanged()
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Consumer<Integer>() {
                                                @Override
                                                public void accept(@NonNull Integer integer) throws Exception {
                                                    onSuccess(bytesRead, contentLength, progress, done, filePath);
                                                }
                                            });
                                }

                            });

                        } catch (IOException e) {
                            Observable
                                    .just(e.getMessage())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<String>() {
                                        @Override
                                        public void accept(String s) throws Exception {
                                            doOnError(s);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void doOnCompleted() {

    }
}
