package cn.hnit.common.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页实体类
 *
 * @author king
 * @since 2022-10-28 16:15
 **/
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Page<T> implements Serializable {
    private static final long serialVersionUID = -1983370762627075537L;
    /**
     * 第几页
     */
    private int pageNo;
    /**
     * 这一页的条数
     */
    private int pageSize;
    /**
     * 记录总数
     */
    private long total;
    /**
     * 一共有多少页
     */
    private long pages;
    /**
     * 结果集
     */
    private List<T> result;

    public Page(int pageSize, long total, List<T> result) {
        this.pageSize = pageSize;
        this.total = total;
        this.result = new ArrayList<>(result);
        this.pages = getPages();
    }

    /**
     * 获取总页数
     */
    public int getPages() {
        if (total == 0 || pageSize == 0) {
            return 0;
        } else {
            return (int) total / pageSize + (total % pageSize == 0 ? 0 : 1);
        }
    }
}
