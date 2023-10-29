package cn.hnit.utils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptEngineUtil {

    public static <T> T computByFormula(String formula) throws ScriptException {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        ScriptEngine engine=engineManager.getEngineByName("javascript");
        return (T)engine.eval(formula);
    }
}
