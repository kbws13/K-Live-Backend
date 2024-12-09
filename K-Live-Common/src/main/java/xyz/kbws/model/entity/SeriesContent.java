package xyz.kbws.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 视频合集内容表
 *
 * @TableName seriesContent
 */
@TableName(value = "seriesContent")
@Data
public class SeriesContent implements Serializable {
    /**
     * 合集 id
     */
    private Integer seriesId;

    /**
     * 视频 id
     */
    private String videoId;

    /**
     * 排序
     */
    private Integer sort;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}