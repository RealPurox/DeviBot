package net.devibot.provider.manager;

import net.devibot.provider.Provider;
import net.devibot.provider.cache.DeviGuildCache;

public class CacheManager {

    private DeviGuildCache deviGuildCache;

    public CacheManager(Provider provider) {
        this.deviGuildCache = new DeviGuildCache(provider);
    }

    public DeviGuildCache getDeviGuildCache() {
        return deviGuildCache;
    }
}
