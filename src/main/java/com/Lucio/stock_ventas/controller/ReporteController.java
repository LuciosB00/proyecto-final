package com.Lucio.stock_ventas.controller;

import com.Lucio.stock_ventas.dto.ReporteDTO;
import com.Lucio.stock_ventas.service.ReporteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {
    private final ReporteService reporteService;
    public ReporteController(ReporteService reporteService) { this.reporteService = reporteService; }

    @GetMapping
    public ReporteDTO obtenerReporte() { return reporteService.obtenerReporte(); }
}
