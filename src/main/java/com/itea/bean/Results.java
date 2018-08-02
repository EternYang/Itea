package com.itea.bean;

public class Results {

	private boolean iSuc;
	private Object result;
	public Results() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
	public Results(boolean iSuc, Object result) {
		super();
		this.iSuc = iSuc;
		this.result = result;
	}



	public boolean getSuc() {
		return iSuc;
	}
	public void setSuc(boolean iSuc) {
		this.iSuc = iSuc;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	
	
	
}
