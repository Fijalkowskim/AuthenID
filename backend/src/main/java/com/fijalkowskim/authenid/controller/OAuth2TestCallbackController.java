package com.fijalkowskim.authenid.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuth2TestCallbackController {

    @GetMapping("/callback")
    public String callback(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state
    ) {
        return "Authorization code: " + code + (state != null ? ", state: " + state : "");
    }
}
