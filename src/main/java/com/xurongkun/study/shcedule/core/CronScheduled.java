package com.xurongkun.study.shcedule.core;

import java.lang.annotation.*;

/**
 * 定时任务注解
 *
 * @author xu_rongkun
 * @date 2019-10-19
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CronScheduled {
    /**
     * cron表达式
     *
     * @return
     */
    String cron() default "";

    /**
     * 描述
     *
     * @return
     */
    String desc() default "";
}
