package cn.hnit.utils.context;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author xwk
 * @date 2022/10/27
 * @Description 用户信息等级实体
 */
@Data
public class LevelUserDTO implements Serializable {


    private Long userId;

    private SimpleUserDTO user;


    //PrivilegeEnum
    private int nobleId;


    private int nobleLevel;

    public int getNobleLevel() {
        if (nobleTimeout != null && nobleTimeout.compareTo(LocalDateTime.now()) > 0) {
            return nobleLevel;
        }
        return 0;
    }


    private boolean isNobleOpen;
    /**
     * 冻结时间
     */
    private LocalDateTime nobleFrozenTime;
    /**
     * 过期时间
     */
    private LocalDateTime nobleTimeout;

}