package cn.hnit.common.result;

import lombok.Getter;

@Getter
public enum ResultCode implements StatusCode {
    /**
     * 请求成功
     */
    SUCCESS(1000, "请求成功"),
    /**
     * 请求失败
     */
    FAILED(1001, "请求失败"),
    /**
     * 参数校验失败
     */
    VALIDATE_ERROR(1002, "参数校验失败"),
    /**
     * response返回包装失败
     */
    RESPONSE_PACK_ERROR(1003, "response返回包装失败");
    /**
     * 状态码
     */
    private final int code;
    /**
     * 回复的消息
     */
    private final String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
