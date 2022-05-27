package aliyunRAMTools.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.TypeReference;
import com.aliyuncs.ram.model.v20150501.ListPoliciesForUserResponse.Policy;
import com.aliyuncs.ram.model.v20150501.ListUsersResponse.User;

import aliyunRAMTools.model.PolicyDocument;
import aliyunRAMTools.model.Statement;
import aliyunRAMTools.model.WebResult;
import aliyunRAMTools.service.AliyunCMPService;
import aliyunRAMTools.utils.JsonUtils;
import aliyunRAMTools.utils.UUIDUtils;

@Controller
@RequestMapping("/aliyunram")
public class AliyunRAMController extends BaseController {

    @RequestMapping("/searchRAMUser")
    public void searchRAMUser(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String simpleSearch = request.getParameter("simpleSearch");
            List<User> list = AliyunCMPService.getAllRAMUser();
            List<User> targetList = new ArrayList<>();
            if (StringUtils.isBlank(simpleSearch)) {
                targetList = list;
            } else {
                for (User user : list) {
                    if (user.getUserName().contains(simpleSearch) || user.getDisplayName().contains(simpleSearch)) {
                        targetList.add(user);
                    }
                }
            }
            result.setTotal(targetList.size());
            result.setData(targetList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/listPoliciesForUser")
    public void listPoliciesForUser(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String userName = request.getParameter("userName");
            List<Policy> list = AliyunCMPService.listPoliciesForUser(userName);
            result.setTotal(list.size());
            result.setData(list);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/getPolicy")
    public void getPolicy(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String policyType = request.getParameter("policyType");
            String policyName = request.getParameter("policyName");
            String versionId = request.getParameter("versionId");
            String policyDocument = AliyunCMPService.getPolicy(policyType, policyName, versionId);
            String preview = AliyunCMPService.detachPolicyPrevew(policyType, policyName, "\n");
            Map<String, String> map = new HashMap<>();
            map.put("preview", preview);
            map.put("policyDocument", policyDocument);
            result.setData(map);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/createPolicy")
    public void createPolicy(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String formString = request.getParameter("formString");
            String policyDocument = JsonUtils.getValueFormJsonString(formString, "policyDocument", String.class);
            String policyName = JsonUtils.getValueFormJsonString(formString, "policyName", String.class);
            AliyunCMPService.createPolicy(policyName, policyDocument);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/mergePolicy")
    public void mergePolicy(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String userName = request.getParameter("userName");
            String policyArray = request.getParameter("policyArray");
            List<Map<String, String>> policyList =
                JsonUtils.parseObject(policyArray, new TypeReference<List<Map<String, String>>>() {});

            List<Statement> statementList = new ArrayList<>();
            for (Map<String, String> map : policyList) {
                String policyDocument =
                    AliyunCMPService.getPolicy(map.get("policyType"), map.get("policyName"), map.get("versionId"));
                PolicyDocument policyToMerge = JsonUtils.parseObject(policyDocument, PolicyDocument.class);
                statementList.addAll(policyToMerge.getStatement());
            }
            List<Statement> mergedList = AliyunCMPService.traversalPolicyBySameAction(statementList);
            List<PolicyDocument> policyMergedList = AliyunCMPService.buildPolicyDocuments(mergedList);

            String uuid = UUIDUtils.generateUUID();
            int index = 1;
            for (PolicyDocument policyMerged : policyMergedList) {
                // create policy (toJsonStringDefault without null propertie )
                com.aliyuncs.ram.model.v20150501.CreatePolicyResponse.Policy policy =
                    AliyunCMPService.createPolicy("charles_" + uuid + "_" + index,
                        JsonUtils.toJsonStringDefault(policyMerged));
                // attach Policy To User
                AliyunCMPService.attachPolicyToUser(policy.getPolicyType(), policy.getPolicyName(), userName);
                index++;
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/mergePolicyPreview")
    public void mergePolicyPreview(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String policyArray = request.getParameter("policyArray");
            List<Map<String, String>> policyList =
                JsonUtils.parseObject(policyArray, new TypeReference<List<Map<String, String>>>() {});
            List<Statement> statementList = new ArrayList<>();
            for (Map<String, String> map : policyList) {
                String policyDocument =
                    AliyunCMPService.getPolicy(map.get("policyType"), map.get("policyName"), map.get("versionId"));
                PolicyDocument policyToMerge = JsonUtils.parseObject(policyDocument, PolicyDocument.class);
                statementList.addAll(policyToMerge.getStatement());
            }
            List<Statement> mergedList = AliyunCMPService.traversalPolicyBySameAction(statementList);
            List<PolicyDocument> policyMergedList = AliyunCMPService.buildPolicyDocuments(mergedList);

            result.setData(JsonUtils.toJsonStringDefault(policyMergedList));
            result.setTotal(policyMergedList.size());

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/detachPolicyFromUser")
    public void detachPolicyFromUser(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String userName = request.getParameter("userName");
            String policyArray = request.getParameter("policyArray");
            List<Map<String, String>> policyList =
                JsonUtils.parseObject(policyArray, new TypeReference<List<Map<String, String>>>() {});
            for (Map<String, String> map : policyList) {
                AliyunCMPService.detachPolicyFromUser(map.get("policyType"), map.get("policyName"), userName);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/deletePolicy")
    public void deletePolicy(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String policyType = request.getParameter("policyType");
            String policyName = request.getParameter("policyName");
            boolean needDelete = Boolean.valueOf(request.getParameter("needDelete"));
            AliyunCMPService.detachPolicy(policyType, policyName);
            if (needDelete) {
                AliyunCMPService.deletePolicy(policyName);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/detachPolicyPrevew")
    public void detachPolicyPrevew(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String policyType = request.getParameter("policyType");
            String policyName = request.getParameter("policyName");
            String lineSeparate = request.getParameter("lineSeparate");
            String preview = AliyunCMPService.detachPolicyPrevew(policyType, policyName, lineSeparate);
            result.setData(preview);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/searchPolicies")
    public void searchPolicies(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String simpleSearch = request.getParameter("simpleSearch");
            String policyType = request.getParameter("policyType");
            List<com.aliyuncs.ram.model.v20150501.ListPoliciesResponse.Policy> policieList =
                AliyunCMPService.listPolicies(policyType);

            List<com.aliyuncs.ram.model.v20150501.ListPoliciesResponse.Policy> targetList = new ArrayList<>();
            if (StringUtils.isBlank(simpleSearch)) {
                targetList = policieList;
            } else {
                for (com.aliyuncs.ram.model.v20150501.ListPoliciesResponse.Policy policy : policieList) {
                    if (policy.getPolicyName().contains(simpleSearch)) {
                        targetList.add(policy);
                    }
                }
            }

            result.setData(targetList);
            result.setTotal(targetList.size());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

}
