
package com.Lucio.stock_ventas.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venta")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoVenta estado;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montoPagado = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoEntrega estadoEntrega = EstadoEntrega.NO_APLICA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ModalidadEntrega modalidadEntrega = ModalidadEntrega.RETIRO_LOCAL;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal costoEnvio = BigDecimal.ZERO;

    @Column(length = 500)
    private String direccionEntrega;

    private LocalDateTime fechaEntrega;

    @Column(nullable = false)
    private boolean activo = true;

    @JsonManagedReference
    @OneToMany(
            mappedBy = "venta",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<DetalleVenta> detalles = new ArrayList<>();

    public Venta() {
    }

    public void agregarDetalle(DetalleVenta detalle) {
        if (detalle == null) {
            throw new IllegalArgumentException(
                    "El detalle de venta no puede ser nulo"
            );
        }

        detalles.add(detalle);
        detalle.setVenta(this);
    }

    public void quitarDetalle(DetalleVenta detalle) {
        if (detalle != null && detalles.remove(detalle)) {
            detalle.setVenta(null);
        }
    }

    public BigDecimal getSaldoPendiente() {
        if (total == null || montoPagado == null) {
            return BigDecimal.ZERO;
        }

        return total.subtract(montoPagado).max(BigDecimal.ZERO);
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public EstadoVenta getEstado() {
        return estado;
    }

    public void setEstado(EstadoVenta estado) {
        this.estado = estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }

    public EstadoEntrega getEstadoEntrega() {
        return estadoEntrega;
    }

    public void setEstadoEntrega(EstadoEntrega estadoEntrega) {
        this.estadoEntrega = estadoEntrega;
    }

    public ModalidadEntrega getModalidadEntrega() {
        return modalidadEntrega;
    }

    public void setModalidadEntrega(ModalidadEntrega modalidadEntrega) {
        this.modalidadEntrega = modalidadEntrega;
    }

    public BigDecimal getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(BigDecimal costoEnvio) {
        this.costoEnvio = costoEnvio;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }
}
