package cn.hnit.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterators;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Java 流工具包
 */
public class StreamUtil {

	/**
	 * ➜ 用于流间对比时非匹配元素的处理
	 * @param stream 对比流
	 * @param supplier 处理方式
	 * @return
	 */
	public static <T> Stream<T> onEmpty(Stream<T> stream, Supplier<T> supplier) {
	    Iterator<T> iterator = stream.iterator();
	    if (iterator.hasNext()) {
	        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
	    } else {
	        return Stream.of(supplier.get());
	    }
	}

	/**
	 * ➜ 合并更新Map
	 * @param orig 原Map
	 * @param update 新Map,与原Map可为交集
	 * @return
	 */
	public static <S, T> Map<S, T> mergeMap(Map<S, T> orig, Map<S, T> update) {
        return Stream.of(orig, update).map(Map::entrySet).flatMap(Collection::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b));
	}

	/**
	 * 流唯一操作时自定义唯一条件
	 * @param extrator 唯一条件
	 * @return
	 */
	public static <T> Predicate<T> distinct(Function<? super T, Object> extrator) {
		Map<Object, Boolean> map = new ConcurrentHashMap<>();
		return e -> map.putIfAbsent(extrator.apply(e), Boolean.TRUE) == null;
	}

}
