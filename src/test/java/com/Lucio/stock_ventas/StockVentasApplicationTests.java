package com.Lucio.stock_ventas;

import com.Lucio.stock_ventas.dto.DetalleVentaDTO;
import com.Lucio.stock_ventas.dto.ProductoDTO;
import com.Lucio.stock_ventas.dto.VentaDTO;
import com.Lucio.stock_ventas.entity.EstadoVenta;
import com.Lucio.stock_ventas.entity.Producto;
import com.Lucio.stock_ventas.entity.Venta;
import com.Lucio.stock_ventas.repository.ProductoRepository;
import com.Lucio.stock_ventas.repository.VentaRepository;
import com.Lucio.stock_ventas.service.ProductoService;
import com.Lucio.stock_ventas.service.VentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class StockVentasApplicationTests {
    @Autowired private ProductoService productoService;
    @Autowired private VentaService ventaService;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private VentaRepository ventaRepository;

    @BeforeEach
    void limpiarDatos() {
        ventaRepository.deleteAll();
        productoRepository.deleteAll();
    }

    @Test
    void contextoCarga() {
        assertThat(productoService).isNotNull();
    }

    @Test
    void registrarVentaDescuentaStockYCalculaTotal() {
        Producto producto = productoService.guardar(new ProductoDTO("Café", new BigDecimal("12.50"), 8));

        Venta venta = ventaService.guardar(new VentaDTO(EstadoVenta.EN_ESPERA, new BigDecimal("10.00"),
                List.of(new DetalleVentaDTO(producto.getId(), 2))));

        assertThat(venta.getTotal()).isEqualByComparingTo("25.00");
        assertThat(venta.getSaldoPendiente()).isEqualByComparingTo("15.00");
        assertThat(productoRepository.findById(producto.getId()).orElseThrow().getStock()).isEqualTo(6);
    }

    @Test
    void noPermiteVentaSinStockYNoModificaExistencias() {
        Producto producto = productoService.guardar(new ProductoDTO("Té", new BigDecimal("5.00"), 1));

        assertThatThrownBy(() -> ventaService.guardar(new VentaDTO(EstadoVenta.EN_ESPERA, BigDecimal.ZERO,
                List.of(new DetalleVentaDTO(producto.getId(), 2)))))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Stock insuficiente");

        assertThat(productoRepository.findById(producto.getId()).orElseThrow().getStock()).isEqualTo(1);
        assertThat(ventaRepository.count()).isZero();
    }

    @Test
    void desactivarProductoLoRetiraDelCatalogoSinEliminarlo() {
        Producto producto = productoService.guardar(new ProductoDTO("Leche", new BigDecimal("4.00"), 3));

        productoService.desactivar(producto.getId());

        assertThat(productoService.obtenerTodos()).isEmpty();
        assertThat(productoRepository.findById(producto.getId()).orElseThrow().isActivo()).isFalse();
    }
}
