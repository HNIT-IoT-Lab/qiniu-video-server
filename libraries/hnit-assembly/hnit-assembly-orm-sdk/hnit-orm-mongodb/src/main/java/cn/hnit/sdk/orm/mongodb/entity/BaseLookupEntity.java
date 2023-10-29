package cn.hnit.sdk.orm.mongodb.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

/**
 * 用于多表聚合查询生成关联的LookupOperation
 */
@Accessors(chain = true)
@Data
@FieldNameConstants
public class BaseLookupEntity {
    /**
     * 关联表名
     */
    private String from;

    /**
     * 关联字段
     */
    private String local;

    /**
     * 关联表对应字段
     */
    private String foreign;

    public BaseLookupEntity(String from, String local, String foreign) {
        this.from = from;
        this.local = local;
        this.foreign = foreign;
    }
}
