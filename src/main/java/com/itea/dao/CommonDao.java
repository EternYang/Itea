package com.itea.dao;

import java.util.List;
import java.util.Map;

public interface CommonDao {

	List<Map<String,Object>> findBySql(String sql,Object...objects);
	
	int updateBySql(String sql,Object...objects);
	
	<T> T findById(String tablename,String id);
	
	@SuppressWarnings("rawtypes")
	List<?> findByHql(String hql,Class cls, String...objects);
	
}
