package io.renren.modules.datasources.type;

import io.renren.common.validator.group.AliyunGroup;
import io.renren.common.validator.group.QcloudGroup;
import io.renren.common.validator.group.QiniuGroup;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 数据源配置信息
 * @author fansy
 * @date 2017-03-25 16:12
 */
public class DataSourceTypeConfig implements Serializable {
    private static final long serialVersionUID = 56L;

    //类型 1：文本  2：数据库
    @Range(min=1, max=2, message = "类型错误")
    private Integer type;
    // 文本
    @NotBlank(message="文件名不能为空")
    private String filePath;
    // 分隔符
    @NotBlank(message="分隔符不能为空")
    private String splitter;
    // 数据库
    @NotBlank(message="Driver不能为空")
    private String driver;
    //
    @NotBlank(message="url不能为空")
    private String url;
    //
    @NotBlank(message="用户名不能为空")
    private String user;
    //
    @NotBlank(message="密码不能为空")
    private String password;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }


}
