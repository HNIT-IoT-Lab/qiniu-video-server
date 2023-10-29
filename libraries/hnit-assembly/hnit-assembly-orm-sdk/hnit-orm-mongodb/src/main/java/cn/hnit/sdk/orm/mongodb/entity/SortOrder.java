package cn.hnit.sdk.orm.mongodb.entity;

import lombok.Data;
import org.springframework.data.annotation.Transient;

/**
 * 用于排序
 *
 * @author king
 * @since 2022-10-28 16:17
 **/
@Data
public class SortOrder {
    /**
     * 排序字段
     */
    @Transient
    private String order;


    /**
     * 排序规则
     */
    @Transient
    private boolean orderDesc;
}
