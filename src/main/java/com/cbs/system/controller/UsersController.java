package com.cbs.system.controller;

import com.cbs.system.dto.UserDTO;
import com.cbs.system.enums.RoleEnums;
import com.cbs.system.repository.UsersRepository;
import com.cbs.system.services.UsersService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class UsersController {

    private final UsersService service;
    private final UsersRepository repo;

    public UsersController(UsersService service, UsersRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "10") int size,
                       @RequestParam(value = "sort", defaultValue = "username") String sort,
                       @RequestParam(value = "dir", defaultValue = "asc") String dir,
                       Model model) {
        Sort s = "desc".equalsIgnoreCase(dir) ? Sort.by(sort).descending() : Sort.by(sort).ascending();
        Pageable pageable = PageRequest.of(page, size, s);
        Page<UserDTO> result = service.list(q, pageable);

        model.addAttribute("q", q);
        model.addAttribute("page", result);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);

        model.addAttribute("usersTotal", repo.count());
        model.addAttribute("usersActive", repo.countByActiveTrue());
        model.addAttribute("admins", repo.countByRole(RoleEnums.ADMIN)); // adjust enum constant if different

        return "admin/users_list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new UserDTO());
        model.addAttribute("roles", RoleEnums.values());
        model.addAttribute("mode", "create");
        return "admin/users_form";
    }

    @PostMapping
    public String create(@ModelAttribute("form") @Valid UserDTO form,
                         BindingResult binding,
                         RedirectAttributes ra,
                         Model model) {
        if (binding.hasErrors() || form.getPassword() == null || form.getPassword().isBlank()) {
            if (form.getPassword() == null || form.getPassword().isBlank())
                binding.rejectValue("password", "NotBlank", "Password is required");
            model.addAttribute("roles", RoleEnums.values());
            model.addAttribute("mode", "create");
            return "admin/users_form";
        }
        try {
            UserDTO saved = service.create(form);
            ra.addFlashAttribute("success", "User created: " + saved.getUsername());
            return "redirect:/admin/users";
        } catch (DataIntegrityViolationException ex) {
            binding.reject("unique", ex.getMessage());
            model.addAttribute("roles", RoleEnums.values());
            model.addAttribute("mode", "create");
            return "admin/users_form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.get(id));
        model.addAttribute("roles", RoleEnums.values());
        model.addAttribute("mode", "edit");
        return "admin/users_form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("form") @Valid UserDTO form,
                         BindingResult binding,
                         RedirectAttributes ra,
                         Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("roles", RoleEnums.values());
            model.addAttribute("mode", "edit");
            return "admin/users_form";
        }
        try {
            UserDTO saved = service.update(id, form);
            ra.addFlashAttribute("success", "User updated: " + saved.getUsername());
            return "redirect:/admin/users";
        } catch (DataIntegrityViolationException ex) {
            binding.reject("unique", ex.getMessage());
            model.addAttribute("roles", RoleEnums.values());
            model.addAttribute("mode", "edit");
            return "admin/users_form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        service.delete(id);
        ra.addFlashAttribute("success", "User deleted");
        return "redirect:/admin/users";
    }
}
