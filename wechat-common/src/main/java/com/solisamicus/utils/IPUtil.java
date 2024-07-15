package com.solisamicus.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * IP 工具类: 从 HttpServletRequest 和 ServerHttpRequest 中获取客户端IP地址
 */
public class IPUtil {
    // 未知 IP 地址常量
    private static final String IP_UNKNOWN = "unknown";
    // 本地 IP 地址常量
    private static final String IP_LOCAL = "127.0.0.1";
    // IP 地址最大长度
    private static final int IP_LEN = 15;
    // 可能包含 IP 地址的请求头列表
    private static final List<String> IP_HEADERS = Arrays.asList(
            "x-forwarded-for",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
    );

    /**
     * 从 HttpServletRequest 的请求头中获取 IP 地址
     *
     * @param request HttpServletRequest
     * @return
     */
    private static String getHeaderIp(HttpServletRequest request) {
        return IP_HEADERS.stream()
                .map(request::getHeader)
                .filter(ip -> ip != null && !ip.isEmpty() && !IP_UNKNOWN.equalsIgnoreCase(ip))
                .findFirst()
                .orElse(null);
    }

    /**
     * 从 HttpServletRequest 获取 IP 地址
     *
     * @param request HttpServletRequest
     * @return
     */
    public static String getRequestIp(HttpServletRequest request) {
        String ip = getHeaderIp(request);
        return (ip != null) ? ip : request.getRemoteAddr();
    }

    /**
     * 从 ServerHttpRequest 的请求头中获取IP地址
     *
     * @param request ServerHttpRequest
     * @return
     */
    private static String getHeaderIp(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        return IP_HEADERS.stream()
                .map(headers::getFirst)
                .filter(ip -> ip != null && !ip.isEmpty() && !IP_UNKNOWN.equalsIgnoreCase(ip))
                .findFirst()
                .orElse(null);
    }

    /**
     * 从 ServerHttpRequest 获取IP地址
     *
     * @param request ServerHttpRequest
     * @return
     */
    public static String getIP(ServerHttpRequest request) {
        String ipAddress = getHeaderIp(request);
        if (ipAddress == null) {
            ipAddress = Optional.ofNullable(request.getRemoteAddress())
                    .map(address -> address.getAddress().getHostAddress())
                    .orElse("");
            if (IP_LOCAL.equals(ipAddress)) {
                try {
                    InetAddress inet = InetAddress.getLocalHost();
                    ipAddress = inet.getHostAddress();
                } catch (UnknownHostException e) {
                    // ignore
                }
            }
        }
        if (ipAddress != null && ipAddress.length() > IP_LEN) {
            int index = ipAddress.indexOf(",");
            if (index > 0) {
                ipAddress = ipAddress.substring(0, index);
            }
        }
        return ipAddress;
    }
}