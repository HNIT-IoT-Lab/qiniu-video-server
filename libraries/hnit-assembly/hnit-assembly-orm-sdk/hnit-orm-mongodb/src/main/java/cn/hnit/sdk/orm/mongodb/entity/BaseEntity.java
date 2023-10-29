package cn.hnit.sdk.orm.mongodb.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;

import org.springframework.data.annotation.Transient;
import java.time.LocalDateTime;

/**
 * 通用实体，所有表实体需继承此类，声明为抽象类
 *
 * @author king
 * @since 2022-10-28 16:08
 **/
@Data
@FieldNameConstants
@Accessors(chain = true)
public class BaseEntity {

    /**
     * mongo中的id字段名
     */
    @Transient
    public static final String ID = "_id";

    /**
     * id
     */
    @Id
    private Long id;

    /**
     * 是否删除
     * <p>
     * 若使用此字段，需想清楚是否需要软删除，方便处理好其他自定义查询语句
     */
    private Integer isDeleted;

    /**
     * 商品创建人用户ID
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 商品创建人用户ID
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
