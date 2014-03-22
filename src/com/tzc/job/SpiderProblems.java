package com.tzc.job;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.jdiy.core.Dao;
import net.jdiy.core.Ls;
import net.jdiy.core.Rs;
import net.jdiy.util.Fn.interval;

import org.apache.log4j.Logger;

import sun.security.timestamp.TSRequest;

import com.tzc.dao.TzcDao;
import com.tzc.spider.HttpClientSpider;
import com.tzc.utils.DateUtils;
import com.tzc.utils.LogUtil;
import com.tzc.utils.RegexUtil;
import com.tzc.utils.Utils;

public class SpiderProblems {
	private static Logger logger = LogUtil.getLogger();
	private static Dao dao = TzcDao.getDao("spiderinfo");
	private static String SELECT_MAX_PROBLEM_ID = "select max(problem_id) as max_id from tb_problem where oj like '%$oj%'";

	private void spiderProblemByZju() {
		logger.info("#开始进行ZJU题库更新");
		String homePageUrl = "http://acm.zju.edu.cn/onlinejudge/showProblemsets.do";
		String homePage = HttpClientSpider.crawl(homePageUrl, 3000, "utf-8");
		int maxPage = 0;
		int maxProblemId = 0;
		Pattern pattern = Pattern.compile("<a\\s+href=.*?>Vol\\s+(\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matcher = pattern.matcher(homePage);
		while (matcher.find()) {
			maxPage = Integer.parseInt(matcher.group(1));
		}
		String lastProblemPageUrl = "http://acm.zju.edu.cn/onlinejudge/showProblems.do"
				+ "?contestId=1&pageNumber={pageNumber}".replace(
						"{pageNumber}", "" + maxPage);
		String lastProblemPageContent = HttpClientSpider.crawl(
				lastProblemPageUrl, 3000, "utf-8");
		pattern = Pattern.compile("<font\\s+color=\"blue\">(\\d+)</font>",
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		matcher = pattern.matcher(lastProblemPageContent);
		while (matcher.find()) {
			maxProblemId = Integer.parseInt(matcher.group(1));
		}
		int beginProblemId = 1000;
		Ls ls = dao.ls(SELECT_MAX_PROBLEM_ID.replace("$oj", "ZJU"), 0, 1);
		Rs[] rsArray = ls.getItems();
		String beginProblemIdString = rsArray[0].get("max_id");
		if (!"".equals(beginProblemIdString)) {
			beginProblemId = Integer.parseInt(beginProblemIdString);
		}
		for (int i = beginProblemId + 1; i <= maxProblemId; i++) {
			String problemUrl = "http://acm.zju.edu.cn/onlinejudge/showProblem.do?problemCode={pid}";
			String content = HttpClientSpider.crawlRetry(problemUrl.replace(
					"{pid}", "" + i), "utf-8", 3);
			String timeLimitString = RegexUtil.getStringByRegex(content,
					"Time\\s+Limit.*?(\\d+)");
			int timeLimit = timeLimitString != null ? Integer
					.parseInt(timeLimitString) : -1;
			String memoryLimitString = RegexUtil.getStringByRegex(content,
					"Memory\\s+Limit.*?(\\d+)");
			String title = RegexUtil.getStringByRegex(content,
					"bigProblemTitle\">(.*?)</span></center>");
			title = title != null ? title : "";
			int memoryLimit = memoryLimitString != null ? Integer
					.parseInt(memoryLimitString) : -1;
			String sp = RegexUtil.getStringByRegex(content,
					"(Special\\s+Judge)");
			int isSpJudge = sp != null ? 1 : 0;
			String problemContent = RegexUtil.getStringByRegex(content,
					"<hr>[\\s|\\S]*?<hr>([\\s|\\S]*?)<hr>");
			if (problemContent == null)
				continue;
			problemContent = problemContent.replaceAll("<img\\s+src=\"",
					"<img src=\"http://acm.zju.edu.cn/onlinejudge/");
			System.out.println("----------------------------------");
			System.out.println(problemContent);
			System.out.println("----------------------------------");
			Rs rs = new Rs("tb_problem");
			rs.set("oj", "ZJU").set("problem_id", i).set("time_limit",
					timeLimit).set("memory_limit", memoryLimit).set(
					"problem_content", problemContent).set("is_special_judge",
					isSpJudge).set("title", title).set("spider_time",
					DateUtils.date2String(new Date())).set("spider_url",
					problemUrl.replace("{pid}", i + ""));
			dao.save(rs);
			logger.info("#插入题号为" + i + "的题目，插入成功");
		}
		logger.info("#完成ZJU题库更新");
	}

	public static void main(String[] args) {
		new SpiderProblems().spiderProblemByZju();// 检查ZJU题库，并插入新题目
	}
}
