package org.example.onepiece;

import jakarta.annotation.Resource;
import org.example.onepiece.service.Impl.ShopServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OnePieceApplicationTests {

    @Resource
    private ShopServiceImpl shopService;

    @Test
    void SaveShop(){
        shopService.SaveShop2Redis(1L, 10L);
    }

}
