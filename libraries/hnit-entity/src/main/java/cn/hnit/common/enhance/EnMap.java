package cn.hnit.common.enhance;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.EqualsAndHashCode;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * {@link LinkedHashMap} 是最常用的一种Map类型，但是它写着麻烦<p>
 * 所以特封装此类，继承Map，进行一些扩展，可以让Map更灵活使用
 *
 * @author 梁峰源
 * @see Map
 * @see LinkedHashMap
 * @since 2022年9月22日19:43:07
 */
@EqualsAndHashCode(callSuper = false)
public class EnMap extends LinkedHashMap<String, Object> {


    private static final long serialVersionUID = 1L;


    public EnMap() {
    }


    /**
     * 以下元素会在isNull函数中被判定为Null，
     */
    public static final Object[] NULL_ELEMENT_ARRAY = {null, ""};
    public static final List<Object> NULL_ELEMENT_LIST;


    static {
        NULL_ELEMENT_LIST = Arrays.asList(NULL_ELEMENT_ARRAY);
    }

    // ============================= 读值 =============================

    /**
     * 获取一个值
     */
    @Override
    public Object get(Object key) {
        if ("this".equals(key)) {
            return this;
        }
        return super.get(key);
    }

    /**
     * 如果为空，则返回默认值
     */
    public Object get(Object key, Object defaultValue) {
        Object value = get(key);
        if (valueIsNull(value)) {
            return defaultValue;
        }
        return value;
    }

    /**
     * 转为String并返回
     */
    public String getString(String key) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        return String.valueOf(value);
    }

    /**
     * 如果为空，则返回默认值
     */
    public String getString(String key, String defaultValue) {
        Object value = get(key);
        if (valueIsNull(value)) {
            return defaultValue;
        }
        return String.valueOf(value);
    }

    /**
     * 转为int并返回
     */
    public int getInt(String key) {
        Object value = get(key);
        if (valueIsNull(value)) {
            return 0;
        }
        return Integer.parseInt(String.valueOf(value));
    }

    /**
     * 转为int并返回，同时指定默认值
     */
    public int getInt(String key, int defaultValue) {
        Object value = get(key);
        if (valueIsNull(value)) {
            return defaultValue;
        }
        return Integer.parseInt(String.valueOf(value));
    }

    /**
     * 转为long并返回
     */
    public long getLong(String key) {
        Object value = get(key);
        if (valueIsNull(value)) {
            return 0;
        }
        return Long.parseLong(String.valueOf(value));
    }

    /**
     * 转为double并返回
     */
    public double getDouble(String key) {
        Object value = get(key);
        if (valueIsNull(value)) {
            return 0.0;
        }
        return Double.parseDouble(String.valueOf(value));
    }

    /**
     * 转为boolean并返回
     */
    public boolean getBoolean(String key) {
        Object value = get(key);
        if (valueIsNull(value)) {
            return false;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }

    /**
     * 转为Date并返回，根据自定义格式
     */
    public Date getDateByFormat(String key, String format) {
        try {
            return new SimpleDateFormat(format).parse(getString(key));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 转为Date并返回，根据格式： yyyy-MM-dd
     */
    public Date getDate(String key) {
        return getDateByFormat(key, "yyyy-MM-dd");
    }

    /**
     * 转为Date并返回，根据格式： yyyy-MM-dd HH:mm:ss
     */
    public Date getDateTime(String key) {
        return getDateByFormat(key, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 获取集合(必须原先就是个集合，否则会创建个新集合并返回)
     */
    @SuppressWarnings("unchecked")
    public List<Object> getList(String key) {
        Object value = get(key);
        List<Object> list = null;
        if (value == null || value.equals("")) {
            list = new ArrayList<Object>();
        } else if (value instanceof List) {
            list = (List<Object>) value;
        } else {
            list = new ArrayList<Object>();
            list.add(value);
        }
        return list;
    }

    /**
     * 获取集合 (指定泛型类型)
     */
    public <T> List<T> getList(String key, Class<T> cs) {
        List<Object> list = getList(key);
        List<T> list2 = new ArrayList<T>();
        for (Object obj : list) {
            T objC = getValueByClass(obj, cs);
            list2.add(objC);
        }
        return list2;
    }

    /**
     * 获取集合(逗号分隔式)，(指定类型)
     */
    public <T> List<T> getListByComma(String key, Class<T> cs) {
        String listStr = getString(key);
        if (listStr == null || listStr.equals("")) {
            return new ArrayList<>();
        }
        // 开始转化
        String[] arr = listStr.split(",");
        List<T> list = new ArrayList<T>();
        for (String str : arr) {
            if (cs == int.class || cs == Integer.class || cs == long.class || cs == Long.class) {
                str = str.trim();
            }
            T objC = getValueByClass(str, cs);
            list.add(objC);
        }
        return list;
    }


    /**
     * 根据指定类型从map中取值，返回实体对象
     */
    public <T> T getModel(Class<T> cs) {
        try {
            return getModelByObject(cs.newInstance());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从map中取值，塞到一个对象中
     */
    public <T> T getModelByObject(T obj) {
        // 获取类型 
        Class<?> cs = obj.getClass();
        // 循环复制  
        for (Field field : cs.getDeclaredFields()) {
            try {
                // 获取对象 
                Object value = this.get(field.getName());
                if (value == null) {
                    continue;
                }
                field.setAccessible(true);
                Object valueConvert = getValueByClass(value, field.getType());
                field.set(obj, valueConvert);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException("属性取值出错：" + field.getName(), e);
            }
        }
        return obj;
    }


    /**
     * 将指定值转化为指定类型并返回
     *
     * @param obj
     * @param cs
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getValueByClass(Object obj, Class<T> cs) {
        String obj2 = String.valueOf(obj);
        Object obj3 = null;
        if (cs.equals(String.class)) {
            obj3 = obj2;
        } else if (cs.equals(int.class) || cs.equals(Integer.class)) {
            obj3 = Integer.valueOf(obj2);
        } else if (cs.equals(long.class) || cs.equals(Long.class)) {
            obj3 = Long.valueOf(obj2);
        } else if (cs.equals(short.class) || cs.equals(Short.class)) {
            obj3 = Short.valueOf(obj2);
        } else if (cs.equals(byte.class) || cs.equals(Byte.class)) {
            obj3 = Byte.valueOf(obj2);
        } else if (cs.equals(float.class) || cs.equals(Float.class)) {
            obj3 = Float.valueOf(obj2);
        } else if (cs.equals(double.class) || cs.equals(Double.class)) {
            obj3 = Double.valueOf(obj2);
        } else if (cs.equals(boolean.class) || cs.equals(Boolean.class)) {
            obj3 = Boolean.valueOf(obj2);
        } else {
            obj3 = (T) obj;
        }
        return (T) obj3;
    }


    // ============================= 写值 =============================

    /**
     * 给指定key添加一个默认值（只有在这个key原来无值的情况先才会set进去）
     */
    public void setDefaultValue(String key, Object defaultValue) {
        if (isNull(key)) {
            set(key, defaultValue);
        }
    }

    /**
     * set一个值，连缀风格
     */
    public EnMap set(String key, Object value) {
        // 防止敏感key 
        if (key.toLowerCase().equals("this")) {
            return this;
        }
        put(key, value);
        return this;
    }

    /**
     * 将一个Map塞进EnMap
     */
    public EnMap setMap(Map<String, ?> map) {
        if (map != null) {
            for (String key : map.keySet()) {
                this.set(key, map.get(key));
            }
        }
        return this;
    }

    /**
     * 将一个对象解析塞进EnMap
     */
    public EnMap setModel(Object model) {
        if (model == null) {
            return this;
        }
        Field[] fields = model.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                boolean isStatic = Modifier.isStatic(field.getModifiers());
                if (!isStatic) {
                    this.set(field.getName(), field.get(model));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return this;
    }

    /**
     * 将json字符串解析后塞进EnMap
     */
    public EnMap setJsonString(String jsonString) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = new ObjectMapper().readValue(jsonString, Map.class);
            return this.setMap(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    // ============================= 删值 =============================

    /**
     * delete一个值，连缀风格
     */
    public EnMap delete(String key) {
        remove(key);
        return this;
    }

    /**
     * 清理所有value为null的字段
     */
    public EnMap clearNull() {
        Iterator<String> iterator = this.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (this.isNull(key)) {
                iterator.remove();
                this.remove(key);
            }

        }
        return this;
    }

    /**
     * 清理指定key
     */
    public EnMap clearIn(String... keys) {
        List<String> keys2 = Arrays.asList(keys);
        Iterator<String> iterator = this.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (keys2.contains(key) == true) {
                iterator.remove();
                this.remove(key);
            }
        }
        return this;
    }

    /**
     * 清理掉不在列表中的key
     */
    public EnMap clearNotIn(String... keys) {
        List<String> keys2 = Arrays.asList(keys);
        Iterator<String> iterator = this.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (keys2.contains(key) == false) {
                iterator.remove();
                this.remove(key);
            }

        }
        return this;
    }

    /**
     * 清理掉所有key
     */
    public EnMap clearAll() {
        clear();
        return this;
    }


    // ============================= 快速构建 ============================= 

    /**
     * 构建一个EnMap并返回
     */
    public static EnMap getEnMap() {
        return new EnMap();
    }

    /**
     * 构建一个EnMap并返回
     */
    public static EnMap getEnMap(String key, Object value) {
        return new EnMap().set(key, value);
    }

    /**
     * 构建一个EnMap并返回
     */
    public static EnMap getEnMap(Map<String, ?> map) {
        return new EnMap().setMap(map);
    }

    /**
     * 将一个对象集合解析成为EnMap
     */
    public static EnMap getEnMapByModel(Object model) {
        return EnMap.getEnMap().setModel(model);
    }

    /**
     * 将一个对象集合解析成为EnMap集合
     */
    public static List<EnMap> getEnMapByList(List<?> list) {
        List<EnMap> listMap = new ArrayList<EnMap>();
        for (Object model : list) {
            listMap.add(getEnMapByModel(model));
        }
        return listMap;
    }

    /**
     * 克隆指定key，返回一个新的EnMap
     */
    public EnMap cloneKeys(String... keys) {
        EnMap so = new EnMap();
        for (String key : keys) {
            so.set(key, this.get(key));
        }
        return so;
    }

    /**
     * 克隆所有key，返回一个新的EnMap
     */
    public EnMap cloneEnMap() {
        EnMap so = new EnMap();
        for (String key : this.keySet()) {
            so.set(key, this.get(key));
        }
        return so;
    }

    /**
     * 将所有key转为大写
     */
    public EnMap toUpperCase() {
        EnMap so = new EnMap();
        for (String key : this.keySet()) {
            so.set(key.toUpperCase(), this.get(key));
        }
        this.clearAll().setMap(so);
        return this;
    }

    /**
     * 将所有key转为小写
     */
    public EnMap toLowerCase() {
        EnMap so = new EnMap();
        for (String key : this.keySet()) {
            so.set(key.toLowerCase(), this.get(key));
        }
        this.clearAll().setMap(so);
        return this;
    }

    /**
     * 将所有key中下划线转为中划线模式 (kebab-case风格)
     */
    public EnMap toKebabCase() {
        EnMap so = new EnMap();
        for (String key : this.keySet()) {
            so.set(wordEachKebabCase(key), this.get(key));
        }
        this.clearAll().setMap(so);
        return this;
    }

    /**
     * 将所有key中下划线转为小驼峰模式
     */
    public EnMap toHumpCase() {
        EnMap so = new EnMap();
        for (String key : this.keySet()) {
            so.set(wordEachBigFs(key), this.get(key));
        }
        this.clearAll().setMap(so);
        return this;
    }

    /**
     * 将所有key中小驼峰转为下划线模式
     */
    public EnMap humpToLineCase() {
        EnMap so = new EnMap();
        for (String key : this.keySet()) {
            so.set(wordHumpToLine(key), this.get(key));
        }
        this.clearAll().setMap(so);
        return this;
    }


    // ============================= 辅助方法 =============================


    /**
     * 指定key是否为null，判定标准为 NULL_ELEMENT_ARRAY 中的元素
     */
    public boolean isNull(String key) {
        return valueIsNull(get(key));
    }

    /**
     * 指定key列表中是否包含value为null的元素，只要有一个为null，就会返回true
     */
    public boolean isContainNull(String... keys) {
        for (String key : keys) {
            if (this.isNull(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 与isNull()相反
     */
    public boolean isNotNull(String key) {
        return !isNull(key);
    }

    /**
     * 指定key的value是否为null，作用同isNotNull()
     */
    public boolean has(String key) {
        return !isNull(key);
    }

    /**
     * 指定value在此EnMap的判断标准中是否为null
     */
    public boolean valueIsNull(Object value) {
        return NULL_ELEMENT_LIST.contains(value);
    }

    /**
     * 验证指定key不为空，为空则抛出异常
     */
    public EnMap checkNull(String... keys) {
        for (String key : keys) {
            if (this.isNull(key)) {
                throw new RuntimeException("参数" + key + "不能为空");
            }
        }
        return this;
    }

    static Pattern patternNumber = Pattern.compile("[0-9]*");

    /**
     * 指定key是否为数字
     */
    public boolean isNumber(String key) {
        String value = getString(key);
        if (value == null) {
            return false;
        }
        return patternNumber.matcher(value).matches();
    }


    /**
     * 转为JSON字符串
     */
    public String toJsonString() {
        try {
//			EnMap so = EnMap.getEnMap(this);
            return new ObjectMapper().writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 转为JSON字符串, 带格式的
     */
    public String toJsonFormatString() {
        try {
            return JSON.toJSONString(this, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ============================= web辅助 =============================


//    /**
//     * 返回当前request请求的的所有参数
//     *
//     * @return
//     */
//    public static EnMap getRequestEnMap() {
//        // 大善人SpringMVC提供的封装
//        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        if (servletRequestAttributes == null) {
//            throw new RuntimeException("当前线程非JavaWeb环境");
//        }
//        // 当前request
//        HttpServletRequest request = servletRequestAttributes.getRequest();
//        if (request.getAttribute("currentEnMap") == null || request.getAttribute("currentEnMap") instanceof EnMap == false) {
//            initRequestEnMap(request);
//        }
//        return (EnMap) request.getAttribute("currentEnMap");
//    }
//
//    /**
//     * 初始化当前request的 EnMap
//     */
//    private static void initRequestEnMap(HttpServletRequest request) {
//        EnMap soMap = new EnMap();
//        Map<String, String[]> parameterMap = request.getParameterMap();    // 获取所有参数
//        for (String key : parameterMap.keySet()) {
//            try {
//                String[] values = parameterMap.get(key); // 获得values
//                if (values.length == 1) {
//                    soMap.set(key, values[0]);
//                } else {
//                    List<String> list = new ArrayList<String>();
//                    for (String v : values) {
//                        list.add(v);
//                    }
//                    soMap.set(key, list);
//                }
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//        request.setAttribute("currentEnMap", soMap);
//    }
//
//    /**
//     * 验证返回当前线程是否为JavaWeb环境
//     *
//     * @return
//     */
//    public static boolean isJavaWeb() {
//        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();// 大善人SpringMVC提供的封装
//        if (servletRequestAttributes == null) {
//            return false;
//        }
//        return true;
//    }


    // ============================= 常见key （以下key经常用，所以封装以下，方便写代码） =============================

    /**
     * get 当前页
     */
    public int getKeyPageNo() {
        int pageNo = getInt("pageNo", 1);
        if (pageNo <= 0) {
            pageNo = 1;
        }
        return pageNo;
    }

    /**
     * get 页大小
     */
    public int getKeyPageSize() {
        int pageSize = getInt("pageSize", 10);
        if (pageSize <= 0 || pageSize > 1000) {
            pageSize = 10;
        }
        return pageSize;
    }

    /**
     * get 排序方式
     */
    public int getKeySortType() {
        return getInt("sortType");
    }


    // ============================= 分页相关(封装mybatis的page-help插件 ) =============================

    /**
     * 分页插件
     */
    private Page<?> pagePlug;

    /**
     * 分页插件 - 开始分页
     */
    public EnMap startPage() {
        this.pagePlug = PageHelper.startPage(getKeyPageNo(), getKeyPageSize());
        return this;
    }

    /**
     * 获取上次分页的记录总数
     */
    public long getDataCount() {
        if (pagePlug == null) {
            return -1;
        }
        return pagePlug.getTotal();
    }

    /**
     * 分页插件 - 结束分页, 返回总条数 （该方法已过时，请调用更加符合语义化的getDataCount() ）
     */
    @Deprecated
    public long endPage() {
        return getDataCount();
    }


    // ============================= 工具方法 =============================


    /**
     * 将一个一维集合转换为树形集合
     *
     * @param list         集合
     * @param idKey        id标识key
     * @param parentIdKey  父id标识key
     * @param childListKey 子节点标识key
     * @return 转换后的tree集合
     */
    public static List<EnMap> listToTree(List<EnMap> list, String idKey, String parentIdKey, String childListKey) {
        // 声明新的集合，存储tree形数据 
        List<EnMap> newTreeList = new ArrayList<EnMap>();
        // 声明hash-Map，方便查找数据 
        EnMap hash = new EnMap();
        // 将数组转为Object的形式，key为数组中的id 
        for (int i = 0; i < list.size(); i++) {
            EnMap json = (EnMap) list.get(i);
            hash.put(json.getString(idKey), json);
        }
        // 遍历结果集
        for (int j = 0; j < list.size(); j++) {
            // 单条记录
            EnMap aVal = (EnMap) list.get(j);
            // 在hash中取出key为单条记录中pid的值
            EnMap hashVp = (EnMap) hash.get(aVal.get(parentIdKey, "").toString());
            // 如果记录的pid存在，则说明它有父节点，将她添加到孩子节点的集合中
            if (hashVp != null) {
                // 检查是否有child属性，有则添加，没有则新建 
                if (hashVp.get(childListKey) != null) {
                    @SuppressWarnings("unchecked")
                    List<EnMap> ch = (List<EnMap>) hashVp.get(childListKey);
                    ch.add(aVal);
                    hashVp.put(childListKey, ch);
                } else {
                    List<EnMap> ch = new ArrayList<EnMap>();
                    ch.add(aVal);
                    hashVp.put(childListKey, ch);
                }
            } else {
                newTreeList.add(aVal);
            }
        }
        return newTreeList;
    }


    /**
     * 指定字符串的字符串下划线转大写模式
     */
    private static String wordEachBig(String str) {
        String newStr = "";
        for (String s : str.split("_")) {
            newStr += wordFirstBig(s);
        }
        return newStr;
    }

    /**
     * 返回下划线转小驼峰形式
     */
    private static String wordEachBigFs(String str) {
        return wordFirstSmall(wordEachBig(str));
    }

    /**
     * 将指定单词首字母大写
     */
    private static String wordFirstBig(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1, str.length());
    }

    /**
     * 将指定单词首字母小写
     */
    private static String wordFirstSmall(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1, str.length());
    }

    /**
     * 下划线转中划线
     */
    private static String wordEachKebabCase(String str) {
        return str.replaceAll("_", "-");
    }

    /**
     * 驼峰转下划线
     */
    private static String wordHumpToLine(String str) {
        return str.replaceAll("[A-Z]", "_$0").toLowerCase();
    }


}
