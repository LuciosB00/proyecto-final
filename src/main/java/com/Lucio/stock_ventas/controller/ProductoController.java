package com.Lucio.stock_ventas.controller;

import com.Lucio.stock_ventas.dto.ProductoDTO;
import com.Lucio.stock_ventas.entity.Producto;
import com.Lucio.stock_ventas.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) { this.productoService = productoService; }

    @GetMapping
    public List<Producto> obtenerTodos() { return productoService.obtenerTodos(); }

    @GetMapping("/stock-bajo")
    public List<Producto> obtenerStockBajo() { return productoService.obtenerConStockBajo(); }

    @GetMapping("/{id}")
    public Producto obtenerPorId(@PathVariable Long id) { return productoService.obtenerPorId(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto guardar(@Valid @RequestBody ProductoDTO producto) { return productoService.guardar(producto); }

    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Long id, @Valid @RequestBody ProductoDTO producto) {
        return productoService.actualizar(id, producto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id) { productoService.desactivar(id); }
}
