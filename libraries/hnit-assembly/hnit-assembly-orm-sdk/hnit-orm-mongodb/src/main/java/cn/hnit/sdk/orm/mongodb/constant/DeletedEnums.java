package cn.hnit.sdk.orm.mongodb.constant;

import lombok.Getter;

@Getter
public enum DeletedEnums {
    Y(1, "是", Boolean.TRUE),
    N(2, "否", Boolean.FALSE);

    /**
     * 编码
     */
    private final Integer code;

    /**
     * 名称
     */
    private final String name;

    /**
     * 具体值
     */
    private final Boolean value;


    DeletedEnums(Integer code, String name, Boolean value) {
        this.code = code;
        this.name = name;
        this.value = value;
    }

    /**
     * 枚举类的code --- 唯一值
     *
     * @return code
     */
    public Integer code() {
        return code;
    }

    /**
     * 枚举类的说明
     *
     * @return value
     */
    public String value() {
        return name;
    }

    /**
     * 枚举类的说明
     *
     * @return value
     */
    public Boolean bool() {
        return value;
    }
}
