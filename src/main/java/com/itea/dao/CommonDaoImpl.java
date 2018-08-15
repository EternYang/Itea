package com.itea.dao;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

/**
 * @author Eternal.Y
 * @version 1.0
 *
 * 
 */
@Repository
@Transactional
public class CommonDaoImpl implements CommonDao {

	// hi
	private static final Logger log = Logger.getLogger(CommonDaoImpl.class);
	
	@Resource
	private EntityManager em;

	/**
	 * 执行原生sql语句，带占位符
	 * @param sql 语句
	 * @param objects 占位符的值
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> findBySql(String sql, Object... objects) {
		//执行原生SQL
		Query nativeQuery = em.createNativeQuery(sql);
		//指定返回对象类型
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		log.info("findBySql is doing now......");
		if(objects != null && objects.length > 0){
			for(int i = 0 ; i < objects.length ; i++){
				nativeQuery.setParameter(i, objects[i]);
			}
		}
		//返回对象
		List<Map<String,Object>> resultList = nativeQuery.getResultList(); 
		return resultList;
	}

	@Override
	public <T> T findById(String tablename, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 执行hql查询，带占位符
	 * @param hql yuju 
	 * @param cls 要封装成的实体类
	 * @param objects 占位符的值
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<?> findByHql(String hql, Class cls,String...objects) {
		Query query = em.createQuery(hql,cls);
		if(objects != null && objects.length > 0){
			for (int i = 0; i < objects.length; i++) {
				query.setParameter(i, objects[i]);
			}
		}
		return query.getResultList();
	}

	
	/**
	 * 根据对应参数执行原生update语句，带占位符
	 * @param sql 要执行的语句
	 * @param objects 占位参数值
	 * */
	@Override
	public int updateBySql(String sql, Object... objects) {
		Query query = em.createNativeQuery(sql);
		if(objects != null && objects.length > 0){
			for(int i = 0 ; i < objects.length ; i++){
				query.setParameter(i, objects[i]);
			}
		}
		return query.executeUpdate();
	}
	
}
