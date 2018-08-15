package com.itea.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itea.bean.Dictionary;
import com.itea.dao.CommonDao;
import com.itea.util.Utils;

@Service
public class IteaService {

	private static final Logger log = Logger.getLogger(IteaService.class);

	@Autowired
	private CommonDao commonDao;

	@SuppressWarnings("unchecked")
	public List<Dictionary> queryDicts() {
		String hql = "from Dictionary";
		return (List<Dictionary>) commonDao.findByHql(hql, Dictionary.class);
	}

	public List<Map<String, Object>> queryOrderByStatus(String status) {
		String sql = "SELECT orderId,orderNO,COUNT(OrderNO) as total,orderTotal FROM y_QueNumberOrder where OrderStatus = ?0"
				+ " and CallDate=curdate() GROUP BY OrderNO,OrderTotal,orderId  ORDER BY  OrderNO";
		return commonDao.findBySql(sql, status);
	}

	public int setStatusByOrderId(String orderId, String status) {
		String sql = "update y_quenumberorder set OrderStatus = ?0 where OrderId = ?1";
		int row = commonDao.updateBySql(sql, status, orderId);
		if (row == 1) {
			log.info("update success");
		} else
			log.info("error!!!!!!");
		return row;
	}

	public List<Map<String, Object>> queryLessInventory() {
		String sql = "select * from (SELECT *, IFNULL((ItemStore * 1.0/ItemFullLoad * 100 ), 0) as percentage "
				+ "FROM y_ItemDetail where ItemWeightWay='sub' and itemFullLoad <> 0)" + 
				 "as tab where tab.percentage < 25 order by tab.percentage";
		return commonDao.findBySql(sql);
	}

	public List<Map<String, Object>> queryWorkStatus() {
		StringBuffer sql = new StringBuffer("select * from y_CurWorkingLog  order by workingtime desc limit 5");
		return commonDao.findBySql(sql.toString());
	}

	public List<Map<String, Object>> queryAllInventory() {
		String sql = "SELECT *, IFNULL((ItemStore * 1.0/ItemFullLoad * 100 ), 0) as percentage "
				+ "FROM y_ItemDetail where ItemWeightWay='sub' or ItemWeightWay='add' and itemFullLoad <> 0 order by ItemStore";
		return commonDao.findBySql(sql);
	}

	public List<Map<String, Object>> queryError(String errorcode) {
		String sql = "select * from y_errorcode where ErrorCode = ?0";
		return commonDao.findBySql(sql,errorcode);
	}

	public int changePrepared(String itemName, String itemPrepared) {
		String sql = "update y_ItemDetail set ItemPrepared = ?0 where ItemName = ?1";
		return commonDao.updateBySql(sql, itemPrepared,itemName);
	}

	public List<Map<String,Object>> queryPrepared(String itemName) {
		String sql = "select * from y_ItemDetail where ItemName = ?0";
		return commonDao.findBySql(sql, itemName);
	}

	public int cleanMachine(String commandCode, String itemName) {
		String sql = "select * from y_Exec_Commond";
		List<Map<String, Object>> list = commonDao.findBySql(sql);
		if(list.size() != 0){
			return -1;
		}
		sql =  "insert into y_Exec_Commond values(?0,?1)";
		return commonDao.updateBySql(sql,commandCode, itemName);
	}
}