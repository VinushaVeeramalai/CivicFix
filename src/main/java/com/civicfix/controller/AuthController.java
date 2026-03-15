package com.civicfix.controller;

import com.civicfix.model.User;
import com.civicfix.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/register")
    public String registerForm(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return "redirect:/";
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String phone,
            RedirectAttributes redirectAttributes
    ) {
        if (name == null || name.isBlank() || email == null || email.isBlank() || password == null || password.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Name, email and password are required.");
            return "redirect:/register";
        }
        if (userRepository.findByEmail(email.trim()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "An account with this email already exists.");
            return "redirect:/register";
        }
        User user = new User(name.trim(), email.trim(), passwordEncoder.encode(password), phone != null ? phone.trim() : "", "USER");
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Registration successful. Please log in.");
        return "redirect:/login?registered=true";
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) Boolean registered, Model model, HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return "redirect:/";
        }
        if (Boolean.TRUE.equals(registered)) {
            model.addAttribute("success", "Registration successful. Please log in.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            RedirectAttributes redirectAttributes,
            HttpSession session
    ) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Email and password are required.");
            return "redirect:/login";
        }
        Optional<User> opt = userRepository.findByEmail(email.trim());
        if (opt.isEmpty() || !passwordEncoder.matches(password, opt.get().getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Invalid email or password.");
            return "redirect:/login";
        }
        User user = opt.get();
        session.setAttribute("userId", user.getId());
        session.setAttribute("userName", user.getName());
        session.setAttribute("userEmail", user.getEmail());
        session.setAttribute("userRole", user.getRole());
        session.setAttribute("userPoints", user.getPoints());
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("error", "Please log in to view your profile.");
            return "redirect:/login";
        }
        return userRepository.findById(userId)
                .map(user -> {
                    model.addAttribute("user", user);
                    return "profile";
                })
                .orElseGet(() -> {
                    session.invalidate();
                    return "redirect:/login";
                });
    }
}
