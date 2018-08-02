package com.itea.util;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONObject;

public class Utils {

	/**
	 * 将Map转换为对象
	 * 
	 * @param paramMap
	 * @param cls
	 * @return
	 */
	public static <T> T parseMap2Object(Map<String, Object> paramMap, Class<T> cls) {
		return JSONObject.parseObject(JSONObject.toJSONString(paramMap), cls);
	}

	/**
	 * 判断string为空
	 * 
	 * @param str
	 *            字符串
	 * @return 若str为null，或者trim后长度为0，返回true
	 */
	public static boolean isEmpty(String str) {
		if (str == null || str.trim().length() == 0)
			return true;
		return false;
	}

	/**
	 * 判断string不为空
	 * 
	 * @param str
	 *            字符串
	 * @return 若str不为null，且trim后长度不为0，返回true
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	/**
	 * 判断string[]为空
	 * 
	 * @param str[]
	 *            字符串数组
	 * @return 若str为null，或者长度为0，返回true
	 */
	public static boolean isEmpty(String[] str) {
		if (str == null || str.length == 0)
			return true;
		return false;
	}

	/**
	 * 判断string[]不为空
	 * 
	 * @param str
	 *            字符串数组
	 * @return 若str不为null，且长度不为0，返回true
	 */
	public boolean isNotEmpty(String[] str) {
		return !isEmpty(str);
	}

	/**
	 * 判断list列表为空
	 * 
	 * @param <T>
	 *            泛型
	 * 
	 * @param list
	 *            List列表
	 * @return 若list列表不存在 或者 list列表不包含元素,返回true
	 */
	public static <T> boolean listIsEmpty(List<T> list) {
		return list == null || list.isEmpty();
	}

	/**
	 * 判断list列表不为空
	 * 
	 * @param <T>
	 *            泛型
	 * 
	 * @param list
	 *            List列表
	 * @return 若list列表存在 并且 list列表包含元素,返回true
	 */
	public static <T> boolean listIsNotEmpty(List<T> list) {
		return !listIsEmpty(list);
	}

	/**
	 * 判断map映射为空
	 * 
	 * @param <K>
	 *            泛型
	 * @param <V>
	 *            泛型
	 * 
	 * @param map
	 *            Map映射
	 * @return 若map映射不存在 或者map映射未包含键-值映射关系,返回true
	 */
	public static <K, V> boolean mapIsEmpty(Map<K, V> map) {
		return map == null || map.isEmpty();
	}

	/**
	 * 判断map映射不为空
	 * 
	 * @param <K>
	 *            泛型
	 * @param <V>
	 *            泛型
	 * 
	 * @param map
	 *            Map映射
	 * @return 若map映射存在 并且map映射包含键-值映射关系,返回true
	 */
	public static <K, V> boolean mapIsNotEmpty(Map<K, V> map) {
		return !mapIsEmpty(map);
	}

	/**
	 * 将double数值四舍五入保留两位小数返回
	 * 
	 * @param double
	 *            要格式化的double数值
	 * @return double
	 */
	public static String paseDouble(double d) {
		NumberFormat nf = NumberFormat.getNumberInstance();
		// 保留两位小数
		nf.setMaximumFractionDigits(2);
		// 如果不需要四舍五入，可以使用RoundingMode.DOWN
		nf.setRoundingMode(RoundingMode.UP);
		return nf.format(d);
	}


}
