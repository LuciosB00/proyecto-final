package com.Lucio.stock_ventas.repository;

import com.Lucio.stock_ventas.entity.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
}