package com.shuqingcc.rxhttputils.api;

import com.shuqingcc.net.bean.BaseData;
import com.shuqingcc.rxhttputils.bean.BannerBean;
import com.shuqingcc.rxhttputils.bean.HotBean;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * <pre>
 *      @author : Allen
 *      e-mail  : lygttpod@163.com
 *      date    : 2019/03/23
 *      desc    :
 * </pre>
 */
public interface WanAndroidApi {

    /**
     * 获取banner数据
     *
     * @return
     */
    @GET("banner/json")
    Observable<BaseData<List<BannerBean>>> getBanner();

    /**
     * 热搜
     *
     * @return
     */
    @GET("hotkey/json")
    Observable<BaseData<List<HotBean>>> getHotSearchData();

    /**
     * 热搜
     *
     * @return
     */
    @GET("hotkey/json")
    Observable<String> getHotSearchStringData();


}
