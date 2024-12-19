package xyz.kbws.rabbitmq;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
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
import xyz.kbws.config.AppConfig;
import xyz.kbws.constant.MqConstant;
import xyz.kbws.es.EsComponent;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.model.dto.video.VideoPlayRequest;
import xyz.kbws.model.entity.VideoFilePost;
import xyz.kbws.model.enums.SearchOrderTypeEnum;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.VideoPlayHistoryService;
import xyz.kbws.service.VideoPostService;
import xyz.kbws.service.VideoService;

import javax.annotation.Resource;
import java.io.File;
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
    private VideoService videoService;

    @Resource
    private VideoPostService videoPostService;

    @Resource
    private VideoPlayHistoryService videoPlayHistoryService;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private EsComponent esComponent;

    @Resource
    private AppConfig appConfig;

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
    public void receiveTransferVideoMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveTransferVideoMessage message = {}", message);
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
            // 抛出异常，进入死信队列
            channel.basicNack(deliveryTag, false, false);
        }
    }

    /**
     * 监听并处理删除视频文件消息
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
                    key = MqConstant.DEL_FILE_ROUTING_KEY
            ),
            ackMode = "MANUAL", concurrency = "2")
    public void receiveDeleteFileMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveDeleteFileMessage message = {}", message);
        if (message == null) {
            // 消息为空，则拒绝消息（不重试），进入死信队列
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.NULL_ERROR, "消息为空");
        }

        try {
            executorService.execute(() -> {
                VideoFilePost videoFilePost = JSONUtil.toBean(message, VideoFilePost.class);
                boolean del = FileUtil.del(new File(appConfig.getProjectFolder() + videoFilePost.getFilePath()));
                if (!del) {
                    log.error("删除文件失败, 文件路径: {}", videoFilePost.getFilePath());
                }
            });
            // 手动确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            // 抛出异常，进入死信队列
            channel.basicNack(deliveryTag, false, false);
        }
    }

    /**
     * 监听并处理视频播放消息
     *
     * @param message
     * @param channel
     * @param deliveryTag
     */
    @SneakyThrows
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = MqConstant.NEWS_QUEUE),
                    exchange = @Exchange(name = MqConstant.NEWS_EXCHANGE_NAME),
                    key = MqConstant.VIDEO_PLAY_ROUTING_KEY
            ),
            ackMode = "MANUAL", concurrency = "2")
    public void receiveNewsMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveNewsMessage message = {}", message);
        if (message == null) {
            // 消息为空，则拒绝消息（不重试），进入死信队列
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.NULL_ERROR, "消息为空");
        }

        try {
            executorService.execute(() -> {
                VideoPlayRequest videoPlayRequest = JSONUtil.toBean(message, VideoPlayRequest.class);
                videoService.addPlayCount(videoPlayRequest.getVideoId());
                if (!StrUtil.isEmpty(videoPlayRequest.getUserId())) {
                    // 记录播放历史
                    String videoId = videoPlayRequest.getVideoId();
                    String userId = videoPlayRequest.getUserId();
                    Integer fileIndex = videoPlayRequest.getFileIndex();
                    videoPlayHistoryService.saveHistory(userId, videoId, fileIndex);
                }
                // 按天记录播放数量
                redisComponent.recordVideoPlayCount(videoPlayRequest.getVideoId());
                // 更新 ES 播放数量
                esComponent.updateDocCount(videoPlayRequest.getVideoId(), SearchOrderTypeEnum.VIDEO_PLAY.getField(), 1);

            });
            // 手动确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            // 抛出异常，进入死信队列
            channel.basicNack(deliveryTag, false, false);
            log.error("处理视频播放消息失败");
        }
    }
}
