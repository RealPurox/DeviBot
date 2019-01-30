package net.devibot.provider.cache;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.devibot.core.entities.DeviGuild;
import net.devibot.core.entities.Strike;
import net.devibot.provider.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class StrikeCache {

    private Logger logger = LoggerFactory.getLogger(StrikeCache.class);

    private AsyncLoadingCache<Map.Entry<String, String>/*<user,guild>*/, List<Strike>> userGuildStrikeListAsyncLoadingCache = Caffeine.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).buildAsync(getAsyncCacheLoader());

    private Provider provider;

    public StrikeCache(Provider provider) {
        this.provider = provider;
    }

    public List<Strike> getDeviGuild(String user, String guild) {
        try {
            return userGuildStrikeListAsyncLoadingCache.get(new AbstractMap.SimpleEntry<>(user, guild)).get();
        } catch (Exception e) {
            logger.error("", e);
            return new ArrayList<>();
        }
    }

    private AsyncCacheLoader<Map.Entry<String, String>, List<Strike>> getAsyncCacheLoader() {
        return (entry, executor) -> {
            CompletableFuture<List<Strike>> future = new CompletableFuture<>();
            executor.execute(() -> provider.getMainframeManager().getStrikes(entry.getKey(), entry.getValue(), future::complete));
            return future;
        };
    }

}
