package com.Lucio.stock_ventas.service;

import com.Lucio.stock_ventas.dto.DetalleVentaDTO;
import com.Lucio.stock_ventas.dto.VentaDTO;
import com.Lucio.stock_ventas.entity.DetalleVenta;
import com.Lucio.stock_ventas.entity.EstadoVenta;
import com.Lucio.stock_ventas.entity.Producto;
import com.Lucio.stock_ventas.entity.Venta;
import com.Lucio.stock_ventas.repository.ProductoRepository;
import com.Lucio.stock_ventas.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class VentaService {
    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    public VentaService(VentaRepository ventaRepository, ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }

    public List<Venta> obtenerTodas(String producto, EstadoVenta estado, LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && hasta.isBefore(desde)) {
            throw new IllegalArgumentException("La fecha hasta no puede ser anterior a la fecha desde");
        }
        String filtroProducto = producto == null ? "" : producto.trim().toLowerCase();
        return ventaRepository.findAllByOrderByFechaDesc().stream()
                .filter(v -> estado == null || v.getEstado() == estado)
                .filter(v -> desde == null || !v.getFecha().toLocalDate().isBefore(desde))
                .filter(v -> hasta == null || !v.getFecha().toLocalDate().isAfter(hasta))
                .filter(v -> filtroProducto.isEmpty() || v.getDetalles().stream()
                        .anyMatch(d -> d.getProducto().getNombre().toLowerCase().contains(filtroProducto)))
                .toList();
    }

    public Venta obtenerPorId(Long id) {
        return ventaRepository.findAllByOrderByFechaDesc().stream()
                .filter(v -> v.getId().equals(id)).findFirst()
                .orElseThrow(() -> new java.util.NoSuchElementException("Venta no encontrada"));
    }

    @Transactional
    public Venta guardar(VentaDTO datos) {
        if (datos.getDetalles() == null || datos.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La venta debe incluir al menos un producto");
        }
        EstadoVenta estado = datos.getEstado() == null ? EstadoVenta.EN_ESPERA : datos.getEstado();
        BigDecimal montoPagado = datos.getMontoPagado() == null ? BigDecimal.ZERO : datos.getMontoPagado();
        if (montoPagado.signum() < 0) throw new IllegalArgumentException("El monto pagado no puede ser negativo");

        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        for (DetalleVentaDTO linea : datos.getDetalles()) {
            if (linea.getProductoId() == null || linea.getProductoId() <= 0 || linea.getCantidad() == null || linea.getCantidad() <= 0) {
                throw new IllegalArgumentException("Cada línea debe tener producto y cantidad mayores que cero");
            }
            cantidades.merge(linea.getProductoId(), linea.getCantidad(), Math::addExact);
        }

        Venta venta = new Venta();
        venta.setFecha(LocalDateTime.now());
        venta.setEstado(estado);
        venta.setActivo(true);
        venta.setMontoPagado(montoPagado);
        BigDecimal total = BigDecimal.ZERO;
        List<Producto> productos = new ArrayList<>();

        for (Map.Entry<Long, Integer> linea : cantidades.entrySet()) {
            Producto producto = productoRepository.findActivoByIdForUpdate(linea.getKey())
                    .orElseThrow(() -> new java.util.NoSuchElementException("Producto activo no encontrado: " + linea.getKey()));
            int cantidad = linea.getValue();
            if (producto.getStock() < cantidad) {
                throw new IllegalStateException("Stock insuficiente para el producto: " + producto.getNombre());
            }
            BigDecimal precio = producto.getPrecio();
            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(cantidad));
            venta.agregarDetalle(new DetalleVenta(venta, producto, cantidad, precio, subtotal));
            total = total.add(subtotal);
            producto.setStock(producto.getStock() - cantidad);
            productos.add(producto);
        }
        if (montoPagado.compareTo(total) > 0) {
            throw new IllegalArgumentException("El monto pagado no puede superar el total de la venta");
        }
        venta.setTotal(total);
        productoRepository.saveAll(productos);
        return ventaRepository.save(venta);
    }

    @Transactional
    public Venta actualizarEstadoYPago(Long id, EstadoVenta estado, BigDecimal montoPagado) {
        if (estado == null) throw new IllegalArgumentException("El estado es obligatorio");
        if (montoPagado == null || montoPagado.signum() < 0) throw new IllegalArgumentException("El monto pagado no puede ser negativo");
        Venta venta = obtenerPorId(id);
        if (montoPagado.compareTo(venta.getTotal()) > 0) throw new IllegalArgumentException("El monto pagado no puede superar el total de la venta");
        venta.setEstado(estado);
        venta.setMontoPagado(montoPagado);
        return ventaRepository.save(venta);
    }
}
