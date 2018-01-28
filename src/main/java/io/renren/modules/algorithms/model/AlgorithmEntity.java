package io.renren.modules.algorithms.model;

import io.renren.modules.datasources.model.DataSourceEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author fanzhe
 * @email fansy1990@foxmail.com
 * @date 2018/1/20 下午5:24.
 */
@Entity(name = "tb_algorithm_entity")
public class AlgorithmEntity implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "id")
    private Long id ;
    private String className;
    @OneToOne(fetch = FetchType.EAGER)
    private DataSourceEntity dataSourceEntity;
    @ElementCollection(targetClass = String.class)
    private Map<String,String> parameters;
    private AlgorithmStatus algorithmStatus;

    @Lob
    private String information;

    @ElementCollection(targetClass = String.class)
    private List<String> pictures;// 图片信息

    @Column(name = "algorithm_name")
    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    private String algorithmName;

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    public DataSourceEntity getDataSourceEntity() {
        return dataSourceEntity;
    }

    public void setDataSourceEntity(DataSourceEntity dataSourceEntity) {
        this.dataSourceEntity = dataSourceEntity;
    }

    public AlgorithmStatus getAlgorithmStatus() {
        return algorithmStatus;
    }

    public void setAlgorithmStatus(AlgorithmStatus algorithmStatus) {
        this.algorithmStatus = algorithmStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }


    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
