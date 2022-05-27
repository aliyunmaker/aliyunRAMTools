package aliyunRAMTools.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonConstants {

    private static final Logger logger = LoggerFactory.getLogger(CommonConstants.class);

    public static final boolean LOAD_ON_START_UP = true;

    public static final String CONFIG_FILE_NAME = "/aliyunRAMTools.properties";

    public static final String Aliyun_AccessKeyId;

    public static final String Aliyun_AccessKeySecret;

    public static final String Aliyun_REGION_HANGZHOU = "cn-hangzhou";

    static {
        Properties properties = loadProperties();
        Aliyun_AccessKeyId = properties.getProperty("Aliyun_AccessKeyId");
        Aliyun_AccessKeySecret = properties.getProperty("Aliyun_AccessKeySecret");
        logger.info("============================CONFIG=========================");
        logger.info("Aliyun_AccessKeyId : " + Aliyun_AccessKeyId);
        logger.info("Aliyun_AccessKeySecret : *******************");
        logger.info("============================================================");
    }

    public static Properties loadProperties() {
        Properties properties = new Properties();
        try {
            File file = null;
            String configPath = System.getProperty("configPath");
            if (StringUtils.isNotBlank(configPath)) {
                file = new File(configPath);
            }
            if (StringUtils.isBlank(configPath) || !file.exists()) {
                logger.info("[1]can not find config file[-D]:" + configPath);
                configPath = System.getProperty("user.dir") + CONFIG_FILE_NAME;
                file = new File(configPath);
            }
            if (!file.exists()) {
                logger.info("[2]can not find config file[user.dir]:" + configPath);
                configPath = System.getProperty("user.home") + File.separator + "config" + CONFIG_FILE_NAME;
                file = new File(configPath);
            }
            if (!file.exists()) {
                logger.info("[3]can not find config file[user.home]:" + configPath);
                throw new RuntimeException("can not find config file!");
            }
            InputStream ins = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(ins, "UTF-8");
            properties.load(reader);
            ins.close();
            reader.close();
            logger.info("load config file:" + file.getAbsolutePath());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            System.exit(0);
        }
        return properties;
    }

}
