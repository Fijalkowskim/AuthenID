package com.fijalkowskim.b2bshop.config;

import com.fijalkowskim.b2bshop.model.Order;
import com.fijalkowskim.b2bshop.model.PricingRule;
import com.fijalkowskim.b2bshop.model.Product;

import java.util.List;

public class DemoData {

    public static List<Product> products() {
        return List.of(
            new Product("Resistor 10k",      "RES-10K",     "$0.05",  12400, "Passive"),
            new Product("Capacitor 100uF",   "CAP-100U",    "$0.12",   5600, "Passive"),
            new Product("STM32F4 MCU",       "MCU-STM32F4", "$8.90",    340, "Microcontrollers"),
            new Product("BC547 Transistor",  "TRANS-BC547", "$0.08",  22000, "Semiconductors"),
            new Product("LM358 Op-Amp",      "IC-LM358",    "$0.35",   1800, "ICs"),
            new Product("24V Power Supply",  "PSU-24V5A",   "$24.99",   120, "Power")
        );
    }

    public static List<Order> orders() {
        return List.of(
            new Order("ORD-0041", "Resistor 10k",     5000,  "$250.00",  "Delivered",  "buyer1"),
            new Order("ORD-0042", "STM32F4 MCU",        20,  "$178.00",  "Processing", "buyer1"),
            new Order("ORD-0043", "Capacitor 100uF",  1000,   "$120.00", "Shipped",    "buyer2"),
            new Order("ORD-0044", "LM358 Op-Amp",      500,   "$175.00", "Pending",    "buyer1"),
            new Order("ORD-0045", "24V Power Supply",   10,   "$249.90", "Processing", "buyer2")
        );
    }

    public static List<PricingRule> pricingRules() {
        return List.of(
            new PricingRule("Resistor 10k",     "Standard",   "$0.05",  "0%",  "1"),
            new PricingRule("Resistor 10k",     "Volume",     "$0.04", "20%", "1000"),
            new PricingRule("STM32F4 MCU",      "Standard",   "$8.90",  "0%",  "1"),
            new PricingRule("STM32F4 MCU",      "Volume",     "$7.50", "16%",  "10"),
            new PricingRule("Capacitor 100uF",  "Standard",   "$0.12",  "0%",  "1"),
            new PricingRule("Capacitor 100uF",  "Volume",     "$0.09", "25%", "500"),
            new PricingRule("24V Power Supply", "Standard",  "$24.99",  "0%",  "1"),
            new PricingRule("24V Power Supply", "Volume",    "$21.99", "12%",  "5")
        );
    }
}
