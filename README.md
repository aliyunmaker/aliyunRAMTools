# 配置文件

```properties
Aliyun_AccessKeyId = tRrmF**********
Aliyun_AccessKeySecret = 4CD07*******************
```

#### 读取顺序
- 运行时参数,示例: java -jar -DconfigPath=/home/config/aliyunRAMTools.properties aliyunRAMTools.jar  
- aliyunRAMTools.jar同目录下的aliyunRAMTools.properties
- 用户home/admin/config/目录下的aliyunRAMTools.properties



# 启动(springboot)
jar包启动: java -jar aliyunRAMTools.jar



# 打包

`mvn clean package -Dmaven.test.skip=true`