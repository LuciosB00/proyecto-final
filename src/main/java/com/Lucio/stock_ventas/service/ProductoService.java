package com.Lucio.stock_ventas.service;

import com.Lucio.stock_ventas.dto.ProductoDTO;
import com.Lucio.stock_ventas.entity.Producto;
import com.Lucio.stock_ventas.repository.ProductoRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto actualizar(Long id, Producto producto) {
        Producto existente = obtenerPorId(id);

        existente.setNombre(producto.getNombre());
        existente.setPrecio(producto.getPrecio());
        existente.setStock(producto.getStock());
        existente.setActivo(producto.getActivo());

        return productoRepository.save(existente);
    }

    public void eliminar(Long id) {
        Producto producto = obtenerPorId(id);
        productoRepository.delete(producto);
    }

    public List<Producto> obtenerConStockBajo() {
        return productoRepository.findByStockLessThan(10);
    }

    public Producto guardar(@Valid ProductoDTO producto) {
        return productoRepository.save(producto.toEntity());
    }

    public Producto actualizar(Long id, @Valid ProductoDTO producto) {
        Producto existente = obtenerPorId(id);
        return existente;
    }

    public void desactivar(Long id) {

    }
}