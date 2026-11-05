package com.Lucio.stock_ventas.dto;

import com.Lucio.stock_ventas.entity.Producto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class ProductoDTO {
    @NotBlank
    private String nombre;
    @NotNull @Positive
    private BigDecimal precio;
    @NotNull @PositiveOrZero
    private Integer stock;

    public ProductoDTO() { }
    public ProductoDTO(String nombre, BigDecimal precio, Integer stock) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Producto toEntity() {
        return new Producto(nombre, precio, stock);
    }
}
