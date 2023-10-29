package cn.hnit.starter.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 鉴权redisKey
 * @author Admin
 *
 */
@Getter
@AllArgsConstructor
public enum AuthRedisEnum {
	USER("userId","用户缓存 key+userId"),
	LIMIT_SIGN("LIMIT:SIGN:","限流sign key+yyyyMMddHH set "),
	USER_BAN_LIST("USER:BAN:LIST:", "黑名单列表");
	private String key;
    private String desc;
}
