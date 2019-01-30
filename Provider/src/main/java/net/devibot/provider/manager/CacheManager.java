package net.devibot.provider.manager;

import net.devibot.provider.Provider;
import net.devibot.provider.cache.DeviGuildCache;
import net.devibot.provider.cache.StrikeCache;

public class CacheManager {

    private DeviGuildCache deviGuildCache;
    private StrikeCache strikeCache;

    public CacheManager(Provider provider) {
        this.deviGuildCache = new DeviGuildCache(provider);
    }

    public DeviGuildCache getDeviGuildCache() {
        return deviGuildCache;
    }
}
