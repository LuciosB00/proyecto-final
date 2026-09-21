package com.Lucio.stock_ventas.repository;

import com.Lucio.stock_ventas.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}