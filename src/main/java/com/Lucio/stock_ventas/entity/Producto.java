package com.Lucio.stock_ventas.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private Double precio;

    private Integer stock;

    public Object getNombre() {
        return this.nombre;
    }
    public void setNombre(Object nombre) {
        this.nombre = nombre.toString();
    }

    public Object getPrecio() {
        return this.precio;
    }
    public void setPrecio(Object precio) {
        this.precio = (Double) precio;
    }

    public Object getStock() {
        return this.stock;
    }
    public void setStock(Object stock) {
        this.stock = (Integer) stock;
    }
}