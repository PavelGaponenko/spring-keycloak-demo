package com.ws.springkeycloakdemo.http;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контроллер обработки ошибок
 */
@Controller
public class ErrorController {

    @GetMapping("/403")
    public String accessDenied() {
        return "errors/403";
    }
}
