package com.guobo.paycarmanage.beans;

public class YunDanItem {

	public int id;
	public String name;
	public String phonenumber;
	public String number;
	public String imgpath;
	public String succinfo;
	public String failinfo;
	public String payTime;
	public String createTime;
	public int issucc;
	public int isdel;
	public int isUpload;
	
	
	
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhonenumber() {
		return phonenumber;
	}
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	public int getIsUpload() {
		return isUpload;
	}
	public void setIsUpload(int isUpload) {
		this.isUpload = isUpload;
	}
	public int getIssucc() {
		return issucc;
	}
	public void setIssucc(int issucc) {
		this.issucc = issucc;
	}
	public String getPayTime() {
		return payTime;
	}
	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getImgpath() {
		return imgpath;
	}
	public void setImgpath(String imgpath) {
		this.imgpath = imgpath;
	}
	public String getSuccinfo() {
		return succinfo;
	}
	public void setSuccinfo(String succinfo) {
		this.succinfo = succinfo;
	}
	public String getFailinfo() {
		return failinfo;
	}
	public void setFailinfo(String failinfo) {
		this.failinfo = failinfo;
	}
	public int getIsdel() {
		return isdel;
	}
	public void setIsdel(int isdel) {
		this.isdel = isdel;
	}
	
	
	
}
