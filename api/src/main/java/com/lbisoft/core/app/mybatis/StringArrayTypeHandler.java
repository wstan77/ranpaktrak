package com.lbisoft.core.app.mybatis;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@MappedJdbcTypes(value = { JdbcType.ARRAY }, includeNullJdbcType = true)
@MappedTypes({ String[].class })
public class StringArrayTypeHandler extends BaseTypeHandler<String[]> {

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, String[] parameter, JdbcType jdbcType)
      throws SQLException {
    // JDBC type is required
    Array array = ps.getConnection().createArrayOf("TEXT", parameter);
    try {
      ps.setArray(i, array);
    } finally {
      array.free();
    }
  }

  @Override
  public String[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return extractArray(rs.getArray(columnName));
  }

  @Override
  public String[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return extractArray(rs.getArray(columnIndex));
  }

  @Override
  public String[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    return extractArray(cs.getArray(columnIndex));
  }

  protected String[] extractArray(Array array) throws SQLException {
    if (array == null) {
      return null;
    }
    Object javaArray = array.getArray();
    array.free();
    return (String[]) javaArray;
  }
}
