package com.cbs.system.controller;

import com.cbs.system.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping({"/admin/dashboard"})
    public String dashboard(Model model) {
        model.addAllAttributes(dashboardService.getStats());
        return "admin/dashboard";
    }
}
