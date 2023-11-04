package cn.hnit.sdk.orm.mongodb.dao;

import cn.hnit.common.page.Page;
import cn.hnit.sdk.orm.mongodb.constant.DeletedEnums;
import cn.hnit.sdk.orm.mongodb.entity.BaseEntity;
import cn.hnit.sdk.orm.mongodb.entity.PageVO;
import cn.hnit.sdk.orm.mongodb.entity.SortOperationMapping;
import cn.hnit.sdk.orm.mongodb.exception.OrmException;
import cn.hnit.sdk.orm.mongodb.utils.IdUtils;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * mongo基础的dao操作
 *
 * @author king
 * @since 2022-10-28 16:01
 **/

@Slf4j
public abstract class BaseDao<T extends BaseEntity> {

    /**
     * 基础泛型类
     */
    protected final Class<T> tClass;

    /**
     * 缓存字段的get方法
     */
    protected final Map<Field, Method> getMap = new HashMap<>(16);

    /**
     * 缓存字段的set方法
     */
    protected final Map<Field, Method> setMap = new HashMap<>(16);

    /**
     * 缓存类中的字段名称
     */
    protected final Map<Class<?>, List<String>> fieldMap = new HashMap<>(16);


    protected MongoTemplate mongoTemplate;


    /**
     * 子类初始化时 赋值泛型
     *
     * @param mongoTemplate mongoTemplate
     */
    @SuppressWarnings({"all"})
    protected BaseDao(MongoTemplate mongoTemplate) {
        // 将泛型类保存
        tClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        // 仅缓存子类字段
        String getFormat = "get%s";
        String isFormat = "is%s";
        String setFormat = "set%s";
        Map<String, Method> map = Arrays.stream(tClass.getMethods())
                .collect(Collectors.toMap(Method::getName, m -> m, (v1, v2) -> v2));
        Arrays.stream(tClass.getDeclaredFields()).forEach(f -> {
            // 如果有@Transient缓存
            if (f.getAnnotation(Transient.class) != null) {
                return;
            }
            f.setAccessible(true);
            String name = CharSequenceUtil.upperFirst(f.getName());
            boolean isBoolean = f.getType().isAssignableFrom(Boolean.class) || f.getType().isAssignableFrom(boolean.class);
            f.setAccessible(false);
            // 获取值方法
            String format = isBoolean ? isFormat : getFormat;
            getMap.put(f, map.get(String.format(format, name)));
            // 设置值方法
            setMap.put(f, map.get(String.format(setFormat, name)));
        });
        this.mongoTemplate = mongoTemplate;
    }


    /**
     * 更新或插入
     *
     * @param query  查询语句
     * @param update 更新语句
     * @return 修改后的值
     */
    public T upsert(Query query, Update update) {
        // 添加更新时间
        if (!update.getUpdateObject().containsKey(BaseEntity.Fields.updateTime)) {
            update.set(BaseEntity.Fields.updateTime, LocalDateTime.now());
        }
        // 尝试获取，若有则更新
        T one = mongoTemplate.findOne(query, tClass);
        if (one != null) {
            return mongoTemplate.findAndModify(
                    query,
                    update,
                    FindAndModifyOptions.options().upsert(true).returnNew(true),
                    tClass);
        }

        one = mongoTemplate.findOne(query, tClass);
        if (one == null) {
            // 如果没设置id，则获取id插入
            if (!query.getQueryObject().containsKey(BaseEntity.ID)) {
                query.addCriteria(Criteria.where(BaseEntity.ID).is(nextId()));
            }
            mongoTemplate.upsert(query, Update.update(BaseEntity.Fields.isDeleted, DeletedEnums.N.code()), tClass);
        }
        return mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                tClass);
    }

    /**
     * 保存
     *
     * @param t 泛型对象
     * @return 保存后的数据
     */
    public T save(T t) {
        if (t == null) {
            throw OrmException.pop("保存目标不能为空");
        }
        setInitValue(t, this::nextId);
        try {
            return mongoTemplate.insert(t);
        } finally {
            log.info("正在保存对象:{}", t);
        }
    }

    /**
     * 保存
     *
     * @param list 泛型对象列表
     * @return 保存后的数据
     */
    public List<T> save(List<T> list) {
        if (CollUtil.isEmpty(list)) {
            throw OrmException.pop("保存目标不能为空");
        }
        // 一次性获取，批量设置
        AtomicLong newId = new AtomicLong(nextId((long) list.size()) - list.size());
        list.forEach(t -> setInitValue(t, newId::incrementAndGet));
        try {
            return (List<T>) mongoTemplate.insertAll(list);
        } finally {
            log.info("正在保存对象列表:{}", list);
        }
    }

    /**
     * 保存或更新
     *
     * @param t 泛型对象
     * @return 保存后的数据
     */
    public T upsert(T t) {
        if (t == null) {
            throw OrmException.pop("保存目标不能为空");
        }
        setInitValue(t, this::nextId);
        try {
            return mongoTemplate.save(t);
        } finally {
            log.info("正在保存对象:{}", t);
        }
    }

    /**
     * 设置初始化值
     *
     * @param t 实体
     */
    private void setInitValue(T t, Supplier<Long> idProvider) {
        // id
        if (t.getId() == null) {
            t.setId(idProvider.get());
        }
        LocalDateTime now = LocalDateTime.now();
        // createTime
        if (t.getCreateTime() == null) {
            t.setCreateTime(now);
        }
        if (t.getUpdateTime() == null) {
            t.setUpdateTime(now);
        }
        // isDeleted
        if (t.getIsDeleted() == null) {
            t.setIsDeleted(DeletedEnums.N.code());
        }
    }

    /**
     * 根据id更新非空值
     *
     * @param base 更新对象
     * @return 更新成功
     */
    @SuppressWarnings("unchecked")
    public boolean updateById(BaseEntity base) {
        return this.updateByCriteria(base, Criteria.where(BaseEntity.Fields.id).is(base.getId()));
    }


    @SuppressWarnings("all")
    public boolean updateByCriteria(BaseEntity base, Criteria criteria) {
        if (base == null) {
            throw OrmException.pop("更新目标不能为空");
        }
        if (!tClass.isInstance(base)) {
            log.warn("类{}更新失败，与设定的更新表{}不符合", base, tClass);
            throw OrmException.pop("更新目标不是设定类");
        }
        T t = (T) base;
        // 没有id
        Long id = t.getId();
        if (id == null) {
            throw OrmException.pop("id不能为空");
        }
        // 判断是否有更新字段
        Update update = new Update();
        getMap.forEach((f, m) -> {
            try {
                Object v;
                if (m != null && (v = m.invoke(t)) != null) {
                    f.setAccessible(true);
                    update.set(f.getName(), v);
                    f.setAccessible(false);
                }
            } catch (Exception e) {
                log.warn("\n更新字段{}失败, 原因：\n{}", f, e);
                throw OrmException.pop("更新失败");
            }
        });
//        if (update.getUpdateObject().isEmpty()) {
//            log.warn("更新对象不能为空\n{}", base);
//            throw AppException.pop("更新对象不能为空");
//        }
        // 删除
        if (t.getIsDeleted() != null) {
            update.set(BaseEntity.Fields.isDeleted, t.getIsDeleted());
        }
        // 更新时间
        update.set(BaseEntity.Fields.updateTime, LocalDateTime.now());
        log.info("根据{}查询，更新为: {}", t.getId(), update.getUpdateObject());
        return mongoTemplate.updateFirst(Query.query(criteria), update, tClass).getModifiedCount() > 0;
    }

    @SuppressWarnings("unchecked")
    public boolean updateByCriteria(Update update, Criteria criteria) {
        update.set(BaseEntity.Fields.updateTime, LocalDateTime.now());
        log.info("根据{}查询，更新为: {}", criteria, update.getUpdateObject());
        return mongoTemplate.updateFirst(Query.query(criteria), update, tClass).getModifiedCount() > 0;
    }


    /**
     * 获取字段的下一个自增量
     *
     * @param field 字段名
     * @return 字段名列表
     */
    public Long nextInc(String field) {
        return nextInc(field, 1L);
    }

    /**
     * 获取字段的下一个自增量
     *
     * @param field 字段名
     * @return 字段名列表
     */
    public Long nextInc(String field, Long inc) {
        return IdUtils.getNextId(tClass, field, mongoTemplate, inc);
    }

    /**
     * 获取下一个id
     *
     * @return id
     */
    public Long nextId() {
        return nextInc(null);
    }

    /**
     * 获取下一个id
     *
     * @return id
     */
    public Long nextId(Long inc) {
        return nextInc(null, inc);
    }

    /**
     * id是否存在
     *
     * @param id id值
     * @return 是否
     */
    public boolean existId(Long id) {
        return existField(BaseEntity.ID, id);
    }

    /**
     * 字段是否存在未删除的值
     *
     * @param field 字段名
     * @param value 字段值
     * @return 是否
     */
    public boolean existFieldNotDeleted(String field, Object value) {
        return existField(field, value, null, DeletedEnums.Y.bool());
    }

    /**
     * 字段是否存在值
     *
     * @param field 字段名
     * @param value 字段值
     * @return 是否
     */
    public boolean existField(String field, Object value) {
        return existField(field, value, null, DeletedEnums.N.bool());
    }

    /**
     * 字段是否存在值(此id之外的)
     *
     * @param field      字段名
     * @param value      字段值
     * @param notDeleted 只确定未删除
     * @return 是否
     */
    public boolean existField(String field, Object value, Long excludeId, boolean notDeleted) {
        Criteria main = Criteria.where(field).is(value);
        if (!field.equals(BaseEntity.ID)) {
            main.and(BaseEntity.ID).ne(excludeId);
        }
        if (notDeleted) {
            main.and(BaseEntity.Fields.isDeleted).is(DeletedEnums.N.code());
        }
        return mongoTemplate.exists(Query.query(main), tClass);
    }

    /**
     * 根据id获取对象
     *
     * @param id id
     * @return 结果
     */
    public T getById(Long id) {
        return mongoTemplate.findOne(Query.query(Criteria.where(BaseEntity.ID).is(id)), tClass);
    }

    /**
     * 根据字段及其值获取数据
     *
     * @param field 字段名
     * @param value 字段值
     * @return 记录
     */
    public T getByField(String field, Object value) {
        return mongoTemplate.findOne(Query.query(Criteria.where(field).is(value)), tClass);
    }

    /**
     * 根据ids获取列表
     * <p>
     * 强制获取所有id匹配的数据，不校验状态
     *
     * @param ids ids
     * @return 结果集
     */
    public List<T> listByIds(Collection<Long> ids) {
        return mongoTemplate.find(Query.query(Criteria.where(BaseEntity.ID).in(ids)), tClass);
    }


    public List<T> listValidByIds(Collection<Long> ids) {
        return mongoTemplate.find(Query.query(Criteria.where(BaseEntity.ID).in(ids).and(BaseEntity.Fields.isDeleted).is(DeletedEnums.N.code())), tClass);
    }

    public List<T> queryList(Query query) {
        return mongoTemplate.find(query, tClass);
    }

    /**
     * 根据ids获取列表
     * <p>
     * 强制获取所有id匹配的数据，校验状态
     *
     * @param ids ids
     * @return 结果集
     */
    public List<T> listExists(Collection<Long> ids) {
        return mongoTemplate.find(Query.query(Criteria.where(BaseEntity.ID).in(ids).and(BaseEntity.Fields.isDeleted).is(DeletedEnums.N.code())), tClass);
    }

    /**
     * 根据ids获取相关信息
     *
     * @param ids ids
     * @return map(id - > R)
     */
    public Map<Long, T> map(Collection<Long> ids) {
        return map(ids, Function.identity());
    }

    /**
     * 根据ids获取相关信息
     *
     * @param ids       ids
     * @param converter 转换器
     * @param <R>       泛型
     * @return map(id - > R)
     */
    public <R> Map<Long, R> map(Collection<Long> ids, Function<T, R> converter) {
        if (converter == null) {
            throw OrmException.pop("查询错误，请设置转换器");
        }
        return listByIds(ids).stream().collect(Collectors.toMap(BaseEntity::getId, converter, (v1, v2) -> v2));
    }

    /**
     * 根据id删除对象 --- 删除逻辑 (推荐使用)
     * <p>
     * 使用后：getById -> entity.getIsDeleted = 1
     *
     * @param id id
     * @return 结果
     */
    public boolean deleteById(Long id) {
        return mongoTemplate.updateFirst(
                Query.query(Criteria.where(BaseEntity.ID).is(id)),
                Update.update(BaseEntity.Fields.isDeleted, DeletedEnums.Y.code()),
                tClass).getModifiedCount() > 0;
    }


    /**
     * 根据id删除对象 --- 删除逻辑 (推荐使用)
     * <p>
     * 使用后：getById -> entity.getIsDeleted = 1
     *
     * @param id      id
     * @param deleter deleter
     * @return 结果
     */
    public boolean deleteById(Long id, Long deleter) {
        return mongoTemplate.updateFirst(
                Query.query(Criteria.where(BaseEntity.ID).is(id)),
                Update.update(BaseEntity.Fields.isDeleted, DeletedEnums.Y.code())
                        .set(BaseEntity.Fields.updateBy, deleter),
                tClass).getModifiedCount() > 0;
    }


    /**
     * 根据id删除对象 --- 删除逻辑 (推荐使用)
     * <p>
     * 使用后：getById -> entity.getIsDeleted = 1
     *
     * @param ids ids
     * @return 结果
     */
    public boolean deleteByIds(Collection<Long> ids) {
        return mongoTemplate.updateMulti(
                Query.query(Criteria.where(BaseEntity.ID).in(ids)),
                Update.update(BaseEntity.Fields.isDeleted, DeletedEnums.Y.code()),
                tClass).getModifiedCount() > 0;
    }


    /**
     * 根据id删除对象 --- 删除数据 (谨慎使用)
     * <p>
     * 使用后：getById -> null
     *
     * @param id id
     * @return 结果
     */
    public boolean removeById(Long id) {
        return mongoTemplate.remove(Query.query(Criteria.where(BaseEntity.ID).is(id)), tClass).getDeletedCount() > 0;
    }


    /**
     * 根据id删除对象 --- 删除数据 (谨慎使用)
     * <p>
     * getById -> null
     *
     * @param ids ids
     * @return 结果
     */
    public boolean removeByIds(Collection<Long> ids) {
        return mongoTemplate.remove(Query.query(Criteria.where(BaseEntity.ID).in(ids)), tClass).getDeletedCount() > 0;
    }

    /**
     * 根据id自增某一字段
     *
     * @param id     id
     * @param inc    增量
     * @param fields 字段名列表
     * @return 自增后的值
     */
    public T inc(Long id, int inc, String... fields) {
        return inc(Collections.singletonList(id), inc, fields);
    }

    /**
     * 根据ids自增某些字段
     *
     * @param ids    ids
     * @param inc    增量
     * @param fields 字段名列表
     * @return 自增后的值
     */
    public T inc(Collection<Long> ids, int inc, String... fields) {
        Update update = new Update();
        for (String field : fields) {
            update.inc(field, inc);
        }
        return mongoTemplate.findAndModify(
                Query.query(Criteria.where(BaseEntity.ID).in(ids)),
                update.set(BaseEntity.Fields.updateTime, LocalDateTime.now()),
                new FindAndModifyOptions().upsert(Boolean.TRUE).returnNew(Boolean.TRUE),
                tClass);
    }

    /**
     * 根据id自增某一字段
     *
     * @param id    id
     * @param field 字段名
     * @param inc   增量
     * @return 自增后的值
     */
    public T inc(Long id, String field, int inc) {
        return inc(Collections.singletonList(id), inc, field);
    }

    /**
     * 返回仅有的字段结果 --- 单个查询
     *
     * @param querySetter     查询设置器
     * @param tableName       表名/集合名
     * @param queryFieldClass 仅需的查询字段类名
     * @param <O>             泛型
     * @return 泛型
     */
    public <O> O findOneInclude(Consumer<Query> querySetter, String tableName, Class<O> queryFieldClass) {
        Query query = new Query();
        if (querySetter != null) {
            querySetter.accept(query);
        }
        List<String> names = getClassFields(queryFieldClass);
        names.forEach(n -> query.fields().include(n));
        return mongoTemplate.findOne(query, queryFieldClass, tableName);
    }

    /**
     * 返回仅有的字段结果 --- 返回列表
     *
     * @param querySetter     查询设置器
     * @param tableName       表名/集合名
     * @param queryFieldClass 仅需的查询字段类名
     * @param <O>             泛型
     * @return 泛型
     */
    public <O> List<O> findInclude(Consumer<Query> querySetter, String tableName, Class<O> queryFieldClass) {
        Query query = Query.query(Criteria.where(BaseEntity.Fields.isDeleted).is(DeletedEnums.N.code()));
        if (querySetter != null) {
            querySetter.accept(query);
        }
        List<String> names = getClassFields(queryFieldClass);
        names.forEach(n -> query.fields().include(n));
        return mongoTemplate.find(query, queryFieldClass, tableName);
    }

    /**
     * 获取类中的所有字段
     *
     * @param aClass 类
     * @return 字段名列表
     */
    private List<String> getClassFields(Class<?> aClass) {
        List<String> names = fieldMap.get(aClass);
        if (CollUtil.isEmpty(names)) {
            names = Arrays.stream(ClassUtil.getDeclaredFields(aClass)).map(Field::getName).collect(Collectors.toList());
            fieldMap.put(aClass, names);
        }
        return names;
    }


    /**
     * 聚合查询
     */
    public <R> List<R> queryPageAggregateList(TypedAggregation<T> aggregation, Class<R> outputType) {
        AggregationResults<R> results = mongoTemplate.aggregate(aggregation, outputType);

        return results.getMappedResults();
    }

    /**
     * 聚合查询
     */
    public <R> List<R> queryPageAggregateList(Aggregation aggregation, Class<R> outputType) {
        AggregationResults<R> results = mongoTemplate.aggregate(aggregation, tClass, outputType);

        return results.getMappedResults();
    }

    public TypedAggregation<T> getOperationList(Criteria criteria, Integer pageNumber, Integer pageSize, List<SortOperationMapping> sort, List<LookupOperation> lookUp, List<AggregationOperation> extroOperations) {
        // 联合查询封装类
        List<AggregationOperation> operations = new LinkedList<>(lookUp);
        // 过滤条件
        MatchOperation match = Aggregation.match(criteria);
        operations.add(match);

        // 创建查询对象
        if (sort != null) {
            for (SortOperationMapping sortMap : sort) {
                SortOperation sortOperation = Aggregation.sort(Sort.by(sortMap.getSort(), sortMap.getOrderBy()));
                operations.add(sortOperation);
            }
        }
        // 分页和排序
        if (pageSize == null) pageSize = 10;
        SkipOperation skip = Aggregation.skip((long) (pageNumber - 1) * pageSize);
        LimitOperation limit = Aggregation.limit(pageSize);
        operations.add(skip);
        operations.add(limit);
        if (extroOperations != null) {
            operations.addAll(extroOperations);
        }
        return Aggregation.newAggregation(tClass, operations);
    }

    /**
     * 聚合分页查询
     */
    public <R> Page<R> queryPageAggregateList(Criteria criteria, Integer pageNumber, Integer pageSize, List<SortOperationMapping> sort, List<LookupOperation> lookUp, Class<R> outputType, List<AggregationOperation> extroOperations) {
        TypedAggregation<T> aggregation = getOperationList(criteria, pageNumber, pageSize, sort, lookUp, extroOperations);

        List<R> query = queryPageAggregateList(aggregation, outputType);

        return getPage(criteria, pageSize, query, lookUp);
    }


    public <R> Page<R> getPage(Criteria criteria, Integer pageSize, List<R> query, List<LookupOperation> lookUp) {
        List<AggregationOperation> operations = new LinkedList<>(lookUp);
        operations.add(Aggregation.match(criteria));
        // 查询总记录数
        long count = mongoTemplate.aggregate(Aggregation.newAggregation(tClass, operations), tClass).getMappedResults().size();
        // 返回分好页的数据
        return new Page<>(pageSize, count, query);
    }


    /* 根据id获取对象
     *
     * @param id id
     * @return 结果
     */
    public T getValidById(Long id) {
        return mongoTemplate.findOne(Query.query(Criteria.where(BaseEntity.ID).is(id).and(BaseEntity.Fields.isDeleted).is(DeletedEnums.N.code())), tClass);
    }


    public List<T> findAllIncludeDeleted() {
        return mongoTemplate.find(Query.query(new Criteria()), tClass);
    }

    /**
     * 根据id删除字段的值
     *
     * @param id         id
     * @param fieldNames 字段
     */
    public void unsetById(Long id, String... fieldNames) {
        if (ArrayUtil.isEmpty(fieldNames)) {
            return;
        }
        Update update = new Update();
        Arrays.stream(fieldNames).forEach(update::unset);
        mongoTemplate.updateFirst(Query.query(Criteria.where(BaseEntity.ID).is(id)), update, tClass);
    }

    /**
     * 根据id更新字段值
     *
     * @param id        id
     * @param fieldName 字段
     * @param <V>       泛型
     * @param value     值
     */
    public <V> void setById(Long id, String fieldName, V value) {
        mongoTemplate.updateFirst(Query.query(Criteria.where(BaseEntity.ID).is(id)), Update.update(fieldName, value), tClass);
    }

    /**
     * 根据id批量更新字段值
     *
     * @param id     id
     * @param setter 字段设置器
     * @param <V>    泛型
     */
    public <V> void setById(Long id, Map<String, V> setter) {
        if (CollUtil.isEmpty(setter)) {
            return;
        }
        Update update = new Update();
        setter.forEach(update::set);
        mongoTemplate.updateFirst(Query.query(Criteria.where(BaseEntity.ID).is(id)), update, tClass);
    }


    /**
     * 根据筛选条件获取分页数据
     *
     * @param page    分页
     * @param filter  过滤条件
     * @param convert 转化函数
     * @param <R>     指定返回泛型
     * @return 分页后的返回数据
     */
    public <R> Page<R> page(PageVO page, Function<T, R> convert, Consumer<Criteria> filter, Sort.Order... orders) {
        // 校验
        if (page == null) {
            throw OrmException.pop("请设置分页参数");
        }
        // 分页参数
        long skip = (page.getCurrentPage() - 1L) * page.getPageSize();
        int limit = page.getPageSize();
        return page(skip, limit, convert, filter, orders).setPageNo(page.getCurrentPage()).setPageSize(page.getPageSize());
    }

    public <R> Page<R> page(PageVO page, Function<T, R> convert, Consumer<Criteria> filter) {
        // 校验
        if (page == null) {
            throw OrmException.pop("请设置分页参数");
        }
        // 分页参数
        long skip = (page.getCurrentPage() - 1L) * page.getPageSize();
        int limit = page.getPageSize();
        String order = page.getOrder();
        if (ObjectUtil.isNotEmpty(order)) {
            Sort.Order sort = page.isOrderDesc()
                    ? Sort.Order.desc(order)
                    : Sort.Order.asc(order);
            return page(skip, limit, convert, filter, sort);
        } else {
            return page(skip, limit, convert, filter);
        }
    }

    /**
     * 根据筛选条件获取分页数据
     *
     * @param skip    跳过条数
     * @param limit   获取条数
     * @param filter  过滤条件
     * @param convert 转化函数
     * @param <R>     指定返回泛型
     * @return 分页后的返回数据
     */
    public <R> Page<R> page(long skip, int limit, Function<T, R> convert, Consumer<Criteria> filter, Sort.Order... orders) {
//        if (convert == null) {
//            throw OrmException.pop("请设置转化函数");
//        }
        // 结果集
        Page<R> result = new Page<>();
        // 过滤条件
        Criteria main = Criteria.where(BaseEntity.Fields.isDeleted).is(DeletedEnums.N.code());
        if (filter != null) {
            filter.accept(main);
        }
        Query query = Query.query(main);
        // 查询总数
        long total = mongoTemplate.count(query, tClass);
        result.setTotal(total);
        if (total < 1 || total <= skip) {
            return result.setResult(Lists.newArrayList());
        }
        // 设置排序
        if (ArrayUtil.isNotEmpty(orders)) {
            query.with(Sort.by(orders));
        }
        List<T> source = mongoTemplate.find(query.skip(skip).limit(limit), tClass);
        if (CollUtil.isEmpty(source)) {
            return result.setResult(Lists.newArrayList());
        }
        // 转换数据
        return result.setResult(source.stream().map(convert).collect(Collectors.toCollection(ArrayList::new)));
    }

    public T findOne(Query query) {
        return mongoTemplate.findOne(query, tClass);
    }

    public boolean removeByCriteria(Criteria criteria) {
        return mongoTemplate.remove(Query.query(criteria), tClass).getDeletedCount() > 0;
    }

    public List<T> find(Query query) {
        return mongoTemplate.find(query, tClass);

    }

    public long count(Criteria criteria) {
        return mongoTemplate.count(Query.query(criteria), tClass);
    }

    public long count() {
        return mongoTemplate.count(new Query(), tClass);
    }

    /**
     * 删除集合
     */
    public void drop() {
        mongoTemplate.dropCollection(tClass);
    }

}

