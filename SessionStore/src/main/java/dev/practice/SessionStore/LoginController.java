package dev.practice.SessionStore;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @GetMapping("/login")
    public String login(@RequestParam String name, HttpSession session) {

        session.setAttribute("name", name);

        return "OK";
    }

    @GetMapping("/myname")
    public String getName(HttpSession session) {

        return (String) session.getAttribute("name");
    }
}
