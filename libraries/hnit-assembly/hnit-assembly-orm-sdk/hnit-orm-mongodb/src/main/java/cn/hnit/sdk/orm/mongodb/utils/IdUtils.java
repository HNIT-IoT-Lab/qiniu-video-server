package cn.hnit.sdk.orm.mongodb.utils;

import cn.hnit.sdk.orm.mongodb.entity.SeqInfo;
import cn.hnit.sdk.orm.mongodb.exception.OrmException;
import cn.hutool.core.date.SystemClock;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

@Slf4j
public class IdUtils {

    private IdUtils() {
        throw new OrmException("IdUtils 不允许被实例化！");
    }

    /**
     * 获取下一批id，支持字段
     *
     * @param table         表名
     * @param field         字段名
     * @param mongoTemplate 连接器
     * @param inc           增量
     * @return id
     */
    private static Long getNextId(String table, String field, MongoTemplate mongoTemplate, Long inc) {
        String value = CharSequenceUtil.isEmpty(field) ? table : MongoUtils.dot(table, field);
        Query query = new Query(Criteria.where(SeqInfo.Fields.collName).is(value));
        Update update = new Update();
        update.inc(SeqInfo.Fields.seqId, Optional.ofNullable(inc).orElse(1L));
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.upsert(true);
        options.returnNew(true);
        SeqInfo seq = mongoTemplate.findAndModify(query, update, options, SeqInfo.class);
        return seq == null ? null : seq.getSeqId();
    }

    /**
     * 表id自增
     *
     * @param clazz         类
     * @param mongoTemplate 连接器
     * @return 自增后的数值
     */
    public static Long getNextId(Class<?> clazz, MongoTemplate mongoTemplate) {
        return getNextId(MongoUtils.getTableName(clazz), null, mongoTemplate, 1L);
    }

    /**
     * 表字段自增
     *
     * @param clazz         类
     * @param field         字段
     * @param mongoTemplate 连接器
     * @return 自增后的数值
     */
    public static Long getNextId(Class<?> clazz, String field, MongoTemplate mongoTemplate) {
        return getNextId(clazz, field, mongoTemplate, 1L);
    }

    /**
     * 表字段自增
     *
     * @param clazz         类
     * @param field         字段
     * @param mongoTemplate 连接器
     * @return 自增后的数值
     */
    public static Long getNextId(Class<?> clazz, String field, MongoTemplate mongoTemplate, Long inc) {
        return getNextId(MongoUtils.getTableName(clazz), field, mongoTemplate, inc);
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取一个随机id
     *
     * @return 随机id
     */
    public static Long getRandomId() {
        return RandomUtil.randomLong(10000, 99999);
    }

    /**
     * 获取一个随机id，超时为3秒
     * <p>
     * 例如:
     * long newId = getRandomId(id -> dao.existId(id));
     * <p>
     * 说明：newId是dao.existId判断失败的（dao中没有这个newId），是允许插入dao中的
     *
     * @param test 传一个校验id的函数，判定：true为不通过，会重新生成id
     * @return 随机id
     */
    public static Long getRandomId(Predicate<Long> test) {
        long id;
        long time = SystemClock.now();
        while (test.test(id = getRandomId())) {
            if (SystemClock.now() - time >= 3000) {
                log.warn("生成id超时!");
                throw new OrmException("Id生成失败，请稍后重试");
            }
        }
        return id;
    }

    public static String desensitizedPhoneNumber(String phoneNumber) {
        if (StringUtils.isNotEmpty(phoneNumber)) {
            phoneNumber = phoneNumber.replaceAll("(\\w{3})\\w*(\\w{4})", "$1****$2");
        }
        return phoneNumber;
    }

    //身份证前三后四脱敏
    public static String idEncrypt(String id) {
        if (StringUtils.isEmpty(id) || (id.length() < 8)) {
            return id;
        }
        return id.replaceAll("(?<=\\w{3})\\w(?=\\w{4})", "*");
    }


    public static String desensitizedIdCard(String idCard) {
        if (StringUtils.isNotBlank(idCard)) {
            if (idCard.length() == 15) {
                idCard = idCard.replaceAll("(\\w{6})\\w*(\\w{3})", "$1******$2");
            }
            if (idCard.length() == 18) {
                idCard = idCard.replaceAll("(\\w{6})\\w*(\\w{3})", "$1*********$2");
            }
        }
        return idCard;
    }

    private static final int SIZE = 6;
    private static final String SYMBOL = "*";

    /**
     * 通用脱敏方法
     *
     * @param value
     * @return
     */
    public static String commonDisplay(String value) {
        if (null == value || "".equals(value)) {
            return value;
        }
        int len = value.length();
        int pamaone = len / 2;
        int pamatwo = pamaone - 1;
        int pamathree = len % 2;
        StringBuilder stringBuilder = new StringBuilder();
        if (len <= 2) {
            if (pamathree == 1) {
                return SYMBOL;
            }
            stringBuilder.append(SYMBOL);
            stringBuilder.append(value.charAt(len - 1));
        } else {
            if (pamatwo <= 0) {
                stringBuilder.append(value.substring(0, 1));
                stringBuilder.append(SYMBOL);
                stringBuilder.append(value.substring(len - 1, len));

            } else if (pamatwo >= SIZE / 2 && SIZE + 1 != len) {
                int pamafive = (len - SIZE) / 2;
                stringBuilder.append(value.substring(0, pamafive));
                for (int i = 0; i < SIZE; i++) {
                    stringBuilder.append(SYMBOL);
                }
                if ((pamathree == 0 && SIZE / 2 == 0) || (pamathree != 0 && SIZE % 2 != 0)) {
                    stringBuilder.append(value.substring(len - pamafive, len));
                } else {
                    stringBuilder.append(value.substring(len - (pamafive + 1), len));
                }
            } else {
                int pamafour = len - 2;
                stringBuilder.append(value.substring(0, 1));
                for (int i = 0; i < pamafour; i++) {
                    stringBuilder.append(SYMBOL);
                }
                stringBuilder.append(value.substring(len - 1, len));
            }
        }
        return stringBuilder.toString();
    }
}
