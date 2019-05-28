package com.cheng.erik.john.controller;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Set;

/**
 * @ClassName ：AuthController
 * @Author ：JohnErikCheng
 * @Email ：dong@19910925@126.com
 * @Date ：Created in 2019/5/28 18:42
 * @Description:
 */

@Controller
public class AuthController {

    @GetMapping("/articles")
    public String search(Principal principal, Model model) {
        if (principal instanceof KeycloakPrincipal) {
            AccessToken accessToken = ((KeycloakPrincipal) principal).getKeycloakSecurityContext().getToken();
            String userName = accessToken.getPreferredUsername();
            AccessToken.Access realmAccess = accessToken.getRealmAccess();
            Set<String> roles = realmAccess.getRoles();
            System.out.println("当前登录用户:" + userName + ",角色：" + roles);
            model.addAttribute("userName",userName);
            model.addAttribute("roles",roles);
        }
        return "index";
    }
}
