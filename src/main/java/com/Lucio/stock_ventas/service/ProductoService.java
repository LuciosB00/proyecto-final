package com.Lucio.stock_ventas.service;

import com.Lucio.stock_ventas.dto.ProductoDTO;
import com.Lucio.stock_ventas.entity.Producto;
import com.Lucio.stock_ventas.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductoService {
    private static final int LIMITE_STOCK_BAJO = 5;
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> obtenerTodos() {
        return productoRepository.findByActivoTrueOrderByNombreAsc();
    }

    public List<Producto> obtenerConStockBajo() {
        return productoRepository.findByActivoTrueAndStockLessThanEqualOrderByStockAscNombreAsc(LIMITE_STOCK_BAJO);
    }

    public Producto obtenerPorId(Long id) {
        return productoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Producto no encontrado"));
    }

    @Transactional
    public Producto guardar(ProductoDTO datos) {
        Producto producto = new Producto(datos.getNombre().trim(), datos.getPrecio(), datos.getStock());
        return productoRepository.save(producto);
    }

    @Transactional
    public Producto actualizar(Long id, ProductoDTO datos) {
        Producto producto = obtenerPorId(id);
        producto.setNombre(datos.getNombre().trim());
        producto.setPrecio(datos.getPrecio());
        producto.setStock(datos.getStock());
        return productoRepository.save(producto);
    }

    @Transactional
    public void desactivar(Long id) {
        Producto producto = obtenerPorId(id);
        producto.setActivo(false);
        productoRepository.save(producto);
    }
}
