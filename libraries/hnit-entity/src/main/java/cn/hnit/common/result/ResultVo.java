package cn.hnit.common.result;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 统一实体类封装，这样的封装是比较常用的封装
 * </p>
 *
 * @author 梁峰源
 * @since 2022-08-05 17:36
 **/
@Data
@Accessors(chain = true)
public class ResultVo {
    /**
     * 状态码
     */
    private int code;

    /**
     * 状态信息
     */
    private String msg;

    /**
     * 返回对象
     */
    private Object data;

    /**
     * 手动设置返回vo
     * @param code 状态码
     * @param msg 返回的信息
     * @param data 返回的数据
     */
    public ResultVo(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 默认返回成功状态码，数据对象
     * @param data 返回的数据
     */
    public ResultVo(Object data) {
        this.code = ResultCode.SUCCESS.getCode();
        this.msg = ResultCode.SUCCESS.getMsg();
        this.data = data;
    }

    /**
     * 返回指定状态码，数据对象
     */
    public ResultVo(StatusCode statusCode, Object data) {
        this.code = statusCode.getCode();
        this.msg = statusCode.getMsg();
        this.data = data;
    }

    /**
     * 只返回状态码
     */
    public ResultVo(StatusCode statusCode) {
        this.code = statusCode.getCode();
        this.msg = statusCode.getMsg();
        this.data = null;
    }


    public static ResultVo SUCCESS() {
        return new ResultVo(ResultCode.SUCCESS);
    }
}
