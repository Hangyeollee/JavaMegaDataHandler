package Model;

public class Info {
    private String query;
    private String type;
    private String targetColumns;
    private String targetTable;
    private String selectColumns;
    private int batchSize;
    private String executeOption;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTargetColumns() {
        return targetColumns;
    }

    public void setTargetColumns(String targetColumns) {
        this.targetColumns = targetColumns;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    public String getSelectColumns() {
        return selectColumns;
    }

    public void setSelectColumns(String selectColumns) {
        this.selectColumns = selectColumns;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public String getExecuteOption() {
        return executeOption;
    }

    public void setExecuteOption(String executeOption) {
        this.executeOption = executeOption;
    }
}
