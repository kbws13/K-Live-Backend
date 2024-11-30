package xyz.kbws.constant;

/**
 * @author kbws
 * @date 2024/11/29
 * @description: 消息队列常量
 */
public interface MqConstant {
    // 普通交换机
    String FILE_EXCHANGE_NAME = "file_exchange";
    // 交换队列
    String FILE_QUEUE = "file_queue";
    // 视频转码路由键
    String TRANSFER_VIDEO_ROOTING_KEY = "transfer_video_rootingKey";
    // 删除文件路由键
    String DEL_FILE_ROUTING_KEY = "del_file_routingKey";
    // 路由模式
    String FILE_DIRECT_EXCHANGE = "direct";

    // 死信队列交换机
    String DLX_EXCHANGE = "dlx_exchange";
    // 死信队列
    String DLX_QUEUE = "dlx_queue";
}
