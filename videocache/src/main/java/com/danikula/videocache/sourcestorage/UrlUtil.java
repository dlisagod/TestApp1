package com.danikula.videocache.sourcestorage;

import java.net.URL;

public class UrlUtil {
    public static String getBaseUrl(String fullUrl) throws Exception {
        URL url = new URL(fullUrl);
        String path = url.getPath();
        
        // 找到最后一个斜杠位置
        int lastIndex = path.lastIndexOf('/');
        if (lastIndex <= 0) return ""; // 路径格式异常
        
        // 重组基础URL
        return new URL(url.getProtocol(), url.getHost(), url.getPort(), 
                      path.substring(0, lastIndex)).toString();
    }

    // 简单版本（不处理协议/主机）
    public static String getSimpleBasePath(String fullUrl) {
        int lastIndex = fullUrl.lastIndexOf('/');
        return (lastIndex > 0) ? fullUrl.substring(0, lastIndex) : "";
    }
}