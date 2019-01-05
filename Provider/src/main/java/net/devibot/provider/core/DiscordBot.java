package net.devibot.provider.core;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.devibot.core.entities.DeviGuild;
import net.devibot.core.request.RequestBuilder;
import net.devibot.provider.Config;
import net.devibot.provider.Provider;
import net.devibot.provider.listener.ReadyListener;
import net.devibot.provider.manager.AgentManager;
import net.devibot.provider.manager.MainframeManager;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;

public class DiscordBot {

    private final Logger logger = LoggerFactory.getLogger(DiscordBot.class);

    private AsyncLoadingCache<String, DeviGuild> deviGuildAsyncLoadingCache = Caffeine.newBuilder()
            .buildAsync(new AsyncCacheLoader<String, DeviGuild>() {
                @Nonnull
                @Override
                public CompletableFuture<DeviGuild> asyncLoad(@Nonnull String guildId, @Nonnull Executor executor) {
                    CompletableFuture<DeviGuild> future = new CompletableFuture<>();
                    executor.execute(() -> provider.getMainframeManager().getDeviGuild(guildId, future::complete));
                    return future;
                }
            });

    private AgentManager agentManager;

    private Provider provider;
    private ShardManager shardManager;

    private OkHttpClient okHttpClient = new OkHttpClient();

    public DiscordBot(Provider provider) {
        this.provider = provider;

        this.agentManager = new AgentManager(this);

        initialize();

        DeviGuild deviGuild = deviGuildAsyncLoadingCache.get("392264119102996480").join();
        System.out.println(deviGuild.getPrefix());
    }

    private void initialize() {
        //Create ShardManager and connect to Discord
        try {
            DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();
            builder.setToken(getConfig().getBotToken());
            builder.setAutoReconnect(true);
            builder.setHttpClient(okHttpClient);

            //listener
            builder.addEventListeners(new ReadyListener(this));

            this.shardManager = builder.build();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public RequestBuilder newRequestBuilder() {
        return new RequestBuilder(okHttpClient, getThreadPool());
    }

    public Color getColor() {
        return getConfig().isDevMode() ? Color.decode("#F48924") : Color.decode("#7289DA");
    }

    public MainframeManager getMainframeManager() {
        return this.provider.getMainframeManager();
    }

    public ScheduledExecutorService getThreadPool() {
        return this.provider.getThreadPool();
    }

    public Config getConfig() {
        return this.provider.getConfig();
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public AgentManager getAgentManager() {
        return agentManager;
    }
}
