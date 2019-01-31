package net.devibot.provider.manager;

import net.devibot.provider.Provider;
import net.devibot.provider.cache.DeviGuildCache;
import net.devibot.provider.cache.UserCache;

public class CacheManager {

    private DeviGuildCache deviGuildCache;
    private UserCache userCache;

    public CacheManager(Provider provider) {
        this.deviGuildCache = new DeviGuildCache(provider);
        this.userCache = new UserCache(provider);
    }

    public DeviGuildCache getDeviGuildCache() {
        return deviGuildCache;
    }

    public UserCache getUserCache() {
        return userCache;
    }
}
