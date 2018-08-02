package com.itea.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 订单代码跟状态对应的字典表实体类
 * */
@Entity
@Table(name="y_status_dictionary")
public class Dictionary {

	@Id
	@Column(name="id")
	private String id;
	
	@Column(name="status")
	private String status;
	
	@Column(name="name")
	private String name;

	public Dictionary() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Dictionary(String id, String status, String name) {
		super();
		this.id = id;
		this.status = status;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
	
	
}
