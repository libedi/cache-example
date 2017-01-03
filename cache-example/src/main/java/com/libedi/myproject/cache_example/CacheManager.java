package com.libedi.myproject.cache_example;

/**
 * 캐싱 관리 Interface
 * @author 박상준
 *
 */
public interface CacheManager {
	/**
	 * 캐싱된 객체 조회
	 * @return Object
	 */
	Object fetch(String key);
	
	/**
	 * 객체 캐싱
	 * @param obj
	 */
	void addCache(String key, Object obj);
	
	/**
	 * 캐시 초기화
	 */
	void initCache();
	
	/**
	 * 캐시 사용여부
	 * @param objs
	 * @return boolean
	 */
	boolean useCache(Object... objs);
}
