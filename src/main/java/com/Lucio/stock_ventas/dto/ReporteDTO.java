package com.Lucio.stock_ventas.dto;

import com.Lucio.stock_ventas.entity.Producto;

import java.math.BigDecimal;
import java.util.List;

public class ReporteDTO {
    private BigDecimal totalVendido;
    private BigDecimal totalAdeudado;
    private long cantidadVentas;
    private String productoMasVendido;
    private List<String> ventasPorMes;
    private List<String> ventasPorEstado;
    private List<Producto> productosConStockBajo;

    public ReporteDTO(BigDecimal totalVendido, BigDecimal totalAdeudado, long cantidadVentas,
                      String productoMasVendido, List<String> ventasPorMes,
                      List<String> ventasPorEstado, List<Producto> productosConStockBajo) {
        this.totalVendido = totalVendido;
        this.totalAdeudado = totalAdeudado;
        this.cantidadVentas = cantidadVentas;
        this.productoMasVendido = productoMasVendido;
        this.ventasPorMes = ventasPorMes;
        this.ventasPorEstado = ventasPorEstado;
        this.productosConStockBajo = productosConStockBajo;
    }
    public BigDecimal getTotalVendido() { return totalVendido; }
    public BigDecimal getTotalAdeudado() { return totalAdeudado; }
    public long getCantidadVentas() { return cantidadVentas; }
    public String getProductoMasVendido() { return productoMasVendido; }
    public List<String> getVentasPorMes() { return ventasPorMes; }
    public List<String> getVentasPorEstado() { return ventasPorEstado; }
    public List<Producto> getProductosConStockBajo() { return productosConStockBajo; }
}
