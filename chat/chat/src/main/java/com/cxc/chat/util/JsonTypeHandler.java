package com.cxc.chat.util;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

public class JsonTypeHandler<T> extends BaseTypeHandler<T> {

	private static final ObjectMapper objectMapper = initObjectMapper();
	private Class<T> clazz;
	public JsonTypeHandler(Class<T> clazz) {
		super();
		if (clazz == null) throw new IllegalArgumentException("Type argument cannot be null");
		this.clazz = clazz;
	}
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, this.toJson(parameter));
	}
	@Override
	public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return this.toObject(rs.getString(columnName), clazz);
	}

	@Override
	public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return this.toObject(rs.getString(columnIndex), clazz);
	}

	@Override
	public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return this.toObject(cs.getString(columnIndex), clazz);
	}
	
    private String toJson(T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @SuppressWarnings("unchecked")
	private T toObject(String content, Class<?> clazz) {
        if (content != null && !content.isEmpty()) {
            try {
                return (T) objectMapper.readValue(content, clazz);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }
	private static ObjectMapper initObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		return objectMapper;
	}
}
