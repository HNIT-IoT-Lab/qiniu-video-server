package cn.hnit.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AppSourceEnums {
    /** IOS **/
    IOS(0, "ios"),
    ANDROID(1, "安卓"),
    PC( 2,  "PC");



    private final Integer code;
    private final String name;

    public static AppSourceEnums getByCode(Integer code) {
        for (AppSourceEnums item : AppSourceEnums.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }

}
