package com.Lucio.stock_ventas.repository;

import com.Lucio.stock_ventas.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Long> {
}