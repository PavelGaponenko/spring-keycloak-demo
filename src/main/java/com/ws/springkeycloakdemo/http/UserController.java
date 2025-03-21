package com.ws.springkeycloakdemo.http;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контроллер обработки пользовательских страниц
 */
@Controller
public class UserController {
    @GetMapping("/")
    @PreAuthorize("hasRole('USER')")
    public String getMainPage() {
        return "main";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String getAdminPage() {
        return "admin";
    }

    @GetMapping("/manager")
    @PreAuthorize("hasRole('MANAGER')")
    public String getManagerPage() {
        return "manager";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public String getUserPage() {
        return "user";
    }
}
