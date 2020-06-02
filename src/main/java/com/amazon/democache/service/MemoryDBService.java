package com.amazon.democache.service;



import com.amazon.democache.entity.Product;
import com.amazon.democache.entity.User;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Transactional
@Service
public class MemoryDBService {

    @Autowired RedisTemplate<String, Object> redisTemplate;

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyData implements Serializable {
        private static final long serialVersionUID = -7353484588260422449L;
        private int productId;
        private int buyCount;
    }

    public void addProductBuyCount(int productId) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.increment("sell_cnt_" + productId, 1);
    }

    public int getProductBuyCount(int productId) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        long l = valueOperations.increment("sell_cnt_" + productId, 0);
        return (int)l;
    }


    public void addProductViewList(User user, Product product) {
        redisTemplate.opsForZSet().add(getKey(user), product, System.currentTimeMillis());
    }

    public List<Product> getViewHistory(User user) {
        Set<Object> setProduct = redisTemplate.opsForZSet().reverseRange(getKey(user), 0, -1);

        List<Product> listProduct = new ArrayList<>();
        for(Object object: setProduct) {
            Product product = (Product)object;
            listProduct.add(product);
        }
        return listProduct;
    }

    public String getKey(User user) {
        return "vh[" + user.getSessionKey() + "]";
    }

}
