
package com.Lucio.stock_ventas.dto;

import com.Lucio.stock_ventas.entity.EstadoVenta;
import com.Lucio.stock_ventas.entity.ModalidadEntrega;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public class VentaDTO {
    private EstadoVenta estado;
    @PositiveOrZero
    private BigDecimal montoPagado = BigDecimal.ZERO;
    @PositiveOrZero
    private BigDecimal costoEnvio = BigDecimal.ZERO;
    private ModalidadEntrega modalidadEntrega = ModalidadEntrega.RETIRO_LOCAL;
    @Size(max = 500)
    private String direccionEntrega;
    @NotEmpty
    @Valid
    private List<DetalleVentaDTO> detalles;

    public VentaDTO() {}
    public VentaDTO(EstadoVenta estado, BigDecimal montoPagado, List<DetalleVentaDTO> detalles) {
        this.estado = estado;
        this.montoPagado = montoPagado;
        this.detalles = detalles;
    }

    public EstadoVenta getEstado() {return estado;}
    public void setEstado(EstadoVenta estado) {this.estado = estado;}
    public BigDecimal getMontoPagado() {return montoPagado;}
    public void setMontoPagado(BigDecimal montoPagado) {this.montoPagado = montoPagado;}
    public BigDecimal getCostoEnvio() {return costoEnvio;}
    public void setCostoEnvio(BigDecimal costoEnvio) {this.costoEnvio = costoEnvio;}
    public ModalidadEntrega getModalidadEntrega() {return modalidadEntrega;}
    public void setModalidadEntrega(ModalidadEntrega modalidadEntrega) {this.modalidadEntrega = modalidadEntrega;}
    public String getDireccionEntrega() {return direccionEntrega;}
    public void setDireccionEntrega(String direccionEntrega) {this.direccionEntrega = direccionEntrega;}
    public List<DetalleVentaDTO> getDetalles() {return detalles;}
    public void setDetalles(List<DetalleVentaDTO> detalles) {this.detalles = detalles;}
}
