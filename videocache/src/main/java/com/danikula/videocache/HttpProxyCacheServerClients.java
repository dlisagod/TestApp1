package com.danikula.videocache;

import static com.danikula.videocache.Preconditions.checkNotNull;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.danikula.videocache.file.FileCache;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Client for {@link HttpProxyCacheServer}
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
final class HttpProxyCacheServerClients {

    private final AtomicInteger clientsCount = new AtomicInteger(0);
    private final String url;
    private volatile HttpProxyCache proxyCache;
    private final List<CacheListener> listeners = new CopyOnWriteArrayList<>();
    private final CacheListener uiCacheListener;
    private final Config config;

    public HttpProxyCacheServerClients(String url, Config config) {
        this.url = checkNotNull(url);
        this.config = checkNotNull(config);
        this.uiCacheListener = new UiListenerHandler(url, listeners);
    }

    public void processRequest(GetRequest request, Socket socket) throws ProxyCacheException, IOException {
        startProcessRequest();
        try {
            clientsCount.incrementAndGet();
            proxyCache.processRequest(request, socket);
        } finally {
            finishProcessRequest();
        }
    }

    private synchronized void startProcessRequest() throws ProxyCacheException {
        proxyCache = proxyCache == null ? newHttpProxyCache() : proxyCache;
    }

    private synchronized void finishProcessRequest() {
        if (clientsCount.decrementAndGet() <= 0) {
            proxyCache.shutdown();
            proxyCache = null;
        }
    }

    public void registerCacheListener(CacheListener cacheListener) {
        listeners.add(cacheListener);
    }

    public void unregisterCacheListener(CacheListener cacheListener) {
        listeners.remove(cacheListener);
    }

    public void shutdown() {
        listeners.clear();
        if (proxyCache != null) {
            proxyCache.registerCacheListener(null);
            proxyCache.shutdown();
            proxyCache = null;
        }
        clientsCount.set(0);
    }

    public int getClientsCount() {
        return clientsCount.get();
    }

    private HttpProxyCache newHttpProxyCache() throws ProxyCacheException {
        HttpUrlSource source = new HttpUrlSource(url, config.sourceInfoStorage, config.headerInjector);
        FileCache cache = new FileCache(config.generateCacheFile(url), config.diskUsage);
        HttpProxyCache httpProxyCache = new HttpProxyCache(source, cache);
        httpProxyCache.registerCacheListener(uiCacheListener);
        return httpProxyCache;
    }

    private static final class UiListenerHandler extends Handler implements CacheListener {
        private final int CHANGED = 1;
        private final int NOT_CHANGED = 0;
        private final String url;
        private final List<CacheListener> listeners;

        public UiListenerHandler(String url, List<CacheListener> listeners) {
            super(Looper.getMainLooper());
            this.url = url;
            this.listeners = listeners;
        }

        @Override
        public void onCacheAvailable(File file, String url, int percentsAvailable, boolean percentChanged, long speedPerSecond) {
            Message message = obtainMessage();
            message.arg1 = percentsAvailable;
            message.arg2 = percentChanged ? CHANGED : NOT_CHANGED;
            message.obj = new FileSpeed(speedPerSecond, file);
            sendMessage(message);
        }

        @Override
        public void handleMessage(Message msg) {
            FileSpeed fs = (FileSpeed) msg.obj;
            int percents = msg.arg1;
            boolean percentsChanged = msg.arg2 == CHANGED;
//            Log.d(getClass().getSimpleName(), "handleMessage:" + String.format("file:%s percents:%d, percentChanged:%b", file.getAbsoluteFile(), percents, percentsChanged));
//            Log.d(getClass().getSimpleName(), "handleMessage:" + String.format("listeners.size:%d ", listeners.size()));
            for (CacheListener cacheListener : listeners) {
                cacheListener.onCacheAvailable(fs.file, url, percents, percentsChanged, fs.speedPerSecond);
            }
        }

        private static class FileSpeed {
            long speedPerSecond;
            File file;

            public FileSpeed(long speedPerSecond, File file) {
                this.speedPerSecond = speedPerSecond;
                this.file = file;
            }
        }
    }
}
