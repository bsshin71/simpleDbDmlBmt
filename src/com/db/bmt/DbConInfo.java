package com.db.bmt;

import java.util.List;

public class DbConInfo {

	public String        url ;
	public String        driver;
	public String        user  ;
	public String        passwd;
	public String        dbproduct;
	public List<String> initquery ;
	public List<String> insertparams ;
	public String        insertquery;
	public List<String> selectparams ;
	public String        selectquery;
	public List<String> updateparams ;
	public String        updatequery;
	public List<String> deleteparams ;
	public String        deletequery;
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		url = url;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public String getDbproduct() {
		return dbproduct;
	}
	public void setDbproduct(String dbproduct) {
		this.dbproduct = dbproduct;
	}
	public List<String> getInitquery() {
		return initquery;
	}
	public void setInitquery(List<String> initquery) {
		this.initquery = initquery;
	}
	public List<String> getInsertparams() {
		return insertparams;
	}
	public void setInsertparams(List<String> insertparams) {
		this.insertparams = insertparams;
	}
	public String getInsertquery() {
		return insertquery;
	}
	public void setInsertquery(String insertquery) {
		this.insertquery = insertquery;
	}
	public List<String> getSelectparams() {
		return selectparams;
	}
	public void setSelectparams(List<String> selectparams) {
		this.selectparams = selectparams;
	}
	public String getSelectquery() {
		return selectquery;
	}
	public void setSelectquery(String selectquery) {
		this.selectquery = selectquery;
	}
	public List<String> getUpdateparams() {
		return updateparams;
	}
	public void setUpdateparams(List<String> updateparams) {
		this.updateparams = updateparams;
	}
	public String getUpdatequery() {
		return updatequery;
	}
	public void setUpdatequery(String updatequery) {
		this.updatequery = updatequery;
	}
	public List<String> getDeleteparams() {
		return deleteparams;
	}
	public void setDeleteparams(List<String> deleteparams) {
		this.deleteparams = deleteparams;
	}
	public String getDeletequery() {
		return deletequery;
	}
	public void setDeletequery(String deletequery) {
		this.deletequery = deletequery;
	}
	
}
