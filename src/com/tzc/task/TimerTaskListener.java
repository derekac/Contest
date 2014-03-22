package com.tzc.task;

import java.text.ParseException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.tzc.quartjob.ContestUpdateTaskJob;
import com.tzc.utils.LogUtil;
import com.tzc.utils.Utils;

public class TimerTaskListener implements ServletContextListener{
	private Logger logger = LogUtil.getLogger();
	private Scheduler sched = null;


	public void contextInitialized(ServletContextEvent arg0) {
			logger.info("#Web 监听类加载成功");
			initQuartz();		//初始化调度器
			contestListUpdate();
	}
	
	public void contextDestroyed(ServletContextEvent sce) {
		try {
			if (sched != null) {
				sched.shutdown();
			}
		} catch (SchedulerException e) {
			logger.error("#定时任务销毁出错！", e);
		}
	}
	private void initQuartz() {
		try {
			sched = new StdSchedulerFactory().getScheduler();
			sched.start();
		} catch (SchedulerException e) {
			logger.error("#定时任务初始化出错！", e);
		}
	}
	private void contestListUpdate() {
		try {
			String crontabJobTimer = Utils.getValueByIDFromXML("contestUpdateTaskTimer");
			JobDetail job = JobBuilder.newJob(ContestUpdateTaskJob.class).build();
			Trigger trigger = getTrigger(crontabJobTimer);
			sched.scheduleJob(job, trigger);
		} catch (Exception e) {
			logger.error("#定时抓取任务出错！", e);
		}
	}
	
	private Trigger getTrigger(String cronExpression) throws ParseException {
		return TriggerBuilder.newTrigger()
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.startNow().build();
	}
	public static void main(String[] args) {
		String crontabJobTimer = Utils.getValueByIDFromXML("contestUpdateTaskTimer");
		System.out.println(crontabJobTimer);
	}
}