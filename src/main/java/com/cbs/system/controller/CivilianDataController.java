package com.cbs.system.controller;

import com.cbs.system.dto.CivilianDataDTO;
import com.cbs.system.repository.CivilianDataRepository;
import com.cbs.system.services.CivilianDataServices;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin/civilians")
public class CivilianDataController {

    private final CivilianDataServices service;
    private final CivilianDataRepository repo;

    public CivilianDataController(CivilianDataServices service, CivilianDataRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    // LIST
    @GetMapping
    public String list(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "lastName") String sort,
            @RequestParam(value = "dir", defaultValue = "asc") String dir,
            Model model
    ) {
        Sort s = "desc".equalsIgnoreCase(dir) ? Sort.by(sort).descending() : Sort.by(sort).ascending();
        Pageable pageable = PageRequest.of(page, size, s);
        Page<CivilianDataDTO> result = service.list(q, pageable);

        // Paging + filters
        model.addAttribute("q", q);
        model.addAttribute("page", result);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);

        // KPIs for the cards (matching list template)
        model.addAttribute("civiliansTotal", repo.count());
        BigDecimal avgIncome = repo.averageMonthlyIncome();
        model.addAttribute("avgIncome", (avgIncome != null) ? avgIncome : BigDecimal.ZERO);
        model.addAttribute("maleCount", repo.countMale());
        model.addAttribute("femaleCount", repo.countFemale());

        return "admin/civilian_list"; // templates/admin/civilian_list.html
    }

    // CREATE FORM
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new CivilianDataDTO());
        model.addAttribute("mode", "create");
        return "admin/civilian_form"; // templates/admin/civilian_form.html
    }

    // CREATE SUBMIT
    @PostMapping
    public String create(@ModelAttribute("form") @Valid CivilianDataDTO form,
                         BindingResult binding,
                         RedirectAttributes ra,
                         Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("mode", "create");
            return "admin/civilian_form";
        }
        try {
            CivilianDataDTO saved = service.create(form);
            ra.addFlashAttribute("success", "Civilian created: " + saved.getFirstName() + " " + saved.getLastName());
            return "redirect:/admin/civilians";
        } catch (DataIntegrityViolationException ex) {
            binding.reject("unique", ex.getMessage());
            model.addAttribute("mode", "create");
            return "admin/civilian_form";
        }
    }

    // EDIT FORM
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        CivilianDataDTO dto = service.get(id);
        model.addAttribute("form", dto);
        model.addAttribute("mode", "edit");
        return "admin/civilian_form";
    }

    // UPDATE SUBMIT
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("form") @Valid CivilianDataDTO form,
                         BindingResult binding,
                         RedirectAttributes ra,
                         Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("mode", "edit");
            return "admin/civilian_form";
        }
        try {
            CivilianDataDTO saved = service.update(id, form);
            ra.addFlashAttribute("success", "Civilian updated: " + saved.getFirstName() + " " + saved.getLastName());
            return "redirect:/admin/civilians";
        } catch (DataIntegrityViolationException ex) {
            binding.reject("unique", ex.getMessage());
            model.addAttribute("mode", "edit");
            return "admin/civilian_form";
        }
    }

    // DELETE
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        service.delete(id);
        ra.addFlashAttribute("success", "Civilian deleted");
        return "redirect:/admin/civilians";
    }
}
