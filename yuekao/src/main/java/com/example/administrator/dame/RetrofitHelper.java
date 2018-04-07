package com.example.administrator.dame;

import android.os.Environment;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Administrator on 2018/4/7.
 */

public class RetrofitHelper {
    public static OkHttpClient okHttpClient;
    public static ApiService apiService;

    static {
        getOkHttpClient();
    }

    public static OkHttpClient getOkHttpClient(){
        if(okHttpClient == null){
            synchronized (OkHttpClient.class) {
                if(okHttpClient == null){
                    File fileDir = new File(Environment.getExternalStorageDirectory(), "cache");
                    long fileSize = 10 * 1024 * 1024;
                    okHttpClient = new OkHttpClient.Builder()
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .readTimeout(15, TimeUnit.SECONDS)
                            .writeTimeout(15, TimeUnit.SECONDS)
                            .cache(new Cache(fileDir, fileSize))
                            .build();
                }
            }
        }
        return okHttpClient;
    }

    public static ApiService getApiService(String url){
        if(apiService == null){
            synchronized (OkHttpClient.class) {
                apiService = createApiService(ApiService.class,url);

            }
        }
        return apiService;
    }

    private static <T>T createApiService(Class<T> tClass,String url) {
        T t = new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(tClass);
        return t;
    }

    /*private static class CommonParamsInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            String method = request.method();
            String oldUrl = request.url().toString();
            Log.e("---拦截器",request.url()+"---"+request.method()+"--"+request.header("User-agent"));
            Map<String,String> map = new HashMap<>();
            map.put("source","android");
            if ("GET".equals(method)){
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(oldUrl);
                if (oldUrl.contains("?")){
                    if (oldUrl.indexOf("?") == oldUrl.length()-1){
                    }else {
                        stringBuilder.append("&");
                    }
                }else {
                    stringBuilder.append("?");
                }
                for (Map.Entry<String,String> entry: map.entrySet()) {
                    stringBuilder.append(entry.getKey())
                            .append("=")
                            .append(entry.getValue())
                            .append("&");
                }
                if (stringBuilder.indexOf("&") != -1){
                    stringBuilder.deleteCharAt(stringBuilder.lastIndexOf("&"));
                }
                String newUrl = stringBuilder.toString();
                request = request.newBuilder()
                        .url(newUrl)
                        .build();
            }else if ("POST".equals(method)){
                RequestBody oldRequestBody = request.body();
                if (oldRequestBody instanceof FormBody){
                    FormBody oldBody = (FormBody) oldRequestBody;
                    FormBody.Builder builder = new FormBody.Builder();
                    for (int i=0;i<oldBody.size();i++){
                        builder.add(oldBody.name(i),oldBody.value(i));
                    }
                    for (Map.Entry<String,String> entry:map.entrySet()) {
                        builder.add(entry.getKey(),entry.getValue());
                    }
                    FormBody newBody = builder.build();
                    request = request.newBuilder()
                            .url(oldUrl)
                            .post(newBody)
                            .build();
                }
            }
            Response response = chain.proceed(request);
            return response;
        }
    }*/
}
