package cn.hnit.sdk.orm.mongodb.entity;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@ToString
@FieldNameConstants
@Document(collection = SeqInfo.TABLE_NAME)
public class SeqInfo {

    /**
     * 表名
     */
    public static final String TABLE_NAME = "sequence";

    /**
     * id
     */
    @Id
    private String id;

    /**
     * 集合名称
     */
    private String collName;

    /**
     * 当前最大id值
     */
    private Long seqId;
}
