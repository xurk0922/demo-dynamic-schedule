package com.xurongkun.study.shcedule.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * @author xurongkun
 * @version 1.0
 * @date 2019-10-19
 */
@Component
@Slf4j
public class ScheduleTask implements BeanPostProcessor, DisposableBean {

    private final ScheduledTaskRegistrar scheduledTaskRegistrar = new ScheduledTaskRegistrar();

    private final ThreadFactory threadFactory = new CustomizableThreadFactory("d-demo");

    private final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(50, threadFactory);

    private static boolean init = false;

    private final ScheduleJob scheduleJob;

    public ScheduleTask(ScheduleJob scheduleJob) {
        this.scheduleJob = scheduleJob;
    }

    @Override
    public void destroy() throws Exception {
        scheduledTaskRegistrar.destroy();
    }

    public void configureTasks() {
        // 通过反射拿到目标方法
        Class<? extends ScheduleJob> jobClass = this.scheduleJob.getClass();
        Method[] methods = jobClass.getMethods();
        for (Method method : methods) {
            CronScheduled annotation = method.getAnnotation(CronScheduled.class);
            if (null == annotation) {
                continue;
            }

            String cron = annotation.cron();

            // 创建定时执行目标方法的线程
            Runnable runnable = new ScheduledMethodRunnable(this.scheduleJob, method);

            // 为定时任务注册器添加触发任务
            scheduledTaskRegistrar.addTriggerTask(new TriggerTask(runnable, triggerContext -> {
                CronTrigger cronTrigger = new CronTrigger(cron);

                return cronTrigger.nextExecutionTime(triggerContext);
            }));

            scheduledTaskRegistrar.setScheduler(this.executorService);
            scheduledTaskRegistrar.afterPropertiesSet();
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!init) {
            configureTasks();
            init = true;
        }

        return bean;
    }
}
