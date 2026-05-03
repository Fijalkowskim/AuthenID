package com.fijalkowskim.b2bshop.controller;

import com.fijalkowskim.b2bshop.config.DemoData;
import com.fijalkowskim.b2bshop.config.UserInfoHelper;
import com.fijalkowskim.b2bshop.model.UserInfo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ShopController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser != null) {
            return "redirect:/dashboard";
        }
        return "home";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
        model.addAttribute("user", UserInfoHelper.fromOidcUser(oidcUser));
        model.addAttribute("currentPage", "dashboard");
        return "shop/dashboard";
    }

    @GetMapping("/catalog")
    public String catalog(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
        model.addAttribute("user", UserInfoHelper.fromOidcUser(oidcUser));
        model.addAttribute("products", DemoData.products());
        model.addAttribute("currentPage", "catalog");
        return "shop/catalog";
    }

    @GetMapping("/orders")
    public String orders(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
        UserInfo user = UserInfoHelper.fromOidcUser(oidcUser);
        if (!user.hasRole("B2B_BUYER") && !user.isAdmin()) {
            return "redirect:/dashboard";
        }
        model.addAttribute("user", user);
        model.addAttribute("orders", DemoData.orders());
        model.addAttribute("currentPage", "orders");
        return "shop/orders";
    }

    @GetMapping("/pricing")
    public String pricing(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
        UserInfo user = UserInfoHelper.fromOidcUser(oidcUser);
        if (!user.isSales() && !user.isAdmin()) {
            return "redirect:/dashboard";
        }
        model.addAttribute("user", user);
        model.addAttribute("rules", DemoData.pricingRules());
        model.addAttribute("currentPage", "pricing");
        return "shop/pricing";
    }

    @GetMapping("/account")
    public String account(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
        UserInfo user = UserInfoHelper.fromOidcUser(oidcUser);
        if (!user.isAdmin()) {
            return "redirect:/dashboard";
        }
        model.addAttribute("user", user);
        model.addAttribute("currentPage", "account");
        return "shop/account";
    }
}
