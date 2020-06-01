package com.amazon.mbp.service;

import com.amazon.mbp.entity.Order;
import com.amazon.mbp.entity.Product;
import com.amazon.mbp.entity.User;
import com.amazon.mbp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Transactional
@Service
public class ProductService {

    @Autowired ProductRepository productRepository;
    @Autowired MemoryDBService memoryDBService;

    public Page<Product> findAll(Pageable page) {
        return productRepository.findAll(page);
    }

    public Optional<Product> findById(User user, int id) {
        Optional<Product> optProduct = findById(id);
        memoryDBService.addProductViewList(user, optProduct.get());

        int eventBuyCount = memoryDBService.getProductBuyCount(id);
        optProduct.map(e -> {
            e.setEventBuyCount(eventBuyCount);
            return e;
        } );

        return optProduct;
    }

    public Optional<Product> findById(int id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }


    public void addBuyCount(int productId) {

        productRepository.addBuyCount(productId);

    }

    public List<Product> getViewHistory(User user) {
        return memoryDBService.getViewHistory(user);
    }

}
