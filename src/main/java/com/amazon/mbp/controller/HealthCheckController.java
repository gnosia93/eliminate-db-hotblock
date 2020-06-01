package com.amazon.mbp.controller;

import com.amazon.mbp.common.Response;
import com.amazon.mbp.entity.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @RequestMapping("/")
    public Response<?> list() {

        Response<?> response = Response.success("I am working!");
        return Response.success(response);
    }

}
