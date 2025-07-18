package com.danikula.videocache;

import static com.danikula.videocache.Preconditions.checkNotNull;

import android.util.Log;

import com.danikula.videocache.file.FileCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Proxy for {@link Source} with caching support ({@link Cache}).
 * <p/>
 * Can be used only for sources with persistent data (that doesn't change with time).
 * Method {@link #read(byte[], long, int)} will be blocked while fetching data from source.
 * Useful for streaming something with caching e.g. streaming video/audio etc.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
class ProxyCache {

    private static final Logger LOG = LoggerFactory.getLogger("ProxyCache");
    private static final int MAX_READ_SOURCE_ATTEMPTS = 1;

    private final Source source;
    private final Cache cache;
    private final Object wc = new Object();
    private final Object stopLock = new Object();
    private final AtomicInteger readSourceErrorsCount;
    private volatile Thread sourceReaderThread;
    private volatile boolean stopped;
    private volatile int percentsAvailable = -1;
//    private AtomicInteger percentsAvailable = new AtomicInteger(-1);

    public ProxyCache(Source source, Cache cache) {
        this.source = checkNotNull(source);
        this.cache = checkNotNull(cache);
        this.readSourceErrorsCount = new AtomicInteger();
    }

    public int read(byte[] buffer, long offset, int length) throws ProxyCacheException {
        ProxyCacheUtils.assertBuffer(buffer, offset, length);

        while (!cache.isCompleted() && cache.available() < (offset + length) && !stopped) {
            readSourceAsync();
            waitForSourceData();
            checkReadSourceErrorsCount();
        }
        int read = cache.read(buffer, offset, length);
        if (cache.isCompleted() && percentsAvailable != 100) {
            log("read() percentsAvailable: before" + percentsAvailable);
            percentsAvailable = 100;
            log("read() percentsAvailable:" + percentsAvailable + " percentChanged:" + true);
            onCachePercentsAvailableChanged(100, true, -1);
        }
        //        if (cache.isCompleted() && percentsAvailable.get() != 100) {
//            log("read() percentsAvailable: before" + percentsAvailable);
//            percentsAvailable.set(100);
//            log("read() percentsAvailable:" + percentsAvailable + " percentChanged:" + true);
//            onCachePercentsAvailableChanged(100, true);
//        }
        return read;
    }

    private void checkReadSourceErrorsCount() throws ProxyCacheException {
        int errorsCount = readSourceErrorsCount.get();
        if (errorsCount >= MAX_READ_SOURCE_ATTEMPTS) {
            readSourceErrorsCount.set(0);
            throw new ProxyCacheException("Error reading source " + errorsCount + " times");
        }
    }

    public void shutdown() {
        synchronized (stopLock) {
            LOG.debug("Shutdown proxy for " + source);
            try {
                stopped = true;
                if (sourceReaderThread != null) {
                    sourceReaderThread.interrupt();
                }
                cache.close();
            } catch (ProxyCacheException e) {
                onError(e);
            }
        }
    }

    private synchronized void readSourceAsync() throws ProxyCacheException {
        boolean readingInProgress = sourceReaderThread != null && sourceReaderThread.getState() != Thread.State.TERMINATED;
        if (!stopped && !cache.isCompleted() && !readingInProgress) {
            sourceReaderThread = new Thread(new SourceReaderRunnable(), "Source reader for " + source);
            sourceReaderThread.start();
        }
    }

    private void waitForSourceData() throws ProxyCacheException {
        synchronized (wc) {
            try {
                wc.wait(1000);
            } catch (InterruptedException e) {
                throw new ProxyCacheException("Waiting source data is interrupted!", e);
            }
        }
    }

    private void notifyNewCacheDataAvailable(long cacheAvailable, long sourceAvailable) {
        onCacheAvailable(cacheAvailable, sourceAvailable);

        synchronized (wc) {
            wc.notifyAll();
        }
    }

    private long lastTimeMillis = 0L;
    private int calPeriod = 500;
    private long lastCacheAvailable = 0L;

    protected void onCacheAvailable(long cacheAvailable, long sourceLength) {
        boolean zeroLengthSource = sourceLength == 0;
        int percents = zeroLengthSource ? 100 : (int) ((float) cacheAvailable / sourceLength * 100);
        boolean percentsChanged = percents != percentsAvailable;
        boolean sourceLengthKnown = sourceLength >= 0;
//        if (sourceLengthKnown && percentsChanged) {
        if (sourceLengthKnown) {
            String url = "";
            if (source instanceof HttpUrlSource) {
                url = ((HttpUrlSource) source).getUrl();
            }
            float speedPerSecond = -1F;
            long currentTimeMillis = System.currentTimeMillis();
            if (lastTimeMillis == 0) {
                lastTimeMillis = currentTimeMillis;
                lastCacheAvailable = cacheAvailable;
            }
            long timeDiffer = currentTimeMillis - lastTimeMillis;
            long cacheDiffer = cacheAvailable - lastCacheAvailable;
//            if (differ >= calPeriod || (percents == 100 && differ >= 500)) {
            if (timeDiffer >= calPeriod || (percents == 100 && timeDiffer > 0)) {
                speedPerSecond = cacheDiffer / (timeDiffer / 1000F);
                lastCacheAvailable = cacheAvailable;
                lastTimeMillis = currentTimeMillis;
//                log("onCacheAvailable() speed valid :" + url + " percentsAvailable:" + percents + " percentChanged:" + percentsChanged + " speedPerSecond:" + (long) speedPerSecond + " timeDiffer:" + timeDiffer + " cacheDiffer:" + cacheDiffer);
            }
//            log("onCacheAvailable():" + url + " percentsAvailable:" + percents + " percentChanged:" + percentsChanged + " speedPerSecond:" + (long) speedPerSecond + " timeDiffer:" + timeDiffer + " cacheDiffer:" + cacheDiffer);
            onCachePercentsAvailableChanged(percents, percentsChanged, (long) speedPerSecond);
        }
        percentsAvailable = percents;
    }

    protected void onCachePercentsAvailableChanged(int percentsAvailable, boolean percentChanged, long speedPerSecond) {

    }


    private void readSource() {
        long sourceAvailable = -1;
        long offset = 0;
        try {
            offset = cache.available();
            source.open(offset);
            sourceAvailable = source.length();
            byte[] buffer = new byte[ProxyCacheUtils.DEFAULT_BUFFER_SIZE];
            int readBytes;
            FileCache fCache = (FileCache) cache;
            HttpUrlSource httpUrlSource = (HttpUrlSource) source;
//            log("cache:" + fCache.file + " length:" + fCache.file.length());
//            log("source:" + httpUrlSource.getUrl() + " sourceAvailable:" + sourceAvailable);
            while ((readBytes = source.read(buffer)) != -1) {
                synchronized (stopLock) {
                    if (isStopped()) {
                        return;
                    }
                    cache.append(buffer, readBytes);
                }
                offset += readBytes;
                notifyNewCacheDataAvailable(offset, sourceAvailable);
            }
            tryComplete();
            onSourceRead();
        } catch (Throwable e) {
            readSourceErrorsCount.incrementAndGet();
            onError(e);
        } finally {
            closeSource();
            notifyNewCacheDataAvailable(offset, sourceAvailable);
        }
    }

    private void log(String s) {
//        Log.d("ProxyCache", "Current thread:" + Thread.currentThread().getName() + "  " + s);
        Log.d("ProxyCache", s);
    }

    private void onSourceRead() {
        // guaranteed notify listeners after source read and cache completed
        int temPercent = percentsAvailable;
        int complete = 100;
        percentsAvailable = complete;
        boolean percentChanged = temPercent != complete;
        log("onSourceRead() temPercent:" + temPercent + "  percentsAvailable:" + percentsAvailable + "  percentChanged:" + percentChanged);
//        int temPercent = percentsAvailable.get();
//        int complete = 100;
//        percentsAvailable.set(complete);
//        boolean percentChanged = temPercent != complete;
//        log("onSourceRead() temPercent:" + temPercent + "  percentsAvailable:" + percentsAvailable + "  percentChanged:" + percentChanged);
        onCachePercentsAvailableChanged(complete, percentChanged, -1);
    }

    private void tryComplete() throws ProxyCacheException {
        synchronized (stopLock) {
            if (!isStopped() && cache.available() == source.length()) {
                cache.complete();
            }
        }
    }

    private boolean isStopped() {
        return Thread.currentThread().isInterrupted() || stopped;
    }

    private void closeSource() {
        try {
            source.close();
        } catch (ProxyCacheException e) {
            onError(new ProxyCacheException("Error closing source " + source, e));
        }
    }

    protected final void onError(final Throwable e) {
        boolean interruption = e instanceof InterruptedProxyCacheException;
        if (interruption) {
            LOG.debug("ProxyCache is interrupted");
        } else {
            LOG.error("ProxyCache error", e);
        }
    }

    private class SourceReaderRunnable implements Runnable {

        @Override
        public void run() {
            readSource();
        }
    }
}
