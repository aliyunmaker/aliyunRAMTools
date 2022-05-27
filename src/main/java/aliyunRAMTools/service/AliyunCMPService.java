package aliyunRAMTools.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.ram.model.v20150501.AttachPolicyToUserRequest;
import com.aliyuncs.ram.model.v20150501.CreatePolicyRequest;
import com.aliyuncs.ram.model.v20150501.CreatePolicyResponse;
import com.aliyuncs.ram.model.v20150501.DeletePolicyRequest;
import com.aliyuncs.ram.model.v20150501.DeletePolicyResponse;
import com.aliyuncs.ram.model.v20150501.DetachPolicyFromGroupRequest;
import com.aliyuncs.ram.model.v20150501.DetachPolicyFromRoleRequest;
import com.aliyuncs.ram.model.v20150501.DetachPolicyFromUserRequest;
import com.aliyuncs.ram.model.v20150501.DetachPolicyFromUserResponse;
import com.aliyuncs.ram.model.v20150501.GetPolicyVersionRequest;
import com.aliyuncs.ram.model.v20150501.GetPolicyVersionResponse;
import com.aliyuncs.ram.model.v20150501.ListEntitiesForPolicyRequest;
import com.aliyuncs.ram.model.v20150501.ListEntitiesForPolicyResponse;
import com.aliyuncs.ram.model.v20150501.ListPoliciesForUserRequest;
import com.aliyuncs.ram.model.v20150501.ListPoliciesForUserResponse;
import com.aliyuncs.ram.model.v20150501.ListPoliciesForUserResponse.Policy;
import com.aliyuncs.ram.model.v20150501.ListPoliciesRequest;
import com.aliyuncs.ram.model.v20150501.ListPoliciesResponse;
import com.aliyuncs.ram.model.v20150501.ListUsersRequest;
import com.aliyuncs.ram.model.v20150501.ListUsersResponse;
import com.aliyuncs.ram.model.v20150501.ListUsersResponse.User;

import aliyunRAMTools.common.CommonConstants;
import aliyunRAMTools.model.PolicyDocument;
import aliyunRAMTools.model.Statement;
import aliyunRAMTools.utils.AliyunAPIUtils;
import aliyunRAMTools.utils.JsonUtils;
import aliyunRAMTools.utils.UUIDUtils;

public class AliyunCMPService {

    private static Logger logger = LoggerFactory.getLogger(AliyunCMPService.class);

    public static final int MaxUserItems = 100;

    public static final int MaxPolicyItems = 1000;

    public static final int MaxPolicyLength = 4000;

    private static final IAcsClient CLIENT_HANGZHOU = AliyunAPIUtils.buildClient(CommonConstants.Aliyun_AccessKeyId,
        CommonConstants.Aliyun_AccessKeySecret, CommonConstants.Aliyun_REGION_HANGZHOU);

    public static List<User> getAllRAMUser() throws Exception {
        List<User> result = new ArrayList<>();
        ListUsersRequest request = new ListUsersRequest();
        request.setMaxItems(MaxUserItems);
        while (true) {
            ListUsersResponse response = CLIENT_HANGZHOU.getAcsResponse(request);
            List<User> userList = response.getUsers();
            String marker = response.getMarker();
            request.setMarker(marker);
            result.addAll(userList);
            if (!response.getIsTruncated()) {
                break;
            }
        }
        return result;
    }

    public static List<Policy> listPoliciesForUser(String userName) throws Exception {
        Assert.hasText(userName, "userName can not be blank!");
        ListPoliciesForUserRequest listPoliciesForUserRequest = new ListPoliciesForUserRequest();
        listPoliciesForUserRequest.setUserName(userName);
        ListPoliciesForUserResponse listPoliciesForUserResponse =
            CLIENT_HANGZHOU.getAcsResponse(listPoliciesForUserRequest);
        return listPoliciesForUserResponse.getPolicies();
    }

    public static com.aliyuncs.ram.model.v20150501.CreatePolicyResponse.Policy createPolicy(String policyName,
        String policyDocument) throws Exception {
        Assert.hasText(policyName, "policyName can not be blank!");
        Assert.hasText(policyDocument, "policyDocument can not be blank!");
        CreatePolicyRequest request = new CreatePolicyRequest();
        request.setPolicyName(policyName);
        request.setPolicyDocument(policyDocument);
        CreatePolicyResponse response = CLIENT_HANGZHOU.getAcsResponse(request);
        return response.getPolicy();
    }

