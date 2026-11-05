package com.Lucio.stock_ventas.dto;

import com.Lucio.stock_ventas.entity.EstadoVenta;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.List;

public class VentaDTO {
    private EstadoVenta estado;
    @PositiveOrZero
    private BigDecimal montoPagado = BigDecimal.ZERO;
    @NotEmpty @Valid
    private List<DetalleVentaDTO> detalles;

    public VentaDTO() { }
    public VentaDTO(EstadoVenta estado, BigDecimal montoPagado, List<DetalleVentaDTO> detalles) {
        this.estado = estado;
        this.montoPagado = montoPagado;
        this.detalles = detalles;
    }
    public EstadoVenta getEstado() { return estado; }
    public void setEstado(EstadoVenta estado) { this.estado = estado; }
    public BigDecimal getMontoPagado() { return montoPagado; }
    public void setMontoPagado(BigDecimal montoPagado) { this.montoPagado = montoPagado; }
    public List<DetalleVentaDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVentaDTO> detalles) { this.detalles = detalles; }
}
