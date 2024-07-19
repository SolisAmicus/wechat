package com.solisamicus.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Slf4j
@Aspect
public class ServiceLogAspect {
    @Around("execution(* com.solisamicus.service.impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {
        String pointName = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName();
        StopWatch stopWatch = new StopWatch("Service stopwatch");
        stopWatch.start(pointName);
        Object result=joinPoint.proceed();;
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
        return result;
    }
}
