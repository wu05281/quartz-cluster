package cn.zto.example;

import java.lang.reflect.Method;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;


/** 
 * Spring调度任务 
 * @author liuyazhuang 
 * 
 */  
public class MyDetailQuartzJobBean extends QuartzJobBean{  
	
	private ApplicationContext applicationContext;   

	private String targetObject; 

	private String targetMethod; 

	public String getTargetObject() { 
		return targetObject; 
	} 


	public void setTargetObject(String targetObject) { 
		this.targetObject = targetObject; 
	} 

	public String getTargetMethod() { 
		return targetMethod; 
	} 

	public void setTargetMethod(String targetMethod) { 
		this.targetMethod = targetMethod; 
	} 


	public ApplicationContext getApplicationContext() {  
		return applicationContext; 
	} 

	/** 
	 * 从SchedulerFactoryBean注入的applicationContext. 
	 */   
	public void setApplicationContext(ApplicationContext applicationContext) {   
		this.applicationContext = applicationContext;   
	}   

	@Override   
	protected void executeInternal(JobExecutionContext ctx) 
			throws JobExecutionException { 
		try { 


			// logger.info("execute [" + targetObject + "] at once>>>>>>"); 
			Object otargetObject = applicationContext.getBean(targetObject); 
			Method m = null; 
			try { 
				m = otargetObject.getClass().getMethod(targetMethod, 
						new Class[] {}); 
				m.invoke(otargetObject, new Object[] {}); 
			} catch (SecurityException e) { 
				// logger.error(e); 
			} catch (NoSuchMethodException e) { 
				// logger.error(e); 
			} 


		} catch (Exception e) { 
			throw new JobExecutionException(e); 
		} 
	} 
}  