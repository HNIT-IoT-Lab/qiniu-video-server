package cn.hnit.utils;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * Spring 表达式语言（SpEL）工具类<br/>
 * 更多SpEL请参考Spring官网<a href="https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#expressions"/>
 *
 * @author 梁峰源
 * @since 2022年9月16日11:22:52
 */
public class SpelUtils {
    private static final ExpressionParser expressionParser = new SpelExpressionParser();

    private SpelUtils() {
    }

    public static Object getValue(String expression) {
        return expressionParser.parseExpression(expression);
    }

    public static Object getValue(Object object, String expression) {
        // 解析上下文
        EvaluationContext context = new StandardEvaluationContext(object);
        return expressionParser.parseExpression(expression).getValue(context);
    }

    public static <T> T getValue(Object object, String expression, @Nullable Class<T> desiredResultType) {
        // 解析上下文
        EvaluationContext context = new StandardEvaluationContext(object);
        return expressionParser.parseExpression(expression).getValue(context, desiredResultType);
    }

    public static void setValue(Object object, String expression, Object value) {
        // 解析上下文
        EvaluationContext context = new StandardEvaluationContext(object);
        expressionParser.parseExpression(expression).setValue(context, value);
    }

    public static String patternStr(Map<String, Object> param, String expression) {
        EvaluationContext ctx = new StandardEvaluationContext();
        param.forEach(ctx::setVariable);
        return expressionParser.parseExpression(expression, new TemplateParserContext()).getValue(ctx, String.class);
    }
}
