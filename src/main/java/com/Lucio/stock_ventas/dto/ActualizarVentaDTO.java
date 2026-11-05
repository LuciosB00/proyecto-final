package com.Lucio.stock_ventas.dto;

import com.Lucio.stock_ventas.entity.EstadoVenta;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class ActualizarVentaDTO {
    @NotNull
    private EstadoVenta estado;
    @NotNull @PositiveOrZero
    private BigDecimal montoPagado;

    public ActualizarVentaDTO() { }
    public ActualizarVentaDTO(EstadoVenta estado, BigDecimal montoPagado) {
        this.estado = estado;
        this.montoPagado = montoPagado;
    }
    public EstadoVenta getEstado() { return estado; }
    public void setEstado(EstadoVenta estado) { this.estado = estado; }
    public BigDecimal getMontoPagado() { return montoPagado; }
    public void setMontoPagado(BigDecimal montoPagado) { this.montoPagado = montoPagado; }
}
