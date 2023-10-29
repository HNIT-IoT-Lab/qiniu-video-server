package cn.hnit.utils.common.bean;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 当前线程交易记录 用于追踪堆栈
 */
@Data
public class TradeRecord {

    private String tradeId;

    private String requestTime;

    /**
     * 请求url
     */
    private String url;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求参数body
     */
    private String body;

    public TradeRecord() {
        this.tradeId = UUID.randomUUID().toString();
        this.requestTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
