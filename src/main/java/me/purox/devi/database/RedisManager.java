package me.purox.devi.database;

import me.purox.devi.core.Devi;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisDataException;

public class RedisManager {

    private Devi devi;
    private JedisPubSub pubSub;
    private Jedis sender;

    public RedisManager(Devi devi) {
        this.devi = devi;
        this.pubSub = new DeviRedisPubSub(devi);

        devi.getThreadPool().submit(() -> {
            try {
                sender = new Jedis("54.38.182.128");
                sender.auth(devi.getSettings().getDeviAPIAuthorization());

                Jedis receiverRedis = new Jedis("54.38.182.128");
                receiverRedis.auth(devi.getSettings().getDeviAPIAuthorization());
                receiverRedis.subscribe(pubSub, "devi_update", "devi_twitch_event");
            } catch (JedisDataException e) {
                e.printStackTrace();
            }
        });
    }

    public boolean isConnected() {
        return sender.isConnected();
    }

    public Jedis getSender() {
        return sender;
    }
}
