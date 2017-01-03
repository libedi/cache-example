package com.libedi.myproject.cache_example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

/**
 * 코드 캐싱 관리 class
 * @author 박상준
 *
 */
public class CodeCacheManager implements CacheManager {
	private final Map<String, Object> cacheMap = new ConcurrentHashMap<>();
	private final List<String> keyList = Collections.synchronizedList(new ArrayList<>());
	
	private StopWatch stopWatch;

	private long minuteToExpire;
	public void setMinuteToExpire(long minuteToExpire) {
		this.minuteToExpire = minuteToExpire * 1000L * 60L;
	}

	private int cacheSize;
	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}
	
	public CodeCacheManager(){
		this.stopWatch = new StopWatch();
		this.setMinuteToExpire(10);
		this.setCacheSize(30);
	}
	
	/**
	 * 캐싱된 코드 조회 - LRU 알고리즘 방식
	 * @return Object : 캐싱된 코드가 없으면 null 반환
	 */
	@Override
	public Object fetch(String key) {
		synchronized(this){
			// 캐시가 비었을시 캐시만료 타이머 작동
			if(cacheMap.isEmpty()){
				this.stopWatch.reset();
				this.stopWatch.start();
			}
			// 캐싱에서 조회된 코드는 우선순위를 최상위로 이동
			Object fetchObj = cacheMap.get(key);
			if(fetchObj != null && !this.isExpired()){
				keyList.remove(key);
				keyList.add(0, key);
				return fetchObj;
			}
			// 만료되면 캐시 및 타이머 초기화
			if(this.isExpired()){
				initCache();
			}
			return null;
		}
	}

	/**
	 * 코드 캐싱
	 */
	@Override
	public void addCache(String key, Object obj) {
		synchronized(this){
			if(cacheMap.get(key) != null){
				// 이전에 캐싱된 코드 갱신을 위해 삭제
				keyList.remove(key);
			} else if(keyList.size() == cacheSize){
				// 캐시가 가득차면, LRU 방식으로 우선순위가 최하위인 코드를 삭제
				String lastKey = keyList.remove(keyList.size() - 1);
				cacheMap.remove(lastKey);
			}
			cacheMap.put(key, obj);
			keyList.add(0, key);
		}
	}
	
	@Override
	public void initCache() {
		synchronized(this){
			cacheMap.clear();
			keyList.clear();
		}
	}
	
	@Override
	public boolean useCache(Object... queries){
		return StringUtils.contains((String) queries[0], "COMMON.selectComCdSmlclasList");
	}
	
	/**
	 * 캐시 만료 여부
	 * @return boolean
	 */
	private boolean isExpired(){
		return this.stopWatch.getTime() > this.minuteToExpire;
	}

}

