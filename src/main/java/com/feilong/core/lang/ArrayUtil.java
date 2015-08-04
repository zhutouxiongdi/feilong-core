/*
 * Copyright (C) 2008 feilong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.feilong.core.lang;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.feilong.core.bean.PropertyUtil;
import com.feilong.core.lang.entity.ToStringConfig;
import com.feilong.core.util.Validator;

/**
 * 数组工具类.
 * 
 * <h3>判断是否包含</h3>
 * 
 * <blockquote>
 * <ul>
 * <li>{@link ArrayUtils#contains(boolean[], boolean)}</li>
 * <li>{@link ArrayUtils#contains(byte[], byte)}</li>
 * <li>{@link ArrayUtils#contains(char[], char)}</li>
 * <li>{@link ArrayUtils#contains(double[], double)}</li>
 * <li>{@link ArrayUtils#contains(float[], float)}</li>
 * <li>{@link ArrayUtils#contains(int[], int)}</li>
 * <li>{@link ArrayUtils#contains(long[], long)}</li>
 * <li>{@link ArrayUtils#contains(Object[], Object)}</li>
 * <li>{@link ArrayUtils#contains(short[], short)}</li>
 * <li>{@link ArrayUtils#contains(double[], double, double)}</li>
 * </ul>
 * </blockquote>
 * 
 * @author feilong
 * @version 1.4.0 2015年8月3日 上午3:06:20
 * @since 1.4.0
 */
public final class ArrayUtil{

    /** Don't let anyone instantiate this class. */
    private ArrayUtil(){
        //AssertionError不是必须的. 但它可以避免不小心在类的内部调用构造器. 保证该类在任何情况下都不会被实例化.
        //see 《Effective Java》 2nd
        throw new AssertionError("No " + getClass().getName() + " instances for you!");
    }

    /**
     * 得到数组中的某个元素.
     * 
     * <p>
     * (Returns the value of the indexed component in the specified array object. <br>
     * The value is automatically wrapped in an object if it has a primitive type.)
     * </p>
     *
     * @param <T>
     *            the generic type
     * @param array
     *            数组
     * @param index
     *            索引
     * @return 返回指定数组对象中索引组件的值,the (possibly wrapped) value of the indexed component in the specified array
     * @throws ArrayIndexOutOfBoundsException
     *             If the specified {@code index} argument is negative, or if it is greater than or equal to the length of the specified
     *             array
     * @see java.lang.reflect.Array#get(Object, int)
     */
    @SuppressWarnings("unchecked")
    public static <T> T getElement(Object array,int index) throws ArrayIndexOutOfBoundsException{
        return (T) Array.get(array, index);
    }

    /**
     * 数组转成 ({@link java.util.ArrayList ArrayList})，此方法返回的list可以进行add等操作.
     * <p>
     * 注意 :{@link java.util.Arrays#asList(Object...) Arrays#asList(Object...)}返回的list,没有实现 {@link java.util.Collection#add(Object)
     * Collection#add(Object)}等方法<br>
     * 因此,使用 {@link ArrayList#ArrayList(java.util.Collection)} 来进行重新封装返回
     * </p>
     * 
     * @param <T>
     *            the generic type
     * @param arrays
     *            T数组
     * @return 数组转成 List(ArrayList)<br>
     *         if Validator.isNullOrEmpty(arrays), return null,else return {@code new ArrayList<T>(Arrays.asList(arrays));}
     * @see java.util.Arrays#asList(Object...)
     */
    public static <T> List<T> toList(T[] arrays){
        if (Validator.isNullOrEmpty(arrays)){
            return Collections.emptyList();
        }
        //如果直接使用 Arrays.asList(arrays)方法 返回的是Arrays类的内部类的对象ArrayList,没有实现AbstractList类的add方法，如果 strList.add("c");导致抛异常! 
        return new ArrayList<T>(Arrays.asList(arrays));
    }

