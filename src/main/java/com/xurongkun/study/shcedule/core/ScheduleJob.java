package com.xurongkun.study.shcedule.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 任务类
 * 任务在此中被定时调用
 *
 * @author xurongkun
 * @version 1.0
 * @date 2019-10-19
 */
@Component
@Slf4j
public class ScheduleJob {


    @CronScheduled(cron = "0/1 * * * * ? ", desc = "从0秒开始每1秒执行一次")
    public void job1() {
        log.info("执行job1");
    }

    @CronScheduled(cron = "0/10 * * * * ? ", desc = "从0秒开始每10秒执行一次")
    public void job2() {
        log.info("执行job2");
    }
}
