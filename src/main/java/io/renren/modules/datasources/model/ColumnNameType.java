package io.renren.modules.datasources.model;

import java.io.Serializable;

/**
 * @author fanzhe
 * @email fansy1990@foxmail.com
 * @date 2018/1/28 下午10:22.
 */
public class ColumnNameType implements Serializable {
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private Integer id;
    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public String getColType() {
        return colType;
    }

    public void setColType(String colType) {
        this.colType = colType;
    }

    private String colName;
    private String colType;
}
