package com.cbs.system.controller;

import com.cbs.system.dto.APIUserDTO;
import com.cbs.system.repository.APIUsersRepository;
import com.cbs.system.services.APIUsersService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
@RequestMapping("/admin/api-users")
public class ApiUsersController {

    private final APIUsersService service;
    private final APIUsersRepository repo;

    public ApiUsersController(APIUsersService service, APIUsersRepository repo) {
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
        Page<APIUserDTO> result = service.list(q, pageable);

        model.addAttribute("q", q);
        model.addAttribute("page", result);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);

        model.addAttribute("apiUsersTotal", repo.count());
        model.addAttribute("apiUsersActive", repo.countByActiveTrue());
        return "admin/api_users_list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new APIUserDTO());
        model.addAttribute("mode", "create");
        return "admin/api_users_form";
    }

    @PostMapping
    public String create(@ModelAttribute("form") @Valid APIUserDTO form,
                         BindingResult binding,
                         RedirectAttributes ra,
                         Model model) {
        if (binding.hasErrors() || Objects.toString(form.getPassword(), "").isBlank()) {
            if (Objects.toString(form.getPassword(), "").isBlank())
                binding.rejectValue("password", "NotBlank", "Password is required");
            model.addAttribute("mode", "create");
            return "admin/api_users_form";
        }
        try {
            APIUserDTO saved = service.create(form);
            ra.addFlashAttribute("success", "API user created: " + saved.getUsername());
            return "redirect:/admin/api-users";
        } catch (DataIntegrityViolationException ex) {
            binding.reject("unique", ex.getMessage());
            model.addAttribute("mode", "create");
            return "admin/api_users_form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.get(id));
        model.addAttribute("mode", "edit");
        return "admin/api_users_form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("form") @Valid APIUserDTO form,
                         BindingResult binding,
                         RedirectAttributes ra,
                         Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("mode", "edit");
            return "admin/api_users_form";
        }
        try {
            APIUserDTO saved = service.update(id, form);
            ra.addFlashAttribute("success", "API user updated: " + saved.getUsername());
            return "redirect:/admin/api-users";
        } catch (DataIntegrityViolationException ex) {
            binding.reject("unique", ex.getMessage());
            model.addAttribute("mode", "edit");
            return "admin/api_users_form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        service.delete(id);
        ra.addFlashAttribute("success", "API user deleted");
        return "redirect:/admin/api-users";
    }
}
