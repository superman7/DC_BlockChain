package com.digitalchina.xa.it.util;


import java.io.Serializable;

/**
 * @author Administrator
 *
 */
public class ResultUtil implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer status;
	private String msg;
	private Object data;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public ResultUtil() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ResultUtil(Integer status, String msg, Object data) {
		super();
		this.status = status;
		this.msg = msg;
		this.data = data;
	}

}
