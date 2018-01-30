package io.renren.modules.datasources.model;

import java.io.Serializable;

/**
 * @author fanzhe
 * @email fansy1990@foxmail.com
 * @date 2018/1/28 下午10:22.
 */
public class SimpleColumn implements Serializable {
    private Integer id;
    private String colName;
    private ColumnType colType;

    public ColumnType getColType() {
        return colType;
    }

    public void setColType(ColumnType colType) {
        this.colType = colType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }


    @Override
    public String toString() {
        return "[ "+id+" , "+colName+" , "+colType.name()+" ]";
    }
}