    public static void attachPolicyToUser(String policyType, String policyName, String userName) throws Exception {
        Assert.hasText(policyType, "policyType can not be blank!");
        Assert.hasText(policyName, "policyName can not be blank!");
        Assert.hasText(userName, "userName can not be blank!");
        AttachPolicyToUserRequest request = new AttachPolicyToUserRequest();
        request.setPolicyName(policyName);
        request.setPolicyType(policyType);
        request.setUserName(userName);
        CLIENT_HANGZHOU.getAcsResponse(request);
    }

    public static String getPolicy(String policyType, String policyName, String versionId) throws Exception {
        Assert.hasText(policyType, "policyType can not be blank!");
        Assert.hasText(policyName, "policyName can not be blank!");
        Assert.hasText(policyName, "policyName can not be blank!");
        GetPolicyVersionRequest request = new GetPolicyVersionRequest();
        request.setPolicyType(policyType);
        request.setPolicyName(policyName);
        request.setVersionId(versionId);
        GetPolicyVersionResponse response = CLIENT_HANGZHOU.getAcsResponse(request);
        return response.getPolicyVersion().getPolicyDocument();
    }

    /**
     * 解绑policy
     * 
     * @param policyType
     * @param policyName
     * @param userName
     * @return RequestId
     * @throws Exception
     */

    public static String detachPolicyFromUser(String policyType, String policyName, String userName) throws Exception {
        Assert.hasText(policyType, "policyType can not be blank!");
        Assert.hasText(policyName, "policyName can not be blank!");
        Assert.hasText(userName, "userName can not be blank!");
        DetachPolicyFromUserRequest request = new DetachPolicyFromUserRequest();
        request.setPolicyType(policyType);
        request.setPolicyName(policyName);
        request.setUserName(userName);
        DetachPolicyFromUserResponse response = CLIENT_HANGZHOU.getAcsResponse(request);
        return response.getRequestId();
    }

    public static String detachPolicy(String policyType, String policyName) throws Exception {
        Assert.hasText(policyType, "policyType can not be blank!");
        Assert.hasText(policyName, "policyName can not be blank!");
        ListEntitiesForPolicyRequest request = new ListEntitiesForPolicyRequest();
        request.setPolicyType(policyType);
        request.setPolicyName(policyName);
        ListEntitiesForPolicyResponse response = CLIENT_HANGZHOU.getAcsResponse(request);
        List<com.aliyuncs.ram.model.v20150501.ListEntitiesForPolicyResponse.Group> groupList = response.getGroups();
        if (groupList != null) {
            for (com.aliyuncs.ram.model.v20150501.ListEntitiesForPolicyResponse.Group group : groupList) {
                DetachPolicyFromGroupRequest groupRequest = new DetachPolicyFromGroupRequest();
                groupRequest.setGroupName(group.getGroupName());
                groupRequest.setPolicyType(policyType);
                groupRequest.setPolicyName(policyName);
                CLIENT_HANGZHOU.getAcsResponse(groupRequest);
                logger.info("detach policy[{}] from userGroup:[{}]", policyName, group.getGroupName());
            }
        }
        List<com.aliyuncs.ram.model.v20150501.ListEntitiesForPolicyResponse.User> userList = response.getUsers();
        if (userList != null) {
            for (com.aliyuncs.ram.model.v20150501.ListEntitiesForPolicyResponse.User user : userList) {
                DetachPolicyFromUserRequest userRequest = new DetachPolicyFromUserRequest();
                userRequest.setUserName(user.getUserName());
                userRequest.setPolicyType(policyType);
                userRequest.setPolicyName(policyName);
                CLIENT_HANGZHOU.getAcsResponse(userRequest);
                logger.info("detach policy[{}] from user:[{}]", policyName, user.getUserName());
            }
        }
        List<com.aliyuncs.ram.model.v20150501.ListEntitiesForPolicyResponse.Role> roleList = response.getRoles();
        if (userList != null) {
            for (com.aliyuncs.ram.model.v20150501.ListEntitiesForPolicyResponse.Role role : roleList) {
                DetachPolicyFromRoleRequest roleRequest = new DetachPolicyFromRoleRequest();
                roleRequest.setRoleName(role.getRoleName());
                roleRequest.setPolicyType(policyType);
                roleRequest.setPolicyName(policyName);
                CLIENT_HANGZHOU.getAcsResponse(roleRequest);
                logger.info("detach policy[{}] from role:[{}]", policyName, role.getRoleName());
            }
        }
        return response.getRequestId();
    }

