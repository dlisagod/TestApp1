package com.danikula.videocache;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class M3u8ProxyUtil {
    private static final String TAG_MEDIA_EXT_INF = "#EXTINF";
    public static final String TAG_MEDIA_EXT_STEAM_INF = "#EXT-X-STREAM-INF";

    public static boolean isM3u8Url(String url) {
        return !TextUtils.isEmpty(url) && url.toLowerCase().endsWith(".m3u8");
    }

    public static String rewriteProxyBody(String localProxySuffix, String requestAddress, String m3u8Content) throws IOException {
        StringReader reader = new StringReader(m3u8Content);
        BufferedReader bufferReader = new BufferedReader(reader);

        String line;
        boolean mediaNextLine = false;
        StringBuilder result = new StringBuilder();
        while ((line = bufferReader.readLine()) != null) {
            line = line.trim();
            if (TextUtils.isEmpty(line))
                continue;

            /**
             * #EXTM3U
             * #EXT-X-VERSION:3           -->Constants.TAG_VERSION
             * #EXT-X-MEDIA-SEQUENCE:0    -->Constants.TAG_MEDIA_SEQUENCE
             * #EXT-X-ALLOW-CACHE:YES
             * #EXT-X-TARGETDURATION:16   -->Constants.TAG_TARGET_DURATION
             * #EXTINF:15.520000,         -->Constants.TAG_MEDIA_DURATION
             * out-0000.ts
             * #EXTINF:14.360000,
             * out-0001.ts
             * #EXT-X-ENDLIST             --> Constants.TAG_ENDLIST
             */

            if (mediaNextLine) {
                if (!line.startsWith("http")) {
                    line = requestAddress + "/" + line;
                }
                line = localProxySuffix + "/" + ProxyCacheUtils.encode(line);//拼接成代理url
            }

            mediaNextLine = !mediaNextLine && (line.startsWith(TAG_MEDIA_EXT_INF) || line.startsWith(TAG_MEDIA_EXT_STEAM_INF));
            result.append(line).append("\n");
        }
        return result.toString();
    }

    public static M3u8BodyData rewriteProxyBody2(String localProxySuffix, String requestAddress, String m3u8Content) throws IOException {
        StringReader reader = new StringReader(m3u8Content);
        BufferedReader bufferReader = new BufferedReader(reader);
        ArrayList<String> mediaList = new ArrayList();
        String line;
        boolean mediaNextLine = false;
        StringBuilder result = new StringBuilder();
        while ((line = bufferReader.readLine()) != null) {
            line = line.trim();
            if (TextUtils.isEmpty(line))
                continue;

            /**
             * #EXTM3U
             * #EXT-X-VERSION:3           -->Constants.TAG_VERSION
             * #EXT-X-MEDIA-SEQUENCE:0    -->Constants.TAG_MEDIA_SEQUENCE
             * #EXT-X-ALLOW-CACHE:YES
             * #EXT-X-TARGETDURATION:16   -->Constants.TAG_TARGET_DURATION
             * #EXTINF:15.520000,         -->Constants.TAG_MEDIA_DURATION
             * out-0000.ts
             * #EXTINF:14.360000,
             * out-0001.ts
             * #EXT-X-ENDLIST             --> Constants.TAG_ENDLIST
             */

            if (mediaNextLine) {
                if (!line.startsWith("http")) {
                    line = requestAddress + "/" + line;
                }
                line = localProxySuffix + "/" + ProxyCacheUtils.encode(line);//拼接成代理url
                mediaList.add(line);
            }
            mediaNextLine = !mediaNextLine && (line.startsWith(TAG_MEDIA_EXT_INF) || line.startsWith(TAG_MEDIA_EXT_STEAM_INF));
            result.append(line).append("\n");
        }
        M3u8BodyData data = new M3u8BodyData(result.toString(), mediaList);
        return data;
    }

    public static ArrayList<String> getM3u8TS(String m3u8Content, String requestAddress) throws IOException {
        ArrayList<String> mediaList = new ArrayList<>();
        StringReader reader = new StringReader(m3u8Content);
        BufferedReader bufferReader = new BufferedReader(reader);
        String line;
        boolean mediaNextLine = false;
        while ((line = bufferReader.readLine()) != null) {
            line = line.trim();
            if (TextUtils.isEmpty(line))
                continue;

            /**
             * #EXTM3U
             * #EXT-X-VERSION:3           -->Constants.TAG_VERSION
             * #EXT-X-MEDIA-SEQUENCE:0    -->Constants.TAG_MEDIA_SEQUENCE
             * #EXT-X-ALLOW-CACHE:YES
             * #EXT-X-TARGETDURATION:16   -->Constants.TAG_TARGET_DURATION
             * #EXTINF:15.520000,         -->Constants.TAG_MEDIA_DURATION
             * out-0000.ts
             * #EXTINF:14.360000,
             * out-0001.ts
             * #EXT-X-ENDLIST             --> Constants.TAG_ENDLIST
             */

            if (mediaNextLine) {
                if (!line.startsWith("http") && !TextUtils.isEmpty(requestAddress)) {
                    line = requestAddress + "/" + line;
                }
                mediaList.add(line);
            }
            mediaNextLine = !mediaNextLine && (line.startsWith(TAG_MEDIA_EXT_INF) || line.startsWith(TAG_MEDIA_EXT_STEAM_INF));
        }
        return mediaList;
    }

    public static class M3u8BodyData {
        public M3u8BodyData(String body, List<String> mediaList) {
            this.body = body;
            this.mediaList = mediaList;
        }

        public String body;
        public List<String> mediaList;
    }
}
