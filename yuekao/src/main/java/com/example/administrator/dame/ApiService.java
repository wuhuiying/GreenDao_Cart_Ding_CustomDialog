package com.example.administrator.dame;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Administrator on 2018/4/7.
 */

public interface ApiService {
    /**
     * Get基本请求,这里从Call改为Observable被观察者
     * @param url
     * @return
     */
    @GET
    public Observable<String> get(@Url String url);

    /**
     * Get请求提交表单
     * @param url
     * @param map
     * @return
     */
    @GET
    public Observable<String> get(@Url String url, @QueryMap Map<String, String> map);

    /**
     * Post请求提交表单
     * @param url
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST
    public Observable<String> post(@Url String url, @FieldMap Map<String, String> map);
}
