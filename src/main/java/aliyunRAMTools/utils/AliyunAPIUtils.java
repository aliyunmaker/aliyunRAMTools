package aliyunRAMTools.utils;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.auth.BasicCredentials;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;

/**
 * 构建aliyun API 的client
 * 
 * @author charles
 *
 */
public class AliyunAPIUtils {

    private static Logger logger = LoggerFactory.getLogger(AliyunAPIUtils.class);

    public static final String Region_Hangzhou = "cn-hangzhou";
    public static final String Region_Hongkong = "cn-hongkong";

    /**
     * 通用api call
     * 
     * @param client
     * @param domain
     * @param version
     * @param action
     * @param params
     * @throws Exception
     */
    public static String commonInvoke(IAcsClient client, String domain, String version, String action,
        Map<String, String> params) throws Exception {
        Assert.notNull(client, "client can not be null!");
        Assert.hasText(domain, "domain can not be blank!");
        Assert.hasText(version, "version can not be blank!");
        Assert.hasText(action, "action can not be blank!");
        CommonRequest request = new CommonRequest();
        request.setSysDomain(domain);
        request.setSysVersion(version);
        request.setSysAction(action);
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                request.putQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        request.setSysProtocol(ProtocolType.HTTPS);
        CommonResponse response = client.getCommonResponse(request);
        logger.debug("==========================api info===============================");
        logger.debug(
            "[" + request.getSysDomain() + "][" + request.getSysVersion() + "][" + request.getSysAction() + "]");
        logger.debug(JsonUtils.toJsonString(request.getSysQueryParameters()));
        logger.debug("===========================output================================");
        logger.debug(JsonUtils.toJsonString(JSON.parse(response.getData())));
        return response.getData();
    }

    public static String commonInvoke(IAcsClient client, String domain, String version, String action)
        throws Exception {
        return commonInvoke(client, domain, version, action, null);
    }

    /**
     * 根据ak build client
     * 
     * @param accessKeyID
     * @param accessKeySecret
     * @param region
     * @return
     */
    public static IAcsClient buildClient(String accessKeyID, String accessKeySecret, String region) {
        Assert.notNull(accessKeyID, "accessKeyID can not be null!");
        Assert.hasText(accessKeySecret, "accessKeySecret can not be blank!");
        Assert.hasText(region, "region can not be blank!");
        DefaultProfile profile = DefaultProfile.getProfile(region);
        IAcsClient client = new DefaultAcsClient(profile, new BasicCredentials(accessKeyID, accessKeySecret));
        return client;
    }

    public static IAcsClient buildClient_Hangzhou(String accessKeyID, String accessKeySecret) {
        return buildClient(accessKeyID, accessKeySecret, Region_Hangzhou);
    }

}
