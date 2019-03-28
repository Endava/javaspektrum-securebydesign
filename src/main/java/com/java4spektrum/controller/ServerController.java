package com.java4spektrum.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@RestController
@RequestMapping("/api")
public class ServerController {

    private static final Logger LOG = LogManager.getLogger(ServerController.class);
    private static final String RESPONSE = "Greetings from REST service!\n";

    @RequestMapping(value="/hello", method=RequestMethod.GET)
    public String helloWorld() {

        LOG.info("This is a test EVENT for Log4j2 implementation");
        LOG.error("This is a test ERROR for Log4j2 implementation");
        LOG.debug("Transaction Details:USER:186223,IP:192.168.0.10,CCNUM:5465123456789012,AMNT:3425");

        return RESPONSE;
    }

}

