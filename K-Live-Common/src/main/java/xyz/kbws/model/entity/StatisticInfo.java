package xyz.kbws.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 数据统计表
 * @TableName statisticInfo
 */
@TableName(value ="statisticInfo")
@Data
public class StatisticInfo implements Serializable {
    /**
     * 统计日期
     */
    private String statisticDate;

    /**
     * 用户 id
     */
    private String userId;

    /**
     * 数据统计类型
     */
    private Integer dataType;

    /**
     * 统计数量
     */
    private Integer count;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}