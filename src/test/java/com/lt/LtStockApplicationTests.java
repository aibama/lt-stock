package com.lt;

import com.lt.common.MailUtil;
import com.lt.common.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LtStockApplicationTests {

    @Autowired
    MailUtil mailUtil;

    @Test
    public void contextLoads() {
        mailUtil.sendSimpleMail("gjf0519@163.com","代码","123456");
    }
}
