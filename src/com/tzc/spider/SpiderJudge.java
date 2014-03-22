package com.tzc.spider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.jdiy.util.Fn.interval;

import org.apache.commons.collections.StaticBucketMap;
import org.apache.http.NameValuePair;
import org.apache.log4j.Logger;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.tzc.bean.Status;
import com.tzc.utils.LogUtil;
import com.tzc.utils.RegexUtil;
import com.tzc.utils.Utils;

public class SpiderJudge {

	private static int RETRY_SPIDER_COUNT = 10;
	private static long SLEEP_TIME = 2000;
	private static Logger logger = LogUtil.getLogger();

	public Status judge(String username, String pwd, String problemId,
			String languageId, String source, String ojName) {
		try {
			Status judgeResult = null;
			if ("zju".equalsIgnoreCase(ojName)) {
				judgeResult = judgeZju(username, pwd, problemId, languageId,
						source);
			} else if ("hdu".equalsIgnoreCase(ojName)) {
				judgeResult = judgeHdu(username, pwd, problemId, languageId,
						source);
			}
			System.out.println(judgeResult);
			return judgeResult;
		} catch (Exception e) {
			return new Status();
		}

	}

	/**
	 * @author 0929210011
	 * @param username
	 * @param pwd
	 * @param problemId
	 * @param languageId
	 * @param source
	 * @return
	 */
	private Status judgeHdu(String username, String pwd, String problemId,
			String languageId, String source) throws Exception {
		String statusPageUrl = "http://acm.hdu.edu.cn/status.php";
		Status status = new Status();
		String cookie = HttpClientCookie
				.getCookie(
						"http://acm.hdu.edu.cn/userloginex.php?action=login&login=Sign%20In",
						"username", username, "userpass", pwd);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps = HttpClientSpider.getParameterListCookie(nvps, "check", "0");
		nvps = HttpClientSpider.getParameterListCookie(nvps, "problemid",
				problemId);
		nvps = HttpClientSpider.getParameterListCookie(nvps, "language",
				languageId);
		nvps = HttpClientSpider
				.getParameterListCookie(nvps, "usercode", source);
		String submitString = HttpClientSpider.crawInPost(
				"http://acm.hdu.edu.cn/submit.php?action=submit", nvps, 6000,
				"gb2312", cookie);
//		statusPageUrl = statusPageUrl + "?&pid=" + problemId + "&user="
//				+ username;
//		String searchPage = HttpClientSpider.crawl(statusPageUrl, 6000,
//				"gb2312", cookie);
//		String submitIdString = RegexUtil.getStringByRegex(searchPage,
//				"<tr\\s+align=center\\s+><td\\s+height=22px>(\\d+)</td>");
//		String statusJudge = RegexUtil
//				.getStringByRegex(
//						searchPage,
//						"<td\\s+height=22px>\\d+</td><td>\\d+-\\d+-\\d+\\s+\\d+:\\d+:\\d+</td><td><font\\s+.*?>(.*?)</font>");
//		int retry = 0;
//		statusPageUrl = statusPageUrl + "&first=" + submitIdString;
//		while (!checkStatus(statusJudge) && retry < RETRY_SPIDER_COUNT) {
//			searchPage = HttpClientSpider.crawl(statusPageUrl, 6000, "gb2312",
//					cookie);
//			statusJudge = RegexUtil
//					.getStringByRegex(
//							searchPage,
//							"<td\\s+height=22px>\\d+</td><td>\\d+-\\d+-\\d+\\s+\\d+:\\d+:\\d+</td><td><font\\s+.*?>(.*?)</font>");
//
//			logger.info("#HDU Judging，重试抓取");
//			try {
//				Thread.sleep(SLEEP_TIME);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			retry++;
//		}
//		searchPage = RegexUtil
//				.getStringByRegex(
//						searchPage,
//						"(<tr[\\S|\\s]{0,20}align=center\\s+><td[\\s|\\S]*?>\\d+</td><td>\\d+-\\d+-\\d+\\s+\\d+:\\d+:\\d+</td><td>[\\s|\\S]{0,255}<font[\\s|\\S]*?>(.*?)</font>[\\s|\\S]{0,10}</td><td><a[\\s|\\S]*?>\\d+</a></td><td>(\\d+)MS</td><td>\\d+K</td><td.*?>\\d+\\sB</td><td>.*?</td>)");
//		status.setSubmitId(Integer.parseInt(submitIdString));
//		status.setJudgeStatus(RegexUtil.getStringByRegex(searchPage,
//				"<font\\s+color=.*?>(.*?)</font>").replaceAll("<br>", ""));
//		status.setLanguage(RegexUtil.getStringByRegex(searchPage,
//				"B</td><td>(.*?)</td>"));
//		status.setOjName("HDU");
//		status.setProblemId(Integer.parseInt(problemId));
//		status.setRunMemory(Integer.parseInt(RegexUtil.getStringByRegex(
//				searchPage, "<td>(\\d+)K</td>")));
//		status.setRunTime(Integer.parseInt(RegexUtil.getStringByRegex(
//				searchPage, "(\\d+)MS")));
//		status.setSubmitTime(RegexUtil.getStringByRegex(searchPage,
//				"\\d+</td><td>(\\d+-\\d+-\\d+\\s+\\d+:\\d+:\\d+)</td>"));
//		status.setUserName(username);
//		if ("Compilation Error".equals(status.getJudgeStatus())) {
//			String errorUrl = "http://acm.hdu.edu.cn/viewerror.php?rid="
//					+ status.getSubmitId();
//			String errorContent = HttpClientSpider.crawl(errorUrl, 6000,
//					"gb2312", cookie);
//			errorContent = RegexUtil.getStringByRegex(errorContent,
//					"<pre>([\\s|\\S]*?)</pre>");
//			status.setErrorInfo(errorContent);
//		}
		return status;
	}

