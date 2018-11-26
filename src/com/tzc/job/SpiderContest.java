package com.tzc.job;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.jdiy.core.Dao;
import net.jdiy.core.JDiyContext;
import net.jdiy.core.Ls;
import net.jdiy.core.Rs;
import net.jdiy.util.Fs;

import org.apache.log4j.Logger;

import com.tzc.bean.Contest;
import com.tzc.dao.TzcDao;
import com.tzc.spider.HttpClientSpider;
import com.tzc.utils.DateUtils;
import com.tzc.utils.LogUtil;
import com.tzc.utils.RegexUtil;

public class SpiderContest {

	private static Logger logger = LogUtil.getLogger();
	private static Dao dao = TzcDao.getDao("spiderinfo");
	private static String SELECT_OJ_CONTEST_URL_SQL = "select * from tb_contest where is_frozen = 0";
	private static String SELECT_REGEX_SQL = "select * from tb_regex where contest_url like '%$contest_url%'";
	private static String INSERT_CONTEST_INFO_SQL = "insert ignore into tb_contest_info"
			+ "(oj,link,name,start_time,week,access) values('$oj','$link','$name','$start_time','$week','$access')";

	public void updateContest() {
		Ls ls = dao.ls(SELECT_OJ_CONTEST_URL_SQL, 0, 1);
		for (Rs rs : ls.getItems()) {

			// 对于浙大OJ 必须在比赛内层页面进行抓取，否则有些字段匹配不到
			if ("zju".equalsIgnoreCase(rs.get("oj"))) {
				logger.info("#开始抓取 ： " + rs.get("contest_url"));
				String content = HttpClientSpider.crawl(rs.get("contest_url"),
						6000, "utf-8");
				logger.info("#结束抓取 ： " + rs.get("contest_url"));
				if (content == null)
					continue;
				Pattern pattern = Pattern.compile(
						"(onlinejudge/contestInfo.do\\?contestId=\\d+)",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
				Matcher matcher = pattern.matcher(content);
				while (matcher.find()) {
					String url = rs.get("contest_url_base") + matcher.group(1);
					getContestIntoDB(url, rs.get("contest_url_base"), rs
							.get("oj"), rs.get("charset"));
				}
			} else {
				getContestIntoDB(rs.get("contest_url"), rs
						.get("contest_url_base"), rs.get("oj"), rs
						.get("charset"));
			}

		}
	}

	private void getContestIntoDB(String url, String urlBase, String oj,
			String charset) {
		Map<String, String> regexMap = new HashMap<String, String>();
		logger.info("#开始抓取 ： " + url);
		String content = spiderContest(url, charset);
		logger.info("#结束抓取 ： " + url);
		Ls ls = dao.ls(SELECT_REGEX_SQL.replace("$contest_url", urlBase), 0, 1);
		for (Rs rs : ls.getItems()) {
			regexMap.put(rs.get("regex_name"), rs.get("regex"));
		}
		List<Contest> contests = getContestInfo(content, regexMap, urlBase);
		if (contests == null)
			return;

		for (Contest tmp : contests) {
			if (tmp.getOj() == null)
				tmp.setOj(oj);

			// 对于浙大OJ 必须在比赛内层页面进行抓取，否则有些字段匹配不到
			if ("zju".equalsIgnoreCase(oj)) {
				tmp.setLink(url);
				tmp.setAccess("Public");
			}
			String sql = INSERT_CONTEST_INFO_SQL.replace("$oj", tmp.getOj())
					.replace("$link", tmp.getLink()).replace("$name",
							tmp.getName()).replace("$start_time",
							tmp.getStart_time().toLocaleString()).replace(
							"$week", tmp.getWeek()).replace("$access",
							tmp.getAccess());
			dao.exec(sql);
		}
	}

	private String spiderContest(String url, String charset) {
		// 重试次数3次
		int retry = 3;
		String content = null;
		int cnt = 0;
		while (retry != 0) {
			logger.info("#重试第 " + cnt + " 次抓取");
			content = HttpClientSpider.crawl(url, 6000, charset);
			if (content != null)
				return content;
			retry--;
		}
		return null;
	}

	private List<Contest> getContestInfo(String content,
			Map<String, String> regexMap, String urlBase) {
		List<Contest> contests = new ArrayList<Contest>();
		Pattern pattern = Pattern.compile(regexMap.get("contest_block"),
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		System.out.println(content);
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			pattern = Pattern.compile(regexMap.get("contest_list"),
					Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
			matcher = pattern.matcher(matcher.group(1));
		} else {
			return null;
		}
		int cnt = 1;
		while (matcher.find()) {
			Contest contest = new Contest();
			String tmp = matcher.group(1);
			String contestName = RegexUtil.getStringByRegex(tmp,
					regexMap.get("contest_name")).replaceAll("<.*?>", "")
					.trim();
			String startTime = RegexUtil.getStringByRegex(tmp, regexMap
					.get("contest_start_time"));
			String link = RegexUtil.getStringByRegex(tmp, regexMap
					.get("contest_link"));
			link = link == null ? "" : link;
			String isPrivate = RegexUtil.getStringByRegex(tmp, regexMap
					.get("contest_is_private"));
			isPrivate = isPrivate == null ? "" : isPrivate;

			// 对于正确的竞赛URL 不作处理
			if (link.indexOf("http") == -1) {
				if (link.length() > 0 && link.charAt(0) == '/') {
					link = link.substring(1);
				}
				if (urlBase != null) {
					link = urlBase + link;
				}
			}
			String oj = null;

			// 此处抓取HDU recentcontest列表页，对之前独立抓取的竞赛信息不进行抓取。
			if ("http://acm.hdu.edu.cn/recentcontest/".equals(urlBase)) {
				oj = RegexUtil.getStringByRegex(tmp,
						"<b>note</b>.*?\\s+<td>(.*?)</td>");
				if ("HDU".equalsIgnoreCase(oj)) {
					logger.info("#HDU Contest 已经抓过，此处跳过");
					continue;
				}
				if ("ZJU".equalsIgnoreCase(oj)) {
					logger.info("#ZJU Contest 已经抓过，此处跳过");
					continue;
				}
				if ("FZU".equalsIgnoreCase(oj)) {
					logger.info("#FZU Contest 已经抓过，此处跳过");
					continue;
				}
			}
			Date date = DateUtils.string2Date(startTime);
			contest.setName(contestName);
			contest.setLink(link);
			contest.setOj(oj);
			contest.setAccess(isPrivate != "" ? "Private" : "Public");
			contest.setStart_time(date);
			contest.setWeek(date.toString().substring(0,
					date.toString().indexOf(" ")));
			contests.add(contest);
		}
		return contests;
	}

	public static void main(String[] args) {
		new SpiderContest().updateContest();
	}
}
