package ru.otus.java.pro.pddbot.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.java.pro.pddbot.model.ExamSession;
import ru.otus.java.pro.pddbot.model.User;
import ru.otus.java.pro.pddbot.services.SessionService;
import ru.otus.java.pro.pddbot.services.UserService;

@Controller
public class MainController {

    private final UserService userService;

    private final SessionService sessionService;

    @Autowired
    public MainController(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @GetMapping("/")
    public String indexPage(Model model) {
        Iterable<User> users = userService.findAll();
        model.addAttribute("users", users);
        Iterable<ExamSession> examSessions = sessionService.findAll();
        model.addAttribute("examSessions",examSessions);
        return "index";
    }

    @PostMapping("/del_user")
    public String deleteUser(@RequestParam(required = true) String clientId,Model model){
        userService.deleteUser(Long.parseLong(clientId));
        return "redirect:/";
    }

    @PostMapping("/del_session")
    public String deleteSession(@RequestParam(required = true) String sessionId,Model model){
        sessionService.deleteSessionById(Long.parseLong(sessionId));
        return "redirect:/";
    }
}
