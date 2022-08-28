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

import com.revature.orm.utils.Conf;
import com.revature.orm.utils.ConnectionUtil;
import com.revature.orm.utils.StringUtil;

public class ObjectMapper {
	
	private Conf config;
	
	public <U, T extends PrimaryKey<U>> void insert(T model) {
		try (Connection conn = ConnectionUtil.getConnection(config)) {
			Class<?> c = model.getClass();
			String tableName = c.getSimpleName().toLowerCase() + "s";
			Field[] fields = c.getDeclaredFields();
			String questionMarks = "(" + StringUtil.sqlStringHelper(fields.length-1, "?") + ")";
			StringBuilder sql = new StringBuilder("INSERT INTO "+tableName+" (");
			for (int i=0; i<fields.length; i++) {
				if (fields[i].getName() == model.pKeyFieldName()) { continue; }
				sql.append(fields[i].getName());
				if (i < fields.length-1) {
					sql.append(", ");
				} else {
					sql.append(")");
				}
			}
			sql.append(" VALUES " + questionMarks + ";");
			PreparedStatement statement = conn.prepareStatement(new String(sql));
			
			int count = 0;
			for (Field f: fields) {
				if (f.getName() == model.pKeyFieldName()) { continue; }
				String fieldName = f.getName();
				String fieldGetterName = "get" + fieldName.substring(0,1).toUpperCase() + 
						fieldName.substring(1);
				Method getter = c.getMethod(fieldGetterName);
				Object value = getter.invoke(model);
				statement.setObject(++count, value);
				}
			statement.execute();
		} catch (NoSuchMethodException | SecurityException | InvocationTargetException | 
				IllegalAccessException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	public <U, T extends PrimaryKey<U>> List<T> selectAll(Class<T> c) {
		try (Connection conn = ConnectionUtil.getConnection(config)) {
			String tableName = c.getSimpleName().toLowerCase() + "s";
			String sql = "SELECT * FROM "+tableName+";";
			Statement statement = conn.createStatement();
			ResultSet result = statement.executeQuery(sql);
			List<T> out = new LinkedList<T>();
			
			while (result.next()) {
				T row = null;
				row = c.getDeclaredConstructor().newInstance();
				Field[] fields = c.getDeclaredFields();
				for (Field f: fields) {
					String fieldName = f.getName();
					String fieldSetterName = "set" + fieldName.substring(0,1).toUpperCase() + 
							fieldName.substring(1);
					Method setter = c.getMethod(fieldSetterName, f.getType());
					setter.invoke(row, result.getObject(fieldName));
				}
				out.add(row);
			}
			return out;
		} catch (SQLException | InstantiationException | IllegalAccessException | InvocationTargetException | 
				NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public <U, T extends PrimaryKey<U>> void delete(T model) {
		try (Connection conn = ConnectionUtil.getConnection(config)) {
			Class<?> c = model.getClass();
			String tableName = c.getSimpleName().toLowerCase() + "s";
			String sql = "DELETE FROM "+tableName+" WHERE "+model.pKeyFieldName()+" = ?;";
			PreparedStatement statement = conn.prepareStatement(sql);
			Method pKeyMethod = c.getDeclaredMethod("pKey");
			Object value = pKeyMethod.invoke(model);
			statement.setObject(1, value);
			statement.execute();
		} catch (SQLException | NoSuchMethodException | SecurityException | InvocationTargetException | 
				IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public <U, T extends PrimaryKey<U>> void update(T current, T updated) {
		try (Connection conn = ConnectionUtil.getConnection(config)) {
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
			
			int count = 0;
			for (Field f: fields) {
				String fieldName = f.getName();
				String fieldGetterName = "get" + fieldName.substring(0,1).toUpperCase() + 
						fieldName.substring(1);
				Method getter = c.getMethod(fieldGetterName);
				Object value = getter.invoke(updated);
				statement.setObject(++count, value);
			}
			
			Method pKeyMethod = c.getDeclaredMethod("pKey");
			Object pKeyValue = pKeyMethod.invoke(current);
			statement.setObject(++count, pKeyValue);
			statement.execute();
		} catch (SQLException | NoSuchMethodException | SecurityException | IllegalAccessException | 
				IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public ObjectMapper(Conf config) {
		super();
		this.config = config;
	}
}
