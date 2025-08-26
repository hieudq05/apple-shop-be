package com.web.appleshop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import com.web.appleshop.testutil.TestConfig;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class AppleShopApplicationTests {

    @Test
    void contextLoads() {
    // context should load successfully with test profile
    }

}
