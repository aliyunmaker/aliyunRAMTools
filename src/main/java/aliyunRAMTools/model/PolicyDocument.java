package aliyunRAMTools.model;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * RAM的Policy
 * 
 * @author charles
 *
 */
public class PolicyDocument {

    @JSONField(name = "Version")
    private String version;

    @JSONField(name = "Statement")
    private List<Statement> statement;

    public PolicyDocument(String version, List<Statement> statement) {
        super();
        this.version = version;
        this.statement = statement;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Statement> getStatement() {
        return statement;
    }

    public void setStatement(List<Statement> statement) {
        this.statement = statement;
    }

}
