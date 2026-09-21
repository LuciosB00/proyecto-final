package com.Lucio.stock_ventas.dto;

import com.Lucio.stock_ventas.entity.EstadoVenta;

import java.util.List;

public class VentaDTO {

    private EstadoVenta estado;
    private Double montoPagado;
    private List<DetalleVentaDTO> detalles;

    public VentaDTO() {
    }

    public VentaDTO(EstadoVenta estado, Double montoPagado,
                    List<DetalleVentaDTO> detalles) {
        this.estado = estado;
        this.montoPagado = montoPagado;
        this.detalles = detalles;
    }

    public EstadoVenta getEstado() {
        return estado;
    }

    public void setEstado(EstadoVenta estado) {
        this.estado = estado;
    }

    public Double getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(Double montoPagado) {
        this.montoPagado = montoPagado;
    }

    public List<DetalleVentaDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVentaDTO> detalles) {
        this.detalles = detalles;
    }
}