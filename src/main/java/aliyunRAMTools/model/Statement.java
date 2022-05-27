package aliyunRAMTools.model;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

public class Statement {

    @JSONField(name = "Action")
    private List<String> action;

    @JSONField(name = "Effect")
    private StatementEffect effect;

    @JSONField(name = "Resource")
    private List<String> resource;

    @JSONField(name = "Condition")
    private JSONObject condition;

    public List<String> getAction() {
        return action;
    }

    public void setAction(List<String> action) {
        this.action = action;
    }

    public StatementEffect getEffect() {
        return effect;
    }

    public void setEffect(StatementEffect effect) {
        this.effect = effect;
    }

    public List<String> getResource() {
        return resource;
    }

    public void setResource(List<String> resource) {
        this.resource = resource;
    }

    public JSONObject getCondition() {
        return condition;
    }

    public void setCondition(JSONObject condition) {
        this.condition = condition;
    }

}
