package com.mysnake.app.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MyDaoImpl extends JdbcDaoSupport {
    public boolean isTableExist(String name) {
        try {
            DatabaseMetaData dbm = this.getConnection().getMetaData();
            ResultSet tables = dbm.getTables(null, null, name, null);

            while(tables.next()) {
                String tName = tables.getString("TABLE_NAME");
                if(tName != null && tName.equalsIgnoreCase(name))
                    return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void createTable(String name) {
        if (this.isTableExist(name))
            return;
        else {
            String sql = "CREATE TABLE " + name
                    + " (ID INTEGER GENERATED ALWAYS AS IDENTITY (START WITH  1, INCREMENT BY 1), " +
                    "SCORE INT NOT NULL, PRIMARY KEY(ID))";
            this.getJdbcTemplate().execute(sql);
        }
    }

    public boolean addNewRecordTo(String name, int score) {
        String sql = "INSERT INTO " + name + "(SCORE) VALUES (?)";

        int rowsAffected = this.getJdbcTemplate().update(sql, score);

        return (rowsAffected > 0);
    }

    public List<String> getScores(String name, int rowsnum) {
        String sql = "SELECT SCORE FROM "
                + name + " ORDER BY SCORE DESC FETCH NEXT ? ROWS ONLY";

        return this.getJdbcTemplate().query(sql, new Object[]{rowsnum},(resultSet, i) -> resultSet.getString("SCORE"));
    }
}