    public static String detachPolicyPrevew(String policyType, String policyName, String lineSeparate)
        throws Exception {
        if (StringUtils.isBlank(lineSeparate)) {
            lineSeparate = "\n";
        }
        StringBuilder sb = new StringBuilder();
        Assert.hasText(policyType, "policyType can not be blank!");
        Assert.hasText(policyName, "policyName can not be blank!");
        ListEntitiesForPolicyRequest request = new ListEntitiesForPolicyRequest();
        request.setPolicyType(policyType);
        request.setPolicyName(policyName);
        ListEntitiesForPolicyResponse response = CLIENT_HANGZHOU.getAcsResponse(request);
        List<com.aliyuncs.ram.model.v20150501.ListEntitiesForPolicyResponse.Group> groupList = response.getGroups();
        if (groupList != null) {
            for (com.aliyuncs.ram.model.v20150501.ListEntitiesForPolicyResponse.Group group : groupList) {
                sb.append("[Group] " + group.getGroupName() + lineSeparate);
            }
        }
        List<com.aliyuncs.ram.model.v20150501.ListEntitiesForPolicyResponse.User> userList = response.getUsers();
        if (userList != null) {
            for (com.aliyuncs.ram.model.v20150501.ListEntitiesForPolicyResponse.User user : userList) {
                sb.append("[User] " + user.getUserName() + lineSeparate);
            }
        }
        List<com.aliyuncs.ram.model.v20150501.ListEntitiesForPolicyResponse.Role> roleList = response.getRoles();
        if (userList != null) {
            for (com.aliyuncs.ram.model.v20150501.ListEntitiesForPolicyResponse.Role role : roleList) {
                sb.append("[Role] " + role.getRoleName() + lineSeparate);
            }
        }
        return sb.toString();
    }

    public static String deletePolicy(String policyName) throws Exception {
        Assert.hasText(policyName, "policyName can not be blank!");
        DeletePolicyRequest request = new DeletePolicyRequest();
        request.setPolicyName(policyName);
        DeletePolicyResponse response = CLIENT_HANGZHOU.getAcsResponse(request);
        logger.info("delete policy[{}],requestID[{}]", policyName, response.getRequestId());
        return response.getRequestId();
    }

    public static List<com.aliyuncs.ram.model.v20150501.ListPoliciesResponse.Policy> listPolicies(String policyType)
        throws Exception {
        List<com.aliyuncs.ram.model.v20150501.ListPoliciesResponse.Policy> result = new ArrayList<>();
        ListPoliciesRequest request = new ListPoliciesRequest();
        if (StringUtils.isNotBlank(policyType)) {
            request.setPolicyType(policyType);
        }
        request.setMaxItems(MaxPolicyItems);
        while (true) {
            ListPoliciesResponse response = CLIENT_HANGZHOU.getAcsResponse(request);
            List<com.aliyuncs.ram.model.v20150501.ListPoliciesResponse.Policy> policyList = response.getPolicies();
            String marker = response.getMarker();
            request.setMarker(marker);
            result.addAll(policyList);
            if (!response.getIsTruncated()) {
                break;
            }
        }

        return result;

    }


    /**
     * 根据相同action合并policy, Effect+Action+Condition
     * 
     * @param statementList
     * @return
     */
    public static List<Statement> traversalPolicyBySameAction(List<Statement> statementList) {
        Map<String, Statement> map = new HashMap<>();
        for (Statement statement : statementList) {
            // 排序处理
            Collections.sort(statement.getAction());
            String key = statement.getEffect() + "_" + JsonUtils.toJsonStringDefault(statement.getAction()) + "_"
                + JsonUtils.toJsonStringDefault(statement.getCondition());
            Statement statementBase = map.get(key);
            if (null == statementBase) {
                map.put(key, statement);
            } else {
                int statementBaseLength = JsonUtils.toJsonStringDefault(statementBase).length();
                int statementMergeLength = JsonUtils.toJsonStringDefault(statementBase.getResource()).length();
                if ((statementBaseLength + statementMergeLength) >= MaxPolicyLength) {
                    // 如果长度过长,就另存为
                    map.put(key + UUIDUtils.generateUUID(), statementBase);
                    map.put(key, statement);
                } else {
                    // use set to merge
                    Set<String> resourceSet = new HashSet<>();
                    resourceSet.addAll(statementBase.getResource());
                    resourceSet.addAll(statement.getResource());
                    statementBase.setResource(new ArrayList<>(resourceSet));
                }

            }
        }
        return new ArrayList<>(map.values());
    }

