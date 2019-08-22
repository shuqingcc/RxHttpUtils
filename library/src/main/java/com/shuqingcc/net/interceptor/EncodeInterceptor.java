package com.shuqingcc.net.interceptor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class EncodeInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String method = request.method();

        //用于存储原请求的参数
        HashMap<String, String> params = new HashMap<>();

        //get方法和post方法处理参数情况不一样，需要区分开来
        if ("GET".equals(method)) {
            HttpUrl url = request.url();
            for (int i = 0; i < url.querySize(); i++) {

                //取出url中？后的参数
                String key = url.queryParameterName(i);
                String value = url.queryParameterValue(i);

                params.put(key, value);
                //可以修改内容,例如给key为phone或者password的值进行RSA加密
//                RSAUtils.xxx(params);
            }


            HttpUrl.Builder newBuilder = url.newBuilder();

            for (Map.Entry<String, String> entry : params.entrySet()) {
                newBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }

            //添加公共参数可以单独这样写 也可以直接在上面的map中先添加再遍历map添加

            newBuilder.addEncodedQueryParameter("key1", "value1")
                    .addEncodedQueryParameter("key2", "value2")
                    .build();

            //构建新的request
            request = request.newBuilder()
                    .url(newBuilder.build()).build();


        } else if ("POST".equals(method)) {

            //这里是表单请求（postJson形式后面讲）
            if (request.body() instanceof FormBody) {
                FormBody.Builder builder = new FormBody.Builder();
                FormBody oldFormBody = (FormBody) request.body();
                for (int i = 0; i < oldFormBody.size(); i++) {
                    //取出并保存原请求参数
                    params.put(oldFormBody.encodedName(i), oldFormBody.encodedValue(i));
                }

                //对参数进行加密，对原parms进行了处理
//                RSAUtils.xxx(params);

                //添加加密后的参数
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    builder.add(entry.getKey(), entry.getValue());
                }
                //构建新的request
                request = request.newBuilder()
                        .post(builder.build()).build();
            } else if (request.body() instanceof RequestBody) {
                RequestBody requestBody = request.body(); //获取请求体
                Buffer buffer = new Buffer(); //创建缓存
                requestBody.writeTo(buffer);  //把请求体的内容写入缓存
                //读取原请求的参数，String类型
                String paramsStr = buffer.readUtf8();
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();

                //转成map
                Map<String, String> map = gson.fromJson(paramsStr, new TypeToken<Map<String, String>>() {
                }.getType());


                //对参数进行处理 添加共参，加密等
//                RSAUtils.xxx(params);

//                RequestBody newsRequestBody = RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), gson.toString(map));
                RequestBody newsRequestBody = RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), gson.toJson(map));
                //构建新的请求
                request = request.newBuilder().post(newsRequestBody).build();
            }

        }

        return chain.proceed(request);
    }
}
