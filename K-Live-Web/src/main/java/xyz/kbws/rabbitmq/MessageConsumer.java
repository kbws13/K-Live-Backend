package xyz.kbws.rabbitmq;

import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.constant.MqConstant;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.model.entity.VideoFilePost;
import xyz.kbws.service.VideoPostService;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author kbws
 * @date 2024/11/30
 * @description: 消息消费者
 */
@Slf4j
@Component
public class MessageConsumer {

    @Resource
    private VideoPostService videoPostService;

    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    /**
     * 监听并处理视频转码消息
     *
     * @param message
     * @param channel
     * @param deliveryTag
     */
    @SneakyThrows
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = MqConstant.FILE_QUEUE),
                    exchange = @Exchange(name = MqConstant.FILE_EXCHANGE_NAME),
                    key = MqConstant.TRANSFER_VIDEO_ROOTING_KEY
            ),
            ackMode = "MANUAL", concurrency = "2")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveMessage message = {}", message);
        if (message == null) {
            // 消息为空，则拒绝消息（不重试），进入死信队列
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.NULL_ERROR, "消息为空");
        }

        try {
            executorService.execute(() -> {
                VideoFilePost videoFilePost = JSONUtil.toBean(message, VideoFilePost.class);
                videoPostService.transferVideoFile(videoFilePost);
            });
            // 手动确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            // 抛出异常，进入私信队列
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
