package com.itea.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.itea.bean.Dictionary;
import com.itea.bean.Results;
import com.itea.server.SocketServer;
import com.itea.service.IteaService;
import com.itea.util.Utils;

/**
 * 负责查询并向前台发送信息推送的类 query and send message to front-page
 * 
 * @author Eternal.Y
 * @Date 2018/08/02
 */

@Controller
@RequestMapping("/work")
public class WorkController {

	private static final Logger log = Logger.getLogger(WorkController.class);

	@Resource
	private IteaService is;
	
	private int count = 0;

	/**
	 * 循环查询
	 */
	@RequestMapping("/queryBegin")
	protected void queryBegin(HttpServletRequest request, HttpServletResponse response) {
		while (true) {
			try {
				Map<String, Object> result = this.queryMessage(request, response);
				if(result != null){
					SocketServer.send_message(JSON.toJSONString(result));
				}else{
					if(count >= 5 && count < 10)
						SocketServer.send_message("from server");
					if(count >=10){
						result = new HashMap<>();
						result.put("orders", this.orders);
						result.put("inventory", this.invent);
						result.put("status", this.status);
						count =0;
						SocketServer.send_message(JSON.toJSONString(result));
					}
					count++;
				}
				Thread.sleep(2 * 1000); // 设置暂停的时间 2秒]
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping("/changeErrorOrMiss")
	@ResponseBody
	protected Results changeStatusError(HttpServletRequest request, HttpServletResponse response) {
		String orderId = request.getParameter("orderId");
		String status = request.getParameter("status");
		String nextId = request.getParameter("nextId");
		String nextStatus = request.getParameter("nextStatus");
		Results result = new Results();
		log.info("edit order" + orderId + " status to '" + status+ "'");
		int i = is.setStatusByOrderId(orderId, status);
		if (i != 1) {
			result.setSuc(false);
			return result;
		}
		if (Utils.isNotEmpty(nextId) && Utils.isNotEmpty(nextStatus)) {
			i = is.setStatusByOrderId(nextId, nextStatus);
			if (i != 1) {
				result.setSuc(false);
				return result;
			}
		}
		result.setSuc(true);
		return result;
	}

	@RequestMapping("/changeStatus")
	@ResponseBody
	protected Results changeCalling2Over(@RequestParam("orderId") String orderId,
			@RequestParam("nextId") String nextId) {
		
		Results result = new Results();
		if(Utils.isNotEmpty(orderId)){
			log.info("edit order" + orderId + "to end");
			is.setStatusByOrderId(orderId, "4");	
		}
		log.info("edit order" + nextId + "to Calling");
		is.setStatusByOrderId(nextId, "3");
		result.setSuc(true);
		return result;
	}

	protected List<Map<String, Object>> queryError(String errorcode) {
		log.info("query errorcode");
		Map<String, String> map = new HashMap<>();
		map.put("errorcode", errorcode);
		return is.queryError(errorcode);
	}

	@RequestMapping("/changePrepared")
	@ResponseBody
	protected Results changePrepared(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("itemName") String itemName, @RequestParam("itemPrepared") String itemPrepared) {
		int i = is.changePrepared(itemName, itemPrepared);
		Results result = new Results();
		if (i == 1) {
			result.setSuc(true);
			result.setResult(is.queryPrepared(itemName));
			return result;
		}
		result.setSuc(false);
		return result;
	}
	
	@RequestMapping("/queryAllInventory")
	@ResponseBody
	protected Results queryAllInventory() {
		log.info("queryAllInventory is begin");
		List<Map<String, Object>> list = is.queryAllInventory();
		for (Map<String, Object> map : list)
			map.put("percentage", Utils.paseDouble(Double.parseDouble(map.get("percentage").toString())));
		Results rb = new Results();
		rb.setSuc(true);
		rb.setResult(list);
		log.info("queryAllInventory is end");
		return rb;
	}

	@RequestMapping("/cleanMachine")
	@ResponseBody
	protected Results cleanMachine(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("itemName") String itemName, @RequestParam("commandCode") String commandCode) {
		if (Utils.isEmpty(commandCode) || Utils.isNotEmpty(commandCode.replaceAll("0-9", "")))
			try {
				throw new Exception("CommandCode is error!");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		int i = is.cleanMachine(commandCode, itemName);
		Results result = new Results();
		if (i == 1) {
			result.setSuc(true);
			result.setResult("指令已接收,执行成功(command  acceptance,success)");
			return result;
		}
		if (i == -1) {
			result.setSuc(false);
			result.setResult("当前已有待处理任务，请重试(now has command list,please try again later)");
			return result;
		}
		result.setSuc(false);
		result.setResult("未知错误，请重试(Unkown Error,please try again later");
		return result;
	}

	private Map<String, Object> queryMessage(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> result = new HashMap<>();
		Map<String, List<Map<String, Object>>> orders = this.queryOrders(request, response);
		List<Map<String, Object>> invent = this.queryLessInventory();
		List<Map<String, Object>> stat = this.queryWorkStatus();
		if (orders == null && invent == null && stat == null)
			return null;
		result.put("orders", orders);
		result.put("inventory", invent);
		result.put("status", stat);
		return result;
	}

	private Map<String, List<Map<String, Object>>> orders = null;

	protected Map<String, List<Map<String, Object>>> queryOrders(HttpServletRequest request, HttpServletResponse response) {

		log.info("getOrders is beginning");
		Map<String, List<Map<String, Object>>> map = new HashMap<>();
		// get dictionary
		List<Dictionary> dictionary = is.queryDicts();
		for (Dictionary dic : dictionary) {
			List<Map<String, Object>> result = is.queryOrderByStatus(dic.getStatus());
			List<Map<String, Object>> list = new ArrayList<>();
			for (Map<String, Object> order : result) {
				if (Integer.parseInt(order.get("orderTotal").toString()) == Integer
						.parseInt(order.get("total").toString()))
					list.add(order);
			}
			map.put(dic.getName(), list);
		}
		if (map.equals(orders))
			return null;
		this.orders = map;
		log.info("getOrders was ended");
		return map;
	}

	private List<Map<String, Object>> invent = null;

	protected List<Map<String, Object>> queryLessInventory() {
		log.info("queryLessInventory is beginning");
		List<Map<String, Object>> list = is.queryLessInventory();
		for (Map<String, Object> map : list) {
			map.put("percentage", Utils.paseDouble(Double.parseDouble(map.get("percentage").toString())));
		}

		if (list.equals(invent))
			return null;
		invent = list;
		log.info("queryLessInventory is ended");
		return list;
	}

	private List<Map<String, Object>> status = null;

	protected List<Map<String, Object>> queryWorkStatus() {
		log.info("query work status is begin");
		List<Map<String, Object>> result = is.queryWorkStatus();
		Iterator<Map<String, Object>> it = result.iterator();
		while (it.hasNext()) {
			Map<String, Object> map = it.next();
			if (map.get("WorkingDesc").toString().startsWith("ERROR")) {
				List<Map<String, Object>> list = this.queryError(map.get("WorkingDesc").toString());
				map.put("isError", true);
				map.put("ErrorDesc", list.get(0).get("ErrorDesc"));
			}
		}
		if (result.equals(status))
			return null;
		this.status = result;
		log.info("query work status is end");
		return result;
	}

}
