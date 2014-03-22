package com.tzc.quartjob;


import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.tzc.job.SpiderContest;
import com.tzc.utils.LogUtil;


public class ContestUpdateTaskJob implements Job {

	private Logger logger = LogUtil.getLogger();
	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
			logger.info("#比赛列表更新定时任务开始运行");
			SpiderContest updateContest = new SpiderContest();
			updateContest.updateContest();
			logger.info("#比赛列表更新定时任务运行结束");
	}

}
