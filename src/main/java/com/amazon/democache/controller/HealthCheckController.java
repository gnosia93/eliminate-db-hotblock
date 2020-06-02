package com.amazon.democache.controller;

import com.amazon.democache.common.Response;
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
