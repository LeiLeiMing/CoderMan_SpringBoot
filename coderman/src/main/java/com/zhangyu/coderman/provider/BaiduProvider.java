package com.zhangyu.coderman.provider;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zhangyu.coderman.dto.BaiduAccessTokenDTO;
import com.zhangyu.coderman.dto.BaiduUser;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class BaiduProvider {
    /**
     *  获取 Github的授权码
     * @param :数据传输对象,封装网络传输的数据
     * @return
     */
    public static String getAccessToken(BaiduAccessTokenDTO accessTokenDTO) {
        OkHttpClient client = new OkHttpClient();
        String urlString = "https://openapi.baidu.com/oauth/2.0/token?grant_type=" + accessTokenDTO.getGrant_type() +
                "&code=" + accessTokenDTO.getCode() + "&client_id=" + accessTokenDTO.getClient_id() + "&client_secret=" +
                accessTokenDTO.getClient_secret() + "&redirect_uri=" + accessTokenDTO.getRedirect_uri();
        Request request = new Request.Builder().url(urlString).get().build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            String accessToken = JSON.parseObject(string).getString("access_token");
            return accessToken;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static BaiduUser getUser(String accessToken) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://openapi.baidu.com/rest/2.0/passport/users/getInfo?access_token=" + accessToken).build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            BaiduUser baiduUser = JSON.parseObject(string, BaiduUser.class);
            return baiduUser;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
