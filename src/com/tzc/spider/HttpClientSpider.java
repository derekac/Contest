package com.tzc.spider;

import net.jdiy.util.Fn.interval;

import org.apache.log4j.Logger;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.tzc.utils.LogUtil;
import com.tzc.utils.RegexUtil;

public class HttpClientSpider {
	private static Logger logger = LogUtil.getLogger();

	public static String crawl(String url, int timeout, String charset,
			String cookie) {
		if (cookie == null || "".equals(cookie)) {
			cookie = "PHPSESSID=";
		}
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		httpget
				.setHeader("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; rv:20.0) Gecko/20100101 Firefox/20.0");
		httpget.setHeader("Accept-Language",
				"	zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
		httpget.setHeader("Accept-Encoding", "*");
		httpget
				.setHeader("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httpget.setHeader("Cookie", cookie);
		HttpParams params = httpclient.getParams();
		HttpConnectionParams.setSoTimeout(params, timeout);
		try {
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return EntityUtils.toString(entity, charset);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return null;
	}

	public static String crawl(String url, int timeout, String charset) {
		return crawl(url, timeout, charset, "");
	}

	/**
	 * 重试次数为3次 进行抓取
	 * 
	 * @param url
	 * @param charset
	 * @param retry
	 * @return 页面源文件
	 */
	public static String crawlRetry(String url, String charset, int retry) {
		String content = null;
		int cnt = 0;
		while (retry != 0) {
			logger.info("#重试第" + cnt + "次抓取");
			content = crawl(url, 6000, charset, "");
			if (content != null)
				return content;
			retry--;
		}
		return null;
	}

	public static DefaultHttpClient getContentByParameter(String url,
			List<NameValuePair> nvps) throws UnsupportedEncodingException {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httpost = new HttpPost(url);
		httpost.setEntity(new UrlEncodedFormEntity(nvps, "gbk"));
		System.out.println("test--------------------------");
		logger.info("#" + httpost.getURI());
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpost);
			return httpclient;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return null;
	}

	public static List<NameValuePair> getParameterListCookie(
			List<NameValuePair> nvps, String name, String value) {
		nvps.add(new BasicNameValuePair(name, value));
		return nvps;
	}

	/**
	 * 
	 * @param loginUrl
	 * @param parametetList
	 * @param timeout
	 * @param charset
	 * @param cookie
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String crawInPost(String loginUrl,
			List<NameValuePair> parametetList, int timeout, String charset,
			String cookie) throws UnsupportedEncodingException {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httpost = new HttpPost(loginUrl);
		httpost.setHeader("Cookie", cookie);
		httpost.setEntity(new UrlEncodedFormEntity(parametetList, charset));
		System.out.println(httpost.getURI());
		HttpParams params = httpclient.getParams();
		HttpConnectionParams.setSoTimeout(params, timeout);
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}
		HttpEntity entity = response.getEntity();

		if (entity != null) {
			try {
				return EntityUtils.toString(entity);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void main(String[] args) {
		// url, 6000, charset
		System.out.println(HttpClientSpider.crawl("http://acm.uestc.edu.cn/",
				6000, "utf-8"));
	}
}
