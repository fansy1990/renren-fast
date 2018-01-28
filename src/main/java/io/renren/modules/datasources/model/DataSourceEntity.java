package io.renren.modules.datasources.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


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
	@GeneratedValue(strategy= GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	//数据源名称
	private String name;
	// 真实路径
	private String realName;
	//所有者
	private String owner;

	public DataSourceType getDataSourceType() {
		return dataSourceType;
	}

	public void setDataSourceType(DataSourceType dataSourceType) {
		this.dataSourceType = dataSourceType;
	}

	private DataSourceType dataSourceType;
	//创建时间
	private Date createDate;

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
