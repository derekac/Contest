package com.tzc.spider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpClientCookie {

	public static String getCookie(String loginUrl, String userName, String userValue,
			String pwdName, String pwdValue) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httpost = new HttpPost(loginUrl);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps = HttpClientSpider.getParameterListCookie(nvps, userName, userValue);
		nvps = HttpClientSpider.getParameterListCookie(nvps, pwdName, pwdValue);
		httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpEntity entity = response.getEntity();
		entity = response.getEntity();
		List<Cookie> cookies = httpclient.getCookieStore().getCookies();
		System.out.println("Login form get: " + response.getStatusLine());
		try {
			EntityUtils.consume(entity);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cookies = httpclient.getCookieStore().getCookies();
		String cookieResult = "";
		if (cookies.isEmpty()) {
			cookieResult = "PHPSESSID=";
		} else {
			for (int i = 0; i < cookies.size(); i++) {
				if (i != 0)
					cookieResult += ";";
				cookieResult += (cookies.get(i).getName() + "=" + cookies
						.get(i).getValue());
			}
		}
		return cookieResult;
	}

	public static void main(String[] args) {
		for (int i = 0; i < 1; i++) {
			String cookie = HttpClientCookie.getCookie(
					"http://acm.tzc.edu.cn/acmhome/login.do", "userName",
					"password", "tzcjudge", "tzcjudge");
			System.out.println(cookie);
			HttpClientSpider test = new HttpClientSpider();
			String res = test
					.crawl(
							"http://acm.tzc.edu.cn/acmhome/submitcode.do?problemId=1013&language=GCC&code=sorryInTestJudge[testtesttest]",
							6000, "utf-8", cookie);
			System.out.println(res);
		}
	}
}
