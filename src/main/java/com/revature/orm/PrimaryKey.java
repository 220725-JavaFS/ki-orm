package com.revature.orm;

public interface PrimaryKey<T> {
	
	T pKey();
	
	String pKeyFieldName();

}
