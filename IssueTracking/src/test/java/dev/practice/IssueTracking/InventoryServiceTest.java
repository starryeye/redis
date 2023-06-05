package dev.practice.IssueTracking;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class InventoryServiceTest {

    @Autowired
    private InventoryServiceWithLettuce lettuce;
    @Autowired
    private InventoryServiceWithRedisson redisson;

    @Test
    void push_using_lettuce_get_using_redisson() {
        lettuce.setCurrentStock("test", 100);
        try {
            int value = redisson.getCurrentStock("test");
            System.out.println("value : " + value);
        }catch (Exception e) {
            e.printStackTrace();
            Assertions.assertThat(true).isFalse();
        }
    }

    @Test
    void push_using_redisson_get_using_redisson() {
        redisson.setCurrentStock("test", 100);
        try {
            int value = redisson.getCurrentStock("test");
            System.out.println("value : " + value);
        }catch (Exception e) {
            e.printStackTrace();
            Assertions.assertThat(true).isFalse();
        }
    }
}
