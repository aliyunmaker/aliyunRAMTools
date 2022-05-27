package aliyunRAMTools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan("aliyunRAMTools")

public class My_aliyunRAMTools_Application {

    public static void main(String[] args) {
        SpringApplication.run(My_aliyunRAMTools_Application.class, args);
    }

}
