package com.solisamicus.interceptor;

import com.solisamicus.exceptions.GraceException;
import com.solisamicus.grace.result.ResponseStatusEnum;
import com.solisamicus.utils.IPUtils;
import com.solisamicus.utils.RedisOperator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import static com.solisamicus.constants.Properties.MOBILE_SMSCODE_PREFIX;
import static com.solisamicus.constants.Symbols.COLON;

public class SMSInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisOperator redis;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean isExist = redis.keyIsExist(String.format("%s%s%s", MOBILE_SMSCODE_PREFIX, COLON, IPUtils.getRequestIp(request)));
        if (isExist) {
            GraceException.display(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}