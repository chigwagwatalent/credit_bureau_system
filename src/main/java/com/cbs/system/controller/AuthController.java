package com.cbs.system.controller;

import com.cbs.system.entity.Users;
import com.cbs.system.enums.RoleEnums;
import com.cbs.system.repository.UsersRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage(
            Model model,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "registered", required = false) String registered) {

        model.addAttribute("error", error != null);
        model.addAttribute("logout", logout != null);
        model.addAttribute("registered", registered != null);
        return "auth/login"; 
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("form", new RegisterForm());
        model.addAttribute("roles", RoleEnums.values());
        return "auth/register"; 
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterForm form,
                           BindingResult result,
                           Model model) {
        final String username = safeTrimLower(form.getUsername());
        final String email = safeTrimLower(form.getEmail());

        if (username == null || username.length() < 3) {
            result.rejectValue("username", "invalid", "Username must be at least 3 characters");
        }
        if (form.getPassword() == null || form.getPassword().trim().length() < 6) {
            result.rejectValue("password", "weak", "Password must be at least 6 characters");
        }

        if (usersRepository.existsByUsername(username)) {
            result.rejectValue("username", "exists", "Username is already taken");
        }
        if (usersRepository.existsByEmail(email)) {
            result.rejectValue("email", "exists", "Email is already registered");
        }

        if (result.hasErrors()) {
            model.addAttribute("roles", RoleEnums.values());
            return "auth/register";
        }

        Users user = new Users(
                username,
                email,
                passwordEncoder.encode(form.getPassword().trim()),
                form.getRole() == null ? RoleEnums.ADMIN : form.getRole(),
                true
        );

        usersRepository.save(user);
        log.info("USER_REGISTERED username={} role={}", username, user.getRole());

        return "redirect:/login?registered=true";
    }

    private static String safeTrimLower(String v) {
        return (v == null) ? null : v.trim().toLowerCase();
    }

    public static class RegisterForm {
        @NotBlank 
        private String username;
        
        @Email @NotBlank 
        private String email;
        
        @NotBlank 
        private String password;
        
        private RoleEnums role;

        public String getUsername() { 
            return username; }
        
        public void setUsername(String username) { 
            this.username = username; }
        
        public String getEmail() { 
            return email; }
        
        public void setEmail(String email) { 
            this.email = email; }
        
        public String getPassword() { 
            return password; }
        
        public void setPassword(String password) { 
            this.password = password; }
        
        public RoleEnums getRole() {             
            return role; }
        
        public void setRole(RoleEnums role) { 
            this.role = role; }
    }
}
