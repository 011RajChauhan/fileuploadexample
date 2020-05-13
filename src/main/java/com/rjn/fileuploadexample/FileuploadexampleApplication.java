package com.rjn.fileuploadexample;

import com.rjn.fileuploadexample.property.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        FileStorageProperties.class
})
public class FileuploadexampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileuploadexampleApplication.class, args);
    }
}
