package com.iskyun.im.data.net;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.iskyun.im.common.Constant;
import com.iskyun.im.data.api.ApiUploadService;
import com.iskyun.im.data.api.ApiUserService;
import com.iskyun.im.utils.GsonUtils;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.manager.SpManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {
    private static final String API_URL = Constant.API_URL;
    private static final long CONNECT_TIMEOUT = 20L;        //设置连接超时时间
    private static final long READ_TIMEOUT = 90L;           //设置读取超时时间
    private static final long WRITE_TIMEOUT = 120L;

    private static final RetrofitManager INSTANCE = new RetrofitManager();

    private final Retrofit retrofit;
    private volatile ApiUserService apiUserService;
    private volatile ApiUploadService apiUploadService;

    private RetrofitManager() {
        retrofit = createRetrofit(createHttpClient());
    }

    public static RetrofitManager get() {
        return INSTANCE;
    }

    //
    public ApiUserService getApiUserService() {
        if (apiUserService == null) {
            synchronized (RetrofitManager.class) {
                if (apiUserService == null)
                    apiUserService = retrofit.create(ApiUserService.class);
            }
        }
        return apiUserService;
    }

    public ApiUploadService getApiUploadService() {
        if (apiUploadService == null) {
            synchronized (RetrofitManager.class) {
                if (apiUploadService == null)
                    apiUploadService = retrofit.create(ApiUploadService.class);
            }
        }
        return apiUploadService;
    }

    private OkHttpClient createHttpClient() {
        return new OkHttpClient.Builder().retryOnConnectionFailure(true)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                //https
                //.connectionSpecs(Arrays.asList(ConnectionSpec.CLEARTEXT, ConnectionSpec.MODERN_TLS))
                .addInterceptor(new HttpLogInterceptor())
                .addInterceptor(new TokenInterceptor())
                .build();
    }

    private Retrofit createRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonUtils.gson))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    private static final class TokenInterceptor implements Interceptor {

        @NonNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder requestBuilder = chain.request().newBuilder();
            LogUtils.i("request-token:"+SpManager.getInstance().getToken());
            if (!TextUtils.isEmpty(SpManager.getInstance().getToken())) {
                requestBuilder.addHeader("com-huacao-token", SpManager.getInstance().getToken());
            }
            return chain.proceed(requestBuilder.build());
        }
    }

    /**
     * 日志拦截器
     */
    private static final class HttpLogInterceptor implements Interceptor {

        @NonNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            logForRequest(request);
            Response response = chain.proceed(request);
            return logForResponse(response);
        }

        private Response logForResponse(Response response) {
            Response.Builder builder = response.newBuilder();
            Response clone = builder.build();
            /*if (!TextUtils.isEmpty(clone.message())) {
                LogUtils.d("message:" + clone.message());
            }*/
            ResponseBody body = clone.body();
            if (body != null) {
                MediaType mediaType = body.contentType();
                if (mediaType != null) {
                    if (isText(mediaType)) {
                        try {
                            String resp = body.string();
                            body = ResponseBody.create(mediaType, resp);
                            LogUtils.i("response:" + resp);
                            return response.newBuilder().body(body).build();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        LogUtils.e("response error");
                    }
                }
            }
            return response;
        }

        private boolean isText(MediaType mediaType) {
            if (mediaType.type().equals("text")) {
                return true;
            }
            return mediaType.subtype().equals("json")
                    || mediaType.subtype().equals("xml")
                    || mediaType.subtype().equals("html")
                    || mediaType.subtype().equals("webviewhtml")
                    || mediaType.subtype().equals("x-www-form-urlencoded");
        }

        /**
         * 请求日志
         */
        private void logForRequest(Request request) {
            RequestBody requestBody = request.body();
            if (requestBody != null) {
                try {
                    Buffer buffer = new Buffer();
                    requestBody.writeTo(buffer);
                    Charset charset = Charset.defaultCharset();
                    MediaType contentType = requestBody.contentType();
                    if(contentType != null){
                        charset = contentType.charset(StandardCharsets.UTF_8);
                    }
                    assert charset != null;
                    LogUtils.i("request:" + buffer.readString(charset));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
