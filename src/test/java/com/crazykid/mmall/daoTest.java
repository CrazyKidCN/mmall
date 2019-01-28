package com.crazykid.mmall;

import com.crazykid.mmall.dao.CartMapper;
import com.crazykid.mmall.pojo.Cart;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class daoTest {
    @Autowired
    CartMapper cartMapper;

    @Test
    public void testSelect() {
        Cart cart = cartMapper.selectByPrimaryKey(126);
        System.out.println(cart);
    }
}
