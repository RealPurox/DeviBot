package net.devibot.provider.cache;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.devibot.core.entities.DeviGuild;
import net.devibot.provider.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class DeviGuildCache {

    private Logger logger = LoggerFactory.getLogger(DeviGuildCache.class);

    private AsyncLoadingCache<String, DeviGuild> deviGuildAsyncLoadingCache = Caffeine.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).buildAsync(getAsyncCacheLoader());

    private Provider provider;

    public DeviGuildCache(Provider provider) {
        this.provider = provider;
    }

    public DeviGuild getDeviGuild(String id) {
        try {
            return deviGuildAsyncLoadingCache.get(id).get();
        } catch (Exception e) {
            logger.error("", e);
            return new DeviGuild(id);
        }
    }

    private AsyncCacheLoader<String, DeviGuild> getAsyncCacheLoader() {
        return (guildId, executor) -> {
            CompletableFuture<DeviGuild> future = new CompletableFuture<>();
            executor.execute(() -> provider.getMainframeManager().getDeviGuild(guildId, future::complete));
            return future;
        };
    }
}
