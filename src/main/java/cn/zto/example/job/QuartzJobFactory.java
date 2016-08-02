package cn.zto.example.job;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import cn.zto.example.model.SysJobInfo;

public class QuartzJobFactory implements Job {
	public final Logger log = LoggerFactory.getLogger(this.getClass());

	public void execute(JobExecutionContext context) throws JobExecutionException {
		SysJobInfo sysJobInfo = (SysJobInfo) context.getMergedJobDataMap().get("SysJobInfo");
		Object object = null;
		@SuppressWarnings("rawtypes")
		Class clazz = null;
		if (StringUtils.isEmpty(sysJobInfo.getSjiSpringIdV())) {
			throw new RuntimeException("");
		}

		object = SpringContextHolder.getBean(sysJobInfo.getSjiSpringIdV());
		if (object == null) {
			log.error("任务名称 = [" + sysJobInfo.getSjiCodeV() + "]---------------未启动成功，请检查是否配置正确！！！");
			return;
		}
		clazz = object.getClass();
		Method method = null;
		try {
			method = clazz.getDeclaredMethod(sysJobInfo.getSjiMethodNameV());
		} catch (NoSuchMethodException e) {
			log.error("任务名称 = [" + sysJobInfo.getSjiMethodNameV() + "]---------------未启动成功，方法名设置错误！！！");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (method != null) {
			try {
				method.invoke(object);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}