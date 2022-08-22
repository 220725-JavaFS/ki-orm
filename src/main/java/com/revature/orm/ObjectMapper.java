package com.revature.orm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import com.revature.utils.ConnectionUtil;
import com.revature.utils.StringUtil;

public class ObjectMapper {
	
	public <T> void insert(T model) {
		try (Connection conn = ConnectionUtil.getConnection()) {
			Class<?> c = model.getClass();
			String tableName = c.getSimpleName().toLowerCase() + "s";
			Field[] fields = c.getDeclaredFields();
			String questionMarks = "(" + StringUtil.sqlStringHelper(fields.length, "?") + ")";
			StringBuilder sql = new StringBuilder("INSERT INTO "+tableName+" (");
			for (int i=0; i<fields.length; i++) {
				sql.append(fields[i].getName());
				if (i < fields.length-1) {
					sql.append(", ");
				} else {
					sql.append(")");
				}
			}
			sql.append("VALUES " + questionMarks + ";");
			PreparedStatement statement = conn.prepareStatement(new String(sql));
			
			int count = 0;
			for (Field f: fields) {
				String fieldName = f.getName();
				String fieldGetterName = "get" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
				try {
					Method getter = c.getMethod(fieldGetterName);
					Object value = getter.invoke(model);
					statement.setString(++count, fieldName);
					statement.setObject(count+fields.length, value);
				} catch (NoSuchMethodException | SecurityException | InvocationTargetException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public <T> List<T> selectAll(Class<T> c) {
		try (Connection conn = ConnectionUtil.getConnection()) {
			String tableName = c.getSimpleName().toLowerCase() + "s";
			String sql = "SELECT * FROM "+tableName+";";
			Statement statement = conn.createStatement();
			ResultSet result = statement.executeQuery(sql);
			List<T> out = new LinkedList<T>();
			
			while (result.next()) {
				T row = null;
				try {
					row = c.getDeclaredConstructor().newInstance();
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					e.printStackTrace();
				}
				Field[] fields = c.getDeclaredFields();
				for (Field f: fields) {
					String fieldName = f.getName();
					String fieldSetterName = "set" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
					try {
						Method setter = c.getMethod(fieldSetterName, f.getType());
						setter.invoke(row, result.getObject(fieldName));//, f.getType()));
					} catch (NoSuchMethodException | SecurityException | InvocationTargetException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				out.add(row);
			}
			return out;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public <U, T extends PrimaryKey<U>> void delete(T model) {
		try (Connection conn = ConnectionUtil.getConnection()) {
			Class<?> c = model.getClass();
			String tableName = c.getSimpleName().toLowerCase() + "s";
			String sql = "DELETE FROM "+tableName+" WHERE "+model.pKeyFieldName()+" = ?;";
			PreparedStatement statement = conn.prepareStatement(sql);
			try {
				Method pKeyMethod = c.getDeclaredMethod("pKey");
				Object value = pKeyMethod.invoke(model);
				statement.setObject(1, value);
			} catch (NoSuchMethodException | SecurityException | InvocationTargetException | IllegalAccessException e) {
				e.printStackTrace();
			}
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public <U, T extends PrimaryKey<U>> void update(T current, T updated) {
		try (Connection conn = ConnectionUtil.getConnection()) {
			Class<?> c = current.getClass();
			String tableName = c.getSimpleName().toLowerCase() + "s";
			Field[] fields = c.getDeclaredFields();
			StringBuilder sql = new StringBuilder("UPDATE "+tableName+" SET ");
			for (int i=0; i<fields.length; i++) {
				String fieldName = fields[i].getName();
				sql.append(fieldName+" = ?");
				if (i < fields.length-1) sql.append(", ");
			}
			sql.append(" WHERE "+current.pKeyFieldName()+" = ?;");
			PreparedStatement statement = conn.prepareStatement(new String(sql));
			System.out.println(statement);
			
			int count = 0;
			for (Field f: fields) {
				String fieldName = f.getName();
				String fieldGetterName = "get" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
				try {
					Method getter = c.getMethod(fieldGetterName);
					Object value = getter.invoke(updated);
					statement.setObject(++count, value);
				} catch (NoSuchMethodException | SecurityException | InvocationTargetException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			try {
				Method pKeyMethod = c.getDeclaredMethod("pKey");
				Object pKeyValue = pKeyMethod.invoke(current);
				statement.setObject(++count, pKeyValue);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			System.out.println(statement);
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