    /**
     * 根据相同资源合并policy,Effect+Resource+Condition
     * 
     * @param statementList
     * @return
     */
    public static List<Statement> traversalPolicyBySameResource(List<Statement> statementList) {
        Map<String, Statement> map = new HashMap<>();
        for (Statement statement : statementList) {
            // 排序处理
            Collections.sort(statement.getResource());
            String key = statement.getEffect() + "_" + JsonUtils.toJsonStringDefault(statement.getResource()) + "_"
                + JsonUtils.toJsonStringDefault(statement.getCondition());
            Statement statementBase = map.get(key);
            if (null == statementBase) {
                map.put(key, statement);
            } else {

                int statementBaseLength = JsonUtils.toJsonStringDefault(statementBase).length();
                int statementMergeLength = JsonUtils.toJsonStringDefault(statementBase.getAction()).length();
                if ((statementBaseLength + statementMergeLength) >= MaxPolicyLength) {
                    // 如果长度过长,就另存为
                    map.put(key + UUIDUtils.generateUUID(), statementBase);
                    map.put(key, statement);
                } else {
                    // use set to merge
                    Set<String> actionSet = new HashSet<>();
                    actionSet.addAll(statementBase.getAction());
                    actionSet.addAll(statement.getAction());
                    statementBase.setAction(new ArrayList<>(actionSet));
                }

            }
        }
        return new ArrayList<>(map.values());
    }

    /**
     * 根据charMaxLength自动构造PolicyDocument
     * 
     * @param statementList
     * @param charMaxLength
     * @return
     */
    public static List<PolicyDocument> buildPolicyDocuments(List<Statement> statementList) {
        List<PolicyDocument> policyDocumentList = new ArrayList<>();
        List<Statement> tempList = new ArrayList<>();
        for (Statement statement : statementList) {
            int tempListLength = JsonUtils.toJsonStringDefault(tempList).length();
            int statementLength = JsonUtils.toJsonStringDefault(statement).length();
            if ((tempListLength + statementLength) >= MaxPolicyLength) {
                policyDocumentList.add(new PolicyDocument("1", tempList));
                tempList = new ArrayList<>();
            }
            tempList.add(statement);
        }
        if (!tempList.isEmpty()) {
            policyDocumentList.add(new PolicyDocument("1", tempList));
        }
        return policyDocumentList;
    }

    public static void main(String[] args) throws Exception {
        List<com.aliyuncs.ram.model.v20150501.ListPoliciesResponse.Policy> policies = listPolicies(null);
        for (com.aliyuncs.ram.model.v20150501.ListPoliciesResponse.Policy policy : policies) {
            System.out.println(policy.getPolicyName() + "____" + policy.getPolicyType());
        }
    }

    public static void main1(String[] args) {

        List<Statement> statementList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            String sameAction_policyStr1 = "{\n"
                + "    \"Statement\": [\n"
                + "        {\n"
                + "            \"Effect\": \"Allow\",\n"
                + "            \"Action\": [\n"
                + "                \"mq:PUB\",\n"
                + "                \"mq:SUB\"\n"
                + "            ],\n"
                + "            \"Resource\": \"acs:mq:*:*:MQ_INST_1373760908728050111_EKpFUYxx%alita-customer-dt\"\n"
                + "        }\n"
                + "    ],\n"
                + "    \"Version\": \"1\"\n"
                + "}";
            PolicyDocument policy = JsonUtils.parseObject(sameAction_policyStr1, PolicyDocument.class);
            policy.getStatement().get(0).getResource()
                .add("acs:mq:*:*:MQ_INST_1373760908728050111_" + UUIDUtils.generateUUID());
            statementList.addAll(policy.getStatement());
        }

        List<PolicyDocument> policyDocuments = buildPolicyDocuments(statementList);
        System.out.println(policyDocuments.size());
        System.out.println(JsonUtils.toJsonStringDefault(policyDocuments.get(0)));
    }

}
