package jtools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class Database
{
  private final String source;
  private Connection connection;

  public Database(String src) {
    this.source = src;
    this.connection = null;
  }

  public static class Record extends HashMap<String, Object> {
  }

  public static class Recordset extends ArrayList<Record> {
  }

  public static class Cursor {
    private final ResultSet resultSet;
    private final PreparedStatement statement;

    public Cursor(ResultSet rs, PreparedStatement ps) {
      this.resultSet = rs;
      this.statement = ps;
    }

    public boolean next() throws Exception {
      return this.resultSet.next();
    }

    public Object get(String column) throws Exception {
      return this.resultSet.getObject(column);
    }

    public Record getRow() throws Exception {
      Record row = new Record();
      ResultSetMetaData meta = this.resultSet.getMetaData();
      int columnCount = meta.getColumnCount();

      for (int i = 1; i <= columnCount; i++) {
        row.put(meta.getColumnName(i), this.resultSet.getObject(i));
      }
      return row;
    }

    public void close() {
      try {
        if (this.resultSet != null) this.resultSet.close();
        if (this.statement != null) this.statement.close();
      } catch (SQLException e) {}
    }
  }

  public void open() throws Exception
  {
    Context ctx = new InitialContext();
    DataSource ds = (DataSource) ctx.lookup(this.source);
    this.connection = ds.getConnection();
  }

  public void close()
  {
    if (this.connection != null) {
      try { this.connection.close(); } catch (SQLException e) {}
      this.connection = null;
    }
  }

  public boolean connected()
  {
    try {
      return this.connection != null && !this.connection.isClosed();
    } catch (SQLException e) {
      return false;
    }
  }

  public void begin() throws Exception
  {
    this.connection.setAutoCommit(false);
  }

  public void commit() throws Exception
  {
    this.connection.commit();
    this.connection.setAutoCommit(true);
  }

  public void rollback() throws Exception
  {
    this.connection.rollback();
    this.connection.setAutoCommit(true);
  }

  public int query(String sql, Object... params)
      throws Exception
  {
    if (this.connection == null || this.connection.isClosed()) {
      throw new Exception("Connection not available");
    }
    if (sql == null || sql.trim().isEmpty()) {
      throw new Exception("Invalid SQL");
    }

    PreparedStatement ps = this.connection.prepareStatement(sql);
    for (int i = 0; i < params.length; i++) {
      ps.setObject(i + 1, params[i]);
    }
    int result = ps.executeUpdate();
    ps.close();
    return result;
  }

  public Recordset select(String sql, Object... params)
      throws Exception
  {
    if (this.connection == null || this.connection.isClosed()) {
      throw new Exception("Connection not available");
    }
    if (sql == null || sql.trim().isEmpty()) {
      throw new Exception("Invalid SQL");
    }

    PreparedStatement ps = this.connection.prepareStatement(sql);
    for (int i = 0; i < params.length; i++) {
      ps.setObject(i + 1, params[i]);
    }
    ResultSet rs = ps.executeQuery();

    Recordset result = new Recordset();
    ResultSetMetaData meta = rs.getMetaData();
    int columnCount = meta.getColumnCount();

    while (rs.next()) {
      Record row = new Record();
      for (int i = 1; i <= columnCount; i++) {
        row.put(meta.getColumnName(i), rs.getObject(i));
      }
      result.add(row);
    }

    rs.close();
    ps.close();
    return result;
  }

  public Cursor cursor(String sql, Object... params) throws Exception {
    if (this.connection == null || this.connection.isClosed()) {
      throw new Exception("Connection not available");
    }
    if (sql == null || sql.trim().isEmpty()) {
      throw new Exception("Invalid SQL");
    }

    PreparedStatement ps = this.connection.prepareStatement(sql);
    for (int i = 0; i < params.length; i++) {
      ps.setObject(i + 1, params[i]);
    }
    ResultSet rs = ps.executeQuery();
    return new Cursor(rs, ps);
  }

  public long lastInsertId()
      throws Exception
  {
    String dbProduct = this.connection
      .getMetaData()
      .getDatabaseProductName()
      .toLowerCase();

    String query;

    if (dbProduct.contains("mysql")) {
      query = "SELECT LAST_INSERT_ID()";
    } else if (dbProduct.contains("postgresql")) {
      query = "SELECT LASTVAL()";
    } else if (dbProduct.contains("sqlite")) {
      query = "SELECT last_insert_rowid()";
    } else if (dbProduct.contains("sql server")) {
      query = "SELECT @@IDENTITY";
    } else if (dbProduct.contains("oracle")) {
      query = "SELECT SEQ.CURRVAL FROM DUAL";
    } else {
      throw new Exception("Unsupported database: " + dbProduct);
    }

    PreparedStatement ps = this.connection.prepareStatement(query);
    ResultSet rs = ps.executeQuery();
    long id = 0;
    if (rs.next()) {
      id = rs.getLong(1);
    }
    rs.close();
    ps.close();
    return id;
  }
}