	private boolean checkStatus(String statusJudge) throws Exception {
		String[] retryStatus = { "Compiling", "Running", "Queuing" };
		if(statusJudge == null){
			return false;
		}
		for (String tmp : retryStatus) {
			if (statusJudge.contains(tmp)) {
				return false;
			}
		}
		return true;
	}
	/**
	 * @author 0929210011
	 * @param username
	 * @param pwd
	 * @param problemId
	 * @param languageId
	 * @param source
	 * @return 浙江大学OJ提交状态
	 */
	private Status judgeZju(String username, String pwd, String problemId,
			String languageId, String source) throws Exception {
		Status status = new Status();
		int problem = Integer.parseInt(problemId) - 1000;
		problemId = problem + "";
		String cookie = HttpClientCookie.getCookie(
				"http://acm.zju.edu.cn/onlinejudge/login.do", "handle",
				username, "password", pwd);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps = HttpClientSpider.getParameterListCookie(nvps, "problemId",
				problemId);
		nvps = HttpClientSpider.getParameterListCookie(nvps, "languageId",
				languageId);
		nvps = HttpClientSpider.getParameterListCookie(nvps, "source", source);
		String submitString = HttpClientSpider.crawInPost(
				"http://acm.zju.edu.cn/onlinejudge/submit.do", nvps, 6000,
				"utf-8", cookie);
		System.out.println(submitString);
		String submitId = RegexUtil.getStringByRegex(submitString,
				"<font\\s+color=\\'red\\'>(\\d+)</font>.");
		nvps.clear();
		nvps = HttpClientSpider.getParameterListCookie(nvps, "contestId", "1");
		nvps = HttpClientSpider.getParameterListCookie(nvps, "idStart",
				submitId);
		nvps = HttpClientSpider.getParameterListCookie(nvps, "idEnd", submitId);
		String searchPage = null;
		String statusJudge = null;
		for (int i = 0; i < RETRY_SPIDER_COUNT; i++) {
			logger.info("#ZJU Judging,正在抓取");
			searchPage = HttpClientSpider.crawInPost(
					"http://acm.zju.edu.cn/onlinejudge/showRuns.do", nvps,
					90000, "utf-8", cookie);
			statusJudge = RegexUtil.getStringByRegex(searchPage,
					"<span\\s+class=\"judgeReply.*?\">([\\S|\\s]*?)</span>");
			if (!checkStatus(statusJudge)) {
				break;
			}
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		statusJudge = statusJudge.trim();
		if (statusJudge.contains("Compilation Error")) {
			status.setJudgeStatus("Compilation Error");
			String url = "http://acm.zju.edu.cn"
					+ RegexUtil.getStringByRegex(statusJudge,
							"<a\\s+href=\"(.*?)\">");
			status.setErrorInfo(HttpClientSpider.crawl(url, 6000, "utf-8",
					cookie));
		} else {
			status.setJudgeStatus(statusJudge);
		}
		status
				.setSubmitTime(RegexUtil
						.getStringByRegex(searchPage,
								"<td\\s+class=\"runSubmitTime\">(\\d+-\\d+-\\d+\\s+\\d+:\\d+:\\d+)</td>"));
		status.setSubmitId(Integer.parseInt(submitId));
		status.setProblemId(Integer.parseInt(problemId) + 1000);
		status.setLanguage(RegexUtil.getStringByRegex(searchPage,
				"<td\\s+class=\"runLanguage\"><a.*?>(.*?)</a></td>"));
		status.setRunTime(Integer.parseInt(RegexUtil.getStringByRegex(
				searchPage, "<td\\s+class=\"runTime\">(\\d+)</td>")));
		status.setRunMemory(Integer.parseInt(RegexUtil.getStringByRegex(
				searchPage, "<td\\s+class=\"runMemory\">(\\d+)</td>")));
		status.setOjName("ZJU");
		status.setUserName(RegexUtil.getStringByRegex(searchPage,
				"<td\\s+class=\"runUserName\"><a.*?><font.*?>(.*?)</font>"));
		return status;
	}

	public static void main(String[] args) {
		String  source = "#include <stdio.h>\nint main()\n{\nint a,b;\nwhile(scanf(\"%d%d\",&a,&b) != EOF){\nprintf(\"%d\\n\",a + b);\n}\nreturn  0;\n}";
		int ret = 12023;
		for(int i = 0; i < ret; i++){
			new SpiderJudge().judge("shanghaiboy", "mycode", "1000", "1", source, "hdu");
		}
		//System.out.println(new SpiderJudge().judge("shanghaiboy", "mycode", "1000", "1", source, "hdu"));
	}
}
