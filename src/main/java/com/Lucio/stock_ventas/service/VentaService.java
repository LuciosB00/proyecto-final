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
import java.util.NoSuchElementException;
import com.Lucio.stock_ventas.entity.EstadoEntrega;
import com.Lucio.stock_ventas.entity.ModalidadEntrega;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID de venta no es válido");
        }
        return ventaRepository.findConDetallesById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Venta no encontrada: " + id));
    }


    @Transactional
    public Venta guardar(VentaDTO datos) {
        if (datos.getDetalles() == null || datos.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La venta debe incluir al menos un producto");
        }
        // Validar estado inicial
        EstadoVenta estado = datos.getEstado() == null
                ? EstadoVenta.EN_ESPERA
                : datos.getEstado();
        if (estado != EstadoVenta.EN_ESPERA) {
            throw new IllegalArgumentException("Una venta nueva debe crearse en estado EN_ESPERA");}
        // Validar modalidad de entrega
        ModalidadEntrega modalidad = datos.getModalidadEntrega();
        if (modalidad == null) {
            throw new IllegalArgumentException("La modalidad de entrega es obligatoria");}
        BigDecimal costoEnvio = datos.getCostoEnvio() == null
                ? BigDecimal.ZERO
                : datos.getCostoEnvio();
        if (costoEnvio.signum() < 0) {
            throw new IllegalArgumentException(
                    "El costo de envío no puede ser negativo");
        }
        String direccion = datos.getDireccionEntrega();
        if (modalidad == ModalidadEntrega.RETIRO_LOCAL) {
            if (costoEnvio.signum() > 0) {throw new IllegalArgumentException("El retiro local no puede tener costo de envío");
            }
            direccion = null;
        } else if (modalidad == ModalidadEntrega.DELIVERY) {
            if (direccion == null || direccion.isBlank()) {
                throw new IllegalArgumentException("La dirección es obligatoria para el delivery");
            }
            direccion = direccion.trim();
        }
        // Validar monto pagado
        BigDecimal montoPagado = datos.getMontoPagado() == null
                ? BigDecimal.ZERO
                : datos.getMontoPagado();
        if (montoPagado.signum() < 0) {
            throw new IllegalArgumentException("El monto pagado no puede ser negativo");
        }
        // Agrupar productos repetidos
        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        for (DetalleVentaDTO linea : datos.getDetalles()) {
            if (linea.getProductoId() == null
                    || linea.getProductoId() <= 0
                    || linea.getCantidad() == null
                    || linea.getCantidad() <= 0) {
                throw new IllegalArgumentException("Cada línea debe tener producto y cantidad mayores que cero");
            }
            cantidades.merge(
                    linea.getProductoId(),
                    linea.getCantidad(),
                    Math::addExact
            );
        }
        // Crear venta
        Venta venta = new Venta();
        venta.setFecha(LocalDateTime.now());
        venta.setEstado(EstadoVenta.EN_ESPERA);
        venta.setActivo(true);
        venta.setMontoPagado(montoPagado);
        venta.setModalidadEntrega(modalidad);
        venta.setCostoEnvio(costoEnvio);
        venta.setDireccionEntrega(direccion);
        // La entrega todavía no comenzó
        venta.setEstadoEntrega(EstadoEntrega.NO_APLICA);
        // Crear detalles y calcular subtotal de productos
        BigDecimal subtotalProductos = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> linea : cantidades.entrySet()) {
            Producto producto = productoRepository
                    .findByIdAndActivoTrue(linea.getKey())
                    .orElseThrow(() -> new NoSuchElementException(
                            "Producto activo no encontrado: " + linea.getKey()
                    ));
            int cantidad = linea.getValue();
            BigDecimal precio = producto.getPrecio();
            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(cantidad));
            venta.agregarDetalle(
                    new DetalleVenta(
                            venta,
                            producto,
                            cantidad,
                            precio,
                            subtotal
                    )
            );
            subtotalProductos = subtotalProductos.add(subtotal);
        }
        // Calcular total incluyendo envío
        BigDecimal total = subtotalProductos.add(costoEnvio);
        if (montoPagado.compareTo(total) > 0) {
            throw new IllegalArgumentException("El monto pagado no puede superar el total de la venta");
        }
        venta.setTotal(total);
        // No se modifica stock en esta etapa
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
