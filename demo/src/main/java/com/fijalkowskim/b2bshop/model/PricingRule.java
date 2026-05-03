package com.fijalkowskim.b2bshop.model;

public record PricingRule(String product, String tier, String unitPrice, String discount, String minQty) {}
