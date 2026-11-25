
package com.Lucio.stock_ventas.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private boolean activo = true;

    public Producto() {
    }

    public Producto(String nombre, BigDecimal precio, Integer stock) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.activo = true;
    }

    public Long getId() {return id;}

    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}

    public BigDecimal getPrecio() {return precio;}
    public void setPrecio(BigDecimal precio) {this.precio = precio;}

    public Integer getStock() {return stock;}
    public void setStock(Integer stock) {this.stock = stock;}

    public boolean getActivo() {return activo;}
    public void setActivo(boolean activo) {this.activo = activo;}
}
