package cn.zto.example.job;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import cn.zto.example.dao.base.BaseDao4Quartz;
import cn.zto.example.model.SysJobInfo;

/**
 * TODO
 */
@Service
public class SysJobInfoService {
	public final Logger log = LoggerFactory.getLogger(getClass());

	public final static int STOP = 0;

	public final static int START = 1;

	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	private BaseDao4Quartz baseDao;
	
	@Resource(name="sf_quartz")
	private SessionFactory sessionFactory;

	/**
	 * 从数据库中取 区别于getAllJob
	 * 
	 * @return
	 */
	public List<SysJobInfo> queryAll() {
		return (List<SysJobInfo>) baseDao.find("from SysJobInfo");
	}

	/**
	 * 添加到数据库中 区别于addJob
	 * 
	 * @throws SchedulerException
	 */
	public void create(SysJobInfo job) throws SchedulerException {
		baseDao.create(job);
		addJob(job);
	}

	/**
	 * 从数据库中查询job
	 */
	public SysJobInfo getTaskById(int id) {
		return baseDao.findSingle("from SysJobInfo where sjiIdN = ?", id);
	}

	/**
	 * 更改任务状态
	 * 
	 * @throws SchedulerException
	 */
	public void doChangeStatus(int jobId, int cmd) throws SchedulerException {
		SysJobInfo job = getTaskById(jobId);
		if (job == null) {
			return;
		}

		switch (cmd) {
		case START:
			addJob(job);
			job.setSjiStatusN(START);
			break;
		case STOP:
			deleteJob(job);
			job.setSjiStatusN(STOP);
			break;

		default:
			break;
		}
		baseDao.update(job);
	}

	/**
	 * 更改任务 cron表达式
	 * 
	 * @throws SchedulerException
	 */
	public void update(SysJobInfo job) throws SchedulerException {
		if (job == null) {
			return;
		}
		if (START == job.getSjiStatusN()) {
			updateJobCron(job);
		}
		baseDao.update(job);
	}

	/**
	 * 更改任务 cron表达式
	 * 
	 * @throws SchedulerException
	 */
	public void updateCron(int id, String cron) throws SchedulerException {
		SysJobInfo job = getTaskById(id);
		if (job == null) {
			return;
		}
		job.setSjiCronV(cron);

		if (START == job.getSjiStatusN()) {
			updateJobCron(job);
		}
		baseDao.update(job);
	}

	public void doNowExecute(int id) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		SysJobInfo job = getTaskById(id);
		Object obj = SpringContextHolder.getBean(job.getSjiSpringIdV());
		Method method = obj.getClass().getMethod(job.getSjiMethodNameV(), null);
		method.invoke(obj, null);
	}

	/**
	 * 添加任务
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	public void addJob(SysJobInfo job) throws SchedulerException {
		if (job == null) {
			return;
		}

		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		log.debug(scheduler
				+ ".......................................................................................add");
		TriggerKey triggerKey = TriggerKey.triggerKey(job.getSjiCodeV());

		CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

		// 不存在，创建一个
		if (null == trigger) {
			Class clazz = QuartzJobFactory.class;

			JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(job.getSjiCodeV()).build();

			jobDetail.getJobDataMap().put("SysJobInfo", job);

			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getSjiCronV());

			trigger = TriggerBuilder.newTrigger().withIdentity(job.getSjiCodeV()).withSchedule(scheduleBuilder).build();

			scheduler.scheduleJob(jobDetail, trigger);
		} else {
			// Trigger已存在，那么更新相应的定时设置
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getSjiCronV());

			// 按新的cronExpression表达式重新构建trigger
			trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

			// 按新的trigger重新设置job执行
			scheduler.rescheduleJob(triggerKey, trigger);
		}
		log.info("job[{}] add>>>>>>>>>> ", job.getSjiCodeV());

	}

	@PostConstruct
	public void init() throws Exception {
		Session session = sessionFactory.openSession();
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
		Scheduler scheduler = schedulerFactoryBean.getScheduler();

		// 这里获取任务信息数据
		List<SysJobInfo> jobList = (List<SysJobInfo>) baseDao.find("from SysJobInfo");

		for (SysJobInfo job : jobList) {
			if (START == job.getSjiStatusN()) {
				addJob(job);
			}
		}

		SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
		SessionFactoryUtils.closeSession(sessionHolder.getSession());
	}

	/**
	 * 获取所有计划中的任务列表
	 * 
	 * @return
	 * @throws SchedulerException
	 */
	public List<SysJobInfo> getAllJob() throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
		Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
		List<SysJobInfo> jobList = new ArrayList<SysJobInfo>();
		for (JobKey jobKey : jobKeys) {
			List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
			for (Trigger trigger : triggers) {
				SysJobInfo job = new SysJobInfo();
				job.setSjiCodeV(jobKey.getName());
				Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
				// TODO job.setJobStatus(triggerState.name());
				if (trigger instanceof CronTrigger) {
					CronTrigger cronTrigger = (CronTrigger) trigger;
					String cronExpression = cronTrigger.getCronExpression();
					job.setSjiCronV(cronExpression);
				}
				jobList.add(job);
			}
		}
		return jobList;
	}

	/**
	 * 所有正在运行的job
	 * 
	 * @return
	 * @throws SchedulerException
	 */
	public List<SysJobInfo> getRunningJob() throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
		List<SysJobInfo> jobList = new ArrayList<SysJobInfo>(executingJobs.size());
		for (JobExecutionContext executingJob : executingJobs) {
			SysJobInfo job = new SysJobInfo();
			JobDetail jobDetail = executingJob.getJobDetail();
			JobKey jobKey = jobDetail.getKey();
			Trigger trigger = executingJob.getTrigger();
			job.setSjiCodeV(jobKey.getName());

			Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
			// job.setJobStatus(triggerState.name());
			if (trigger instanceof CronTrigger) {
				CronTrigger cronTrigger = (CronTrigger) trigger;
				String cronExpression = cronTrigger.getCronExpression();
				job.setSjiCronV(cronExpression);
			}
			jobList.add(job);
		}
		return jobList;
	}

	/**
	 * 暂停一个job
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	public void pauseJob(SysJobInfo job) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = JobKey.jobKey(job.getSjiCodeV());
		scheduler.pauseJob(jobKey);
		log.info("job[{}] pause>>>>>>>>>> ", job.getSjiCodeV());
	}

	/**
	 * 恢复一个job
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	public void resumeJob(SysJobInfo job) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = JobKey.jobKey(job.getSjiCodeV());
		scheduler.resumeJob(jobKey);
		log.info("job[{}] resume>>>>>>>>>> ", job.getSjiCodeV());
	}

	/**
	 * 删除一个job
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	public void deleteJob(SysJobInfo job) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = JobKey.jobKey(job.getSjiCodeV());
		scheduler.deleteJob(jobKey);
		log.info("job[{}] stop>>>>>>>>>> ", job.getSjiCodeV());
	}

	/**
	 * 立即执行job
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	public void runAJobNow(SysJobInfo job) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = JobKey.jobKey(job.getSjiCodeV());
		scheduler.triggerJob(jobKey);
		log.info("job[{}] start>>>>>>>>>> ", job.getSjiCodeV());
	}

	/**
	 * 更新job时间表达式
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	public void updateJobCron(SysJobInfo job) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();

		TriggerKey triggerKey = TriggerKey.triggerKey(job.getSjiCodeV());

		CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getSjiCronV());

		trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

		scheduler.rescheduleJob(triggerKey, trigger);
	}

	public static void main(String[] args) {
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("xxxxx");
	}
}
