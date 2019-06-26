package com.hanter.xpulltorefresh;

import android.view.View;

import com.hanter.xpulltorefresh.calculator.Calculator;
import com.hanter.xpulltorefresh.calculator.CoordinatorLayoutCalculator;
import com.hanter.xpulltorefresh.calculator.ListViewCalculator;
import com.hanter.xpulltorefresh.calculator.NestedScrollViewCalculator;
import com.hanter.xpulltorefresh.calculator.RecyclerViewCalculator;
import com.hanter.xpulltorefresh.calculator.ScrollViewCalculator;
import com.hanter.xpulltorefresh.calculator.ScrollingViewCalculator;
import com.hanter.xpulltorefresh.calculator.WebViewCalculator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;

/**
 * 类名：CalculatorManager <br/>
 * 描述：管理
 * 创建时间：2017/03/05 10:52
 *
 * @author hanter
 * @version 1.0
 */
public class CalculatorManager {

    public static final LinkedList<Class> sCalculatorList;

    static {
        sCalculatorList = new LinkedList<>();
        add(ListViewCalculator.class);
        add(NestedScrollViewCalculator.class);
        add(RecyclerViewCalculator.class);
        add(ScrollViewCalculator.class);
        add(WebViewCalculator.class);
        add(ScrollingViewCalculator.class);
        add(CoordinatorLayoutCalculator.class);
    }

    public static void push(Class clazz) {
        sCalculatorList.push(clazz);
    }

    public static void add(Class clazz) {
        sCalculatorList.add(clazz);
    }

    /**
     * 获取对应类处理的Calculator
     * @param content 内容布局对象
     * @return 与内容布局类对应的Calculator
     */
    public static Class findCalculator(Object content) {
        Class supportClass = null;

        for (Class clazz : CalculatorManager.sCalculatorList) {
            Type[] types = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments();

            // 取第一个
            if (((Class) types[0]).isInstance(content)) {
                supportClass = clazz;
                break;
            }
        }

        return supportClass;
    }

    public static Calculator createCalculator(Class clazz, PullToRefreshLayout layout, View content) {
        Calculator result = null;

        try {
            Type[] types = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments();
            Class contentClass = (Class) types[0];
            @SuppressWarnings("unchecked")
            Constructor<Calculator> cs = clazz.getConstructor(PullToRefreshLayout.class, contentClass);
            result = cs.newInstance(layout, content);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            // InstantiationException
        } catch (IllegalAccessException e) {
            // IllegalAccessException
        } catch (InvocationTargetException e) {
            // InvocationTargetException
        }

        return result;
    }

}
