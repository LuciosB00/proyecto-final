
package com.Lucio.stock_ventas.repository;

import com.Lucio.stock_ventas.entity.EstadoVenta;
import com.Lucio.stock_ventas.entity.Venta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    // Obtener todas las ventas ordenadas por fecha descendente
    @EntityGraph(attributePaths = {"detalles", "detalles.producto"})
    List<Venta> findAllByOrderByFechaDesc();

    // Obtener ventas filtradas por estado
    @EntityGraph(attributePaths = {"detalles", "detalles.producto"})
    List<Venta> findByEstadoOrderByFechaDesc(EstadoVenta estado);

    // Buscar una venta por ID cargando sus detalles y productos
    @EntityGraph(attributePaths = {"detalles", "detalles.producto"})
    Optional<Venta> findConDetallesById(Long id);
}
