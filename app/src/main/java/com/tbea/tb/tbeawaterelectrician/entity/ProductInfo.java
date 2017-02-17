package com.tbea.tb.tbeawaterelectrician.entity;

/**
 * 购物车
 */
public class ProductInfo
{
	protected String orderdetailid;
	protected String commodityid;
	protected String commodityname;
	protected String commoditypicture;
	protected String ordercolorid;
	protected String ordercolor;
	protected String orderspecificationid;
	protected String orderspecification;
	protected String ordertime;
	protected boolean isChoosed;
	private String desc;
	private float orderprice;
	private int ordernumber;
	private int position;// 绝对位置，只在ListView构造的购物车中，在删除时有效

	public String getCommodityid() {
		return commodityid;
	}

	public void setCommodityid(String commodityid) {
		this.commodityid = commodityid;
	}

	public String getCommodityname() {
		return commodityname;
	}

	public void setCommodityname(String commodityname) {
		this.commodityname = commodityname;
	}

	public String getCommoditypicture() {
		return commoditypicture;
	}

	public void setCommoditypicture(String commoditypicture) {
		this.commoditypicture = commoditypicture;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public boolean isChoosed() {
		return isChoosed;
	}

	public void setChoosed(boolean choosed) {
		isChoosed = choosed;
	}

	public String getOrdercolorid() {
		return ordercolorid;
	}

	public void setOrdercolorid(String ordercolorid) {
		this.ordercolorid = ordercolorid;
	}

	public String getOrdercolor() {
		return ordercolor;
	}

	public void setOrdercolor(String ordercolor) {
		this.ordercolor = ordercolor;
	}

	public String getOrderdetailid() {
		return orderdetailid;
	}

	public void setOrderdetailid(String orderdetailid) {
		this.orderdetailid = orderdetailid;
	}

	public float getOrderprice() {
		return orderprice;
	}

	public void setOrderprice(float orderprice) {
		this.orderprice = orderprice;
	}

	public int getOrdernumber() {
		return ordernumber;
	}

	public void setOrdernumber(int ordernumber) {
		this.ordernumber = ordernumber;
	}

	public String getOrderspecification() {
		return orderspecification;
	}

	public void setOrderspecification(String orderspecification) {
		this.orderspecification = orderspecification;
	}

	public String getOrderspecificationid() {
		return orderspecificationid;
	}

	public void setOrderspecificationid(String orderspecificationid) {
		this.orderspecificationid = orderspecificationid;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getOrdertime() {
		return ordertime;
	}

	public void setOrdertime(String ordertime) {
		this.ordertime = ordertime;
	}
}
