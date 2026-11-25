
package com.Lucio.stock_ventas.repository;

import com.Lucio.stock_ventas.entity.Producto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Productos activos ordenados por nombre
    List<Producto> findByActivoTrueOrderByNombreAsc();

    // Productos activos con stock igual o inferior al límite
    List<Producto> findByActivoTrueAndStockLessThanEqualOrderByStockAscNombreAsc(
            Integer limite
    );

    // Buscar un producto activo por ID
    Optional<Producto> findByIdAndActivoTrue(Long id);

    // Bloquear el producto durante operaciones críticas de stock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Producto p WHERE p.id = :id AND p.activo = true")
    Optional<Producto> findActivoByIdForUpdate(@Param("id") Long id);
}
