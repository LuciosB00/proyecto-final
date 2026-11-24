
package com.Lucio.stock_ventas.service;

import com.Lucio.stock_ventas.dto.ProductoDTO;
import com.Lucio.stock_ventas.entity.Producto;
import com.Lucio.stock_ventas.repository.ProductoRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> obtenerTodos() {
        return productoRepository.findByActivoTrueOrderByNombreAsc();
    }

    public Producto obtenerPorId(Long id) {
        return productoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() ->
                        new java.util.NoSuchElementException("Producto no encontrado"));
    }

    public Producto guardar(@Valid ProductoDTO datos) {
        return productoRepository.save(datos.toEntity());
    }

    public Producto actualizar(Long id, @Valid ProductoDTO datos) {
        Producto existente = obtenerPorId(id);

        existente.setNombre(datos.getNombre());
        existente.setPrecio(datos.getPrecio());
        existente.setStock(datos.getStock());

        return productoRepository.save(existente);
    }

    public void desactivar(Long id) {
        Producto producto = obtenerPorId(id);
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    public List<Producto> obtenerConStockBajo() {
        return productoRepository.findByActivoTrueAndStockLessThanEqualOrderByStockAscNombreAsc(5);
    }
}
