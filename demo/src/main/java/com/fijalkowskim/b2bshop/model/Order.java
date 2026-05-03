package com.fijalkowskim.b2bshop.model;

public record Order(String id, String product, int qty, String total, String status, String buyer) {}
