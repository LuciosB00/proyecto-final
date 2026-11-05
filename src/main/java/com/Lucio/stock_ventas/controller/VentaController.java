package com.Lucio.stock_ventas.controller;

import com.Lucio.stock_ventas.dto.ActualizarVentaDTO;
import com.Lucio.stock_ventas.dto.VentaDTO;
import com.Lucio.stock_ventas.entity.EstadoVenta;
import com.Lucio.stock_ventas.entity.Venta;
import com.Lucio.stock_ventas.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {
    private final VentaService ventaService;

    public VentaController(VentaService ventaService) { this.ventaService = ventaService; }

    @GetMapping
    public List<Venta> obtenerTodas(
            @RequestParam(required = false) String producto,
            @RequestParam(required = false) EstadoVenta estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ventaService.obtenerTodas(producto, estado, desde, hasta);
    }

    @GetMapping("/{id}")
    public Venta obtenerPorId(@PathVariable Long id) { return ventaService.obtenerPorId(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Venta guardar(@Valid @RequestBody VentaDTO venta) { return ventaService.guardar(venta); }

    @PatchMapping("/{id}/pago")
    public Venta actualizarEstadoYPago(@PathVariable Long id, @Valid @RequestBody ActualizarVentaDTO datos) {
        return ventaService.actualizarEstadoYPago(id, datos.getEstado(), datos.getMontoPagado());
    }
}
