package io.renren.modules.datasources.model;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 数据源实体类
 * 先暂时只支持文件
 *
 * @author fansy
 * @email fansy1990@foxmail.com
 * @date 2018-01-20 22:17:34
 */
@Entity(name = "tb_datasource")
public class DataSourceEntity implements Serializable {
    private static final long serialVersionUID = 2L;

    //
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    //数据源名称
    private String name;
    // 数据库中真实名字，对应一个真实的表
    private String realName;
    // 列描述符
    @ElementCollection(targetClass = SimpleColumn.class)
    @Lob
    private List<SimpleColumn> columnList;
    //所有者
    private String owner;
    //创建时间
    private Date createDate;
    //类型 1：文本  2：数据库
    @Range(min=1, max=2, message = "类型错误")
    private Integer type;
    // 文本
//    @NotBlank(message="文件名不能为空")
    private String filePath;
    // 分隔符
//    @NotBlank(message="分隔符不能为空")
    private String splitter;
    // 数据库
//    @NotBlank(message="Driver不能为空")
    private String driver;
    //
//    @NotBlank(message="url不能为空")
    private String url;
    //
//    @NotBlank(message="用户名不能为空")
    private String user;
    //
//    @NotBlank(message="密码不能为空")
    private String password;

//    @NotBlank(message = "查询语句不能为空")
    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public List<SimpleColumn> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<SimpleColumn> columnList) {
        this.columnList = columnList;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getSplitter() {
        return splitter;
    }

    public void setSplitter(String splitter) {
        this.splitter = splitter;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }


}
