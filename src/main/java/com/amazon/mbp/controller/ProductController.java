package com.amazon.mbp.controller;

import com.amazon.mbp.common.Response;
import com.amazon.mbp.entity.Order;
import com.amazon.mbp.entity.Product;
import com.amazon.mbp.entity.User;
import com.amazon.mbp.repository.ProductRepository;
import com.amazon.mbp.request.ProductRequest;
import com.amazon.mbp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired ProductService productService;

    @RequestMapping(path = "/{page}/{size}", method = RequestMethod.GET)
    public Response<?> list(@PathVariable int page, @PathVariable int size) {

        PageRequest pageRequest = PageRequest.of(page-1, size, Sort.Direction.DESC, "productId" );
        Page<Product> productList = productService.findAll(pageRequest);

        return Response.success(productList);
    }

    @RequestMapping("/{id}")
    public Response<?> get(@RequestHeader(value="sid", required = true)String sid, @PathVariable int id) {

        User user = User.builder().sessionKey(sid).build();
        Optional<Product> product = productService.findById(user, id);

        return Response.success(product);
    }

    @RequestMapping("/history/{id}")
    public Response<?> history(@RequestHeader(value="sid", required = true)String sid, @PathVariable int id) {

        User user = User.builder().sessionKey(sid).build();
        List<Product> listProduct = productService.getViewHistory(user);

        return Response.success(listProduct);
    }


    @RequestMapping(path="/add", method = RequestMethod.POST)
    public Response<?> add(@RequestBody ProductRequest productRequest) {

        Product product = productRequest.toProduct();
        productService.save(product);

        return Response.success(product);
    }
}
