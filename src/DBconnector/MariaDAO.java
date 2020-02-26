package DBconnector;

import Model.Info;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class MariaDAO {
    static Logger logger = Logger.getLogger(MariaDAO.class);

    static {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            logger.info("Succesfully loading Maria driver");

        } catch (Exception e) {
            logger.error(e);
        }
    }

    public Connection getConnection() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(
                    "jdbc:mariadb://127.0.0.1:3306/table",
                    "id",
                    "pw");


        } catch (Exception e) {
            logger.warn(e);
        }
        return con;
    }

    public void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (Exception e) {
                logger.warn(e);
                throw new RuntimeException("MariaDB connection fail");
            }
        }
    }

    public void executeBatch(Statement stmt){
        System.out.println("Commit the batch");
        int [] count = null;
        try {
            count = stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Batch count: "+ count.length);
    }
    public void select(Info info, String start) {
        String selectColumns = info.getSelectColumns();
        String targetColumns = info.getTargetColumns();
        String [] array = selectColumns.split(",");
        String [] targetColumn = targetColumns.split(",");
        int arrayLength = array.length;
        int arrayMaxIndex = arrayLength - 1;
        int targetColumnLength = targetColumn.length;
        if(arrayLength != targetColumnLength) throw new RuntimeException("select 와 target 컬럼의 수가 일치해야 합니다. 다시 확인 해주세요.");

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        int batchSize = info.getBatchSize();
        int count = 0;
        String sql = info.getQuery().replace("%s",start);
        String targetTable = info.getTargetTable();

        String executeOption = info.getExecuteOption();
        try {
            con = getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            con.setAutoCommit(false);
            if (info.getType().equals("insert")) {
                while(rs.next()){
                    StringJoiner insertData = new StringJoiner("','", "'", "'");
                    for (int i=0; i < arrayLength; i++) {
                        String data = rs.getString(array[i]);
                        if(data != null && data.contains("'")) data = data.replace("'","\\'");
                        insertData.add(data);
                    }
                    if(executeOption.equals("real")){
                        stmt.addBatch(String.format("INSERT INTO %s (%s) VALUES (%s)",targetTable,targetColumns,insertData).replace("'null'","null"));
                        count++;
                        if(count % batchSize == 0){
                            executeBatch(stmt);
                            con.commit();
                        }
                    }else if(executeOption.equals("test")) System.out.println(String.format("INSERT INTO %s (%s) VALUES (%s)",targetTable,targetColumns,insertData).replace("'null'","null"));

                }
                executeBatch(stmt);
                con.commit();
            } else if(info.getType().equals("updateALL")){
                while (rs.next()) {
                    StringJoiner updateData = new StringJoiner("','", "'", "'");
                    for (int i=0; i < arrayLength; i++) {
                        String data = rs.getString(array[i]);
                        if(data != null && data.contains("'")) data = data.replace("'","\\'");
                        updateData.add(data);
                    }
                    String [] updateArray = updateData.toString().split(",");
                    StringBuilder sb = new StringBuilder();
                    sb.append(String.format("UPDATE %s\nSET\n",targetTable));

                    for(int i =1; i < targetColumnLength; i++){
                        sb.append(String.format("%s = %s",targetColumn[i],updateArray[i]));
                        if(i != arrayMaxIndex) sb.append(",\n");
                        else sb.append("\n");
                    }
                    sb.append(String.format("WHERE %s = %s\n",targetColumn[0],updateArray[0]));
                    if(executeOption.equals("real")){
                        stmt.addBatch(sb.toString().replace("'null'","null"));
                        count++;
                        if(count % batchSize == 0){
                            executeBatch(stmt);
                            con.commit();
                        }
                    }else if(executeOption.equals("test")) System.out.println(sb.toString().replace("'null'","null"));

                }
                executeBatch(stmt);
                con.commit();
            } else if (info.getType().equals("update")){
                while (rs.next()) {
                    StringJoiner updateData = new StringJoiner("','", "'", "'");
                    for (int i=0; i < arrayLength; i++) {
                        String data = rs.getString(array[i]);
                        if(data != null && data.contains("'")) data = data.replace("'","\\'");
                        updateData.add(data);
                    }
                    String [] updateArray = updateData.toString().split(",");
                    StringBuilder sb = new StringBuilder();
                    sb.append(String.format("UPDATE %s\nSET\n",targetTable));

                    for(int i =1; i < targetColumnLength; i++){
                        sb.append(String.format("%s = %s",targetColumn[i],updateArray[i]));
                        if(i != arrayMaxIndex) sb.append(",\n");
                        else sb.append("\n");
                    }
                    sb.append(String.format("WHERE %s = %s AND PMT_REQ_DT BETWEEN CAST('%s' AS DATETIME(6)) AND (CAST('%s' AS DATETIME(6)) + INTERVAL 60*60*24*1000000-1 MICROSECOND)\n",targetColumn[0],updateArray[0],start,start));
                    if(executeOption.equals("real")){
                        stmt.addBatch(sb.toString().replace("'null'","null"));
                        count++;
                        if(count % batchSize == 0){
                            executeBatch(stmt);
                            con.commit();
                        }
                    }else if(executeOption.equals("test")) System.out.println(sb.toString().replace("'null'","null"));

                }
                executeBatch(stmt);
                con.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (Exception e) {
                logger.error(e);
            }
            ;
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                logger.error(e);
            }
            ;
            try {
                if (con != null) closeConnection(con);
            } catch (Exception e) {
                logger.error(e);
            }

        }
    }

    public List<String> selectExcelInfo(Info info, String start) {
        String selectColumns = info.getSelectColumns();
        String targetColumns = info.getTargetColumns();
        String [] array = selectColumns.split(",");
        String [] targetColumn = targetColumns.split(",");
        int arrayLength = array.length;
        int arrayMaxIndex = arrayLength - 1;
        int targetColumnLength = targetColumn.length;
        if(arrayLength != targetColumnLength) throw new RuntimeException("select 와 target 컬럼의 수가 일치해야 합니다. 다시 확인 해주세요.");

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        int batchSize = info.getBatchSize();
        int count = 0;
        String sql = info.getQuery().replace("%s",start);
        String targetTable = info.getTargetTable();
        System.out.println(Runtime.getRuntime().totalMemory());
        System.out.println(Runtime.getRuntime().freeMemory());
        List<String> datas = new ArrayList<>();
        try {
            con = getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            StringJoiner excelData =null;
            while(rs.next()){
                excelData = new StringJoiner("@,", "", "");
                for (int i=0; i < arrayLength; i++) {
                    String data  = rs.getString(array[i]);
                    excelData.add(data);
                }
                datas.add(excelData.toString());
                //System.out.println(excelData.toString());
                //System.out.println(Runtime.getRuntime().freeMemory());
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (Exception e) {
                logger.error(e);
            }
            ;
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                logger.error(e);
            }
            ;
            try {
                if (con != null) closeConnection(con);
            } catch (Exception e) {
                logger.error(e);
            }

        }
        return datas;
    }

}