package com.ensueno.redis;

import com.ensueno.redis.model.AppData;
import com.ensueno.redis.service.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RedisServiceTest {

    @Autowired
    private RedisService redisService;
    private static final String redisKey = "oneplatformIOS";

    @Test
    void putRedis() throws JsonProcessingException {

        AppData appData = AppData.builder()
                .pushCert("./cert/tms_distribution.p12")
                .enc2Pa("12341234")
                .appName("appIOS")
                .appKey("oneplatformIOS")
                .build();

        redisService.putRedis(redisKey, appData);
    }

}
