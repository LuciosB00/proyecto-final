package com.Lucio.stock_ventas.service;

import com.Lucio.stock_ventas.dto.ReporteDTO;
import com.Lucio.stock_ventas.entity.DetalleVenta;
import com.Lucio.stock_ventas.entity.EstadoVenta;
import com.Lucio.stock_ventas.entity.Producto;
import com.Lucio.stock_ventas.entity.Venta;
import com.Lucio.stock_ventas.repository.ProductoRepository;
import com.Lucio.stock_ventas.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@Transactional(readOnly = true)
public class ReporteService {
    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private static final DateTimeFormatter MES_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    public ReporteService(VentaRepository ventaRepository, ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }

    public ReporteDTO obtenerReporte() {
        List<Venta> ventas = ventaRepository.findAllByOrderByFechaDesc();
        BigDecimal totalVendido = BigDecimal.ZERO;
        BigDecimal totalAdeudado = BigDecimal.ZERO;
        Map<YearMonth, BigDecimal> ventasMes = new TreeMap<>(Comparator.reverseOrder());
        Map<EstadoVenta, Long> ventasEstado = new EnumMap<>(EstadoVenta.class);
        Map<Long, ProductoVendido> productos = new java.util.HashMap<>();

        for (Venta venta : ventas) {
            totalVendido = totalVendido.add(venta.getTotal());
            totalAdeudado = totalAdeudado.add(venta.getSaldoPendiente());
            YearMonth mes = YearMonth.from(venta.getFecha());
            ventasMes.merge(mes, venta.getTotal(), BigDecimal::add);
            ventasEstado.merge(venta.getEstado(), 1L, Long::sum);
            for (DetalleVenta detalle : venta.getDetalles()) {
                Producto p = detalle.getProducto();
                productos.compute(p.getId(), (id, actual) -> actual == null
                        ? new ProductoVendido(p.getNombre(), detalle.getCantidad())
                        : new ProductoVendido(actual.nombre(), actual.cantidad() + detalle.getCantidad()));
            }
        }

        String masVendido = productos.values().stream()
                .max(Comparator.comparingInt(ProductoVendido::cantidad))
                .map(p -> p.nombre() + " (" + p.cantidad() + " vendidos)")
                .orElse("Sin ventas");
        List<String> resumenMes = ventasMes.entrySet().stream().limit(12)
                .map(e -> MES_FORMAT.format(e.getKey()) + ": " + e.getValue()).toList();
        List<String> resumenEstado = java.util.Arrays.stream(EstadoVenta.values())
                .map(e -> e.name() + ": " + ventasEstado.getOrDefault(e, 0L)).toList();

        return new ReporteDTO(totalVendido, totalAdeudado, ventas.size(), masVendido,
                resumenMes, resumenEstado,
                productoRepository.findByActivoTrueAndStockLessThanEqualOrderByStockAscNombreAsc(5));
    }

    private record ProductoVendido(String nombre, int cantidad) { }
}
