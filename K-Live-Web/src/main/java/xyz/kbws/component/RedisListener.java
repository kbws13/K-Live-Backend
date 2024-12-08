package xyz.kbws.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import xyz.kbws.constant.CommonConstant;
import xyz.kbws.constant.RedisConstant;
import xyz.kbws.redis.RedisComponent;

import javax.annotation.Resource;

/**
 * @author kbws
 * @date 2024/12/8
 * @description: 监听 Redis 中的失效 key，自动减少在线观看人数
 */
@Slf4j
@Component
public class RedisListener extends KeyExpirationEventMessageListener {

    @Resource
    private RedisComponent redisComponent;

    public RedisListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String key = message.toString();
        if (!key.startsWith(RedisConstant.VIDEO_PLAY_ONLINE_COUNT_SUFFIX + RedisConstant.VIDEO_PLAY_ONLINE_COUNT_USER_SUFFIX)) {
            int userKeyIndex = key.indexOf(RedisConstant.VIDEO_PLAY_ONLINE_COUNT_USER_SUFFIX) + RedisConstant.VIDEO_PLAY_ONLINE_COUNT_USER_SUFFIX.length();
            String fileId = key.substring(userKeyIndex, userKeyIndex + CommonConstant.LENGTH_20);
            redisComponent.decrementPlayOnlineCount(String.format(RedisConstant.VIDEO_PLAY_ONLINE_COUNT, fileId));
        }
    }
}
