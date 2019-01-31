package net.devibot.provider.cache;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.devibot.core.entities.User;
import net.devibot.provider.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class UserCache {

    private Logger logger = LoggerFactory.getLogger(UserCache.class);

    private AsyncLoadingCache<String, User> userAsyncLoadingCache = Caffeine.newBuilder().expireAfterWrite(2, TimeUnit.MINUTES).buildAsync(getAsyncCacheLoader());

    private Provider provider;

    public UserCache(Provider provider) {
        this.provider = provider;
    }

    public User getUser(String userId) {
        try {
            return userAsyncLoadingCache.get(userId).get();
        } catch (Exception e) {
            logger.error("", e);
            return new User();
        }
    }

    private AsyncCacheLoader<String, User> getAsyncCacheLoader() {
        return (userId, executor) -> {
            CompletableFuture<User> future = new CompletableFuture<>();
            executor.execute(() -> provider.getMainframeManager().getUser(userId, future::complete));
            return future;
        };
    }
}