    /**
     * 将数组 通过 {@link ToStringConfig} 拼接成 字符串.
     * 
     * <code>
     * <pre>
     * Example 1:
     * ArrayUtil.toString(new ToStringConfig(),"a","b")  return "a,b"
     * 
     * Example 2:
     * ToStringConfig toStringConfig=new ToStringConfig(",");
     * toStringConfig.setIsJoinNullOrEmpty(false);
     * ArrayUtil.toString(new ToStringConfig(),"a","b",null)  return "a,b"
     * </pre>
     * </code>
     *
     * @param <T>
     *            the generic type
     * @param toStringConfig
     *            the join string entity
     * @param arrays
     *            <span style="color:red">请使用包装类型,比如 Integer []arrays,而不是 int []arrays</span>
     * @return <ul>
     *         <li>如果 arrays 是null 或者Empty ,返回null</li>
     *         <li>否则循环,拼接 {@link ToStringConfig#getConnector()}</li>
     *         </ul>
     * 
     * @deprecated 有局限性, 具体参见参数 <code>arrays</code>
     */
    @Deprecated
    public static <T> String toString(ToStringConfig toStringConfig,T...arrays){
        if (Validator.isNullOrEmpty(arrays)){
            return StringUtils.EMPTY;
        }
        //ConvertUtils.primitiveToWrapper(type)
        ToStringConfig useToStringConfig = null == toStringConfig ? new ToStringConfig() : toStringConfig;

        String connector = useToStringConfig.getConnector();
        StringBuilder sb = new StringBuilder();
        for (int i = 0, j = arrays.length; i < j; ++i){
            T t = arrays[i];

            //如果是null 或者 empty，但是参数值是不拼接，那么继续循环
            if (Validator.isNullOrEmpty(t) && !useToStringConfig.getIsJoinNullOrEmpty()){
                continue;
            }
            sb.append(t);
            if (Validator.isNotNullOrEmpty(connector)){
                sb.append(connector);
            }
        }

        //由于上面的循环中，最后一个元素可能是null或者empty，判断加还是不加拼接符有点麻烦，因此，循环中统一拼接，但是循环之后做截取处理
        String returnValue = sb.toString();

        if (Validator.isNotNullOrEmpty(connector) && returnValue.endsWith(connector)){
            //去掉最后的拼接符
            return StringUtil.substringWithoutLast(returnValue, connector.length());
        }
        return returnValue;
    }

    /**
     * 将array 分组.
     * 
     * <code>
     * <pre>
     * 
     * Example 1:
     * if Integer[] array = { 1, 1, 1, 2, 2, 3, 4, 5, 5, 6, 7, 8, 8 };
     * 
     * will return 
     *      {
     *         "1":         [
     *             1,
     *             1,
     *             1
     *         ],
     *         "2":         [
     *             2,
     *             2
     *         ],
     *         "3": [3],
     *         "4": [4],
     *         "5":         [
     *             5,
     *             5
     *         ],
     *         "6": [6],
     *         "7": [7],
     *         "8":         [
     *             8,
     *             8
     *         ]
     *     }
     * }
     * </pre></code>
     *
     * @param <T>
     *            the generic type
     * @param array
     *            the array
     * @return the map< t, list< t>>
     * @since 1.0.8
     */
    public static <T> Map<T, List<T>> group(T[] array){
        if (null == array){
            return Collections.emptyMap();
        }
        //视需求  可以换成 HashMap 或者TreeMap
        Map<T, List<T>> map = new WeakHashMap<T, List<T>>(array.length);
        for (T t : array){
            List<T> valueList = map.get(t);
            if (null == valueList){
                valueList = new ArrayList<T>();
            }
            valueList.add(t);
            map.put(t, valueList);
        }
        return map;
    }

    /**
     * Group 对象.
     *
     * @param <O>
     *            the generic type
     * @param <T>
     *            the generic type
     * @param objectArray
     *            对象数组
     * @param propertyName
     *            对面里面属性的名称
     * @return the map< t, list< o>>
     * @see com.feilong.core.bean.PropertyUtil#getProperty(Object, String)
     * @see com.feilong.core.util.CollectionsUtil#group(java.util.Collection, String)
     * @since 1.0.8
     */
    public static <O, T> Map<T, List<O>> group(O[] objectArray,String propertyName){
        if (null == objectArray){
            return Collections.emptyMap();
        }

        if (Validator.isNullOrEmpty(propertyName)){
            throw new NullPointerException("the propertyName is null or empty!");
        }
        //视需求  可以换成 HashMap 或者TreeMap
        Map<T, List<O>> map = new LinkedHashMap<T, List<O>>(objectArray.length);
        for (O o : objectArray){
            T t = PropertyUtil.getProperty(o, propertyName);
            List<O> valueList = map.get(t);
            if (null == valueList){
                valueList = new ArrayList<O>();
            }
            valueList.add(o);
            map.put(t, valueList);
        }
        return map;
    }
}