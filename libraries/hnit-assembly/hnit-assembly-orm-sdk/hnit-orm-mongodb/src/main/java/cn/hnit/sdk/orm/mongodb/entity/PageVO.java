package cn.hnit.sdk.orm.mongodb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PageVO extends SortOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 第几页
     */
    @Transient
    protected Integer pageNumber = 1;

    @Transient
    protected Integer lostId = 1;

    /**
     * 每页几条
     */
    @Transient
    protected Integer pageSize = 10;

    /**
     * 跳过条数
     */
    @Transient
    protected Long skip;
}

