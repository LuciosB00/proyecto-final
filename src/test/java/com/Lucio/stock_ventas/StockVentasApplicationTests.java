
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
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class StockVentasApplicationTests {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @BeforeEach
    void limpiarDatos() {
        ventaRepository.deleteAll();
        productoRepository.deleteAll();
    }

    // --------------------------------------------------
    // 1. CONTEXTO
    // --------------------------------------------------

    @Test
    void contextoCarga() {
        assertThat(productoService).isNotNull();
        assertThat(ventaService).isNotNull();
    }

    // --------------------------------------------------
    // 2. PRODUCTOS
    // --------------------------------------------------

    @Test
    void registrarProductoGuardaCorrectamenteSusDatos() {

        Producto producto = productoService.guardar(
                new ProductoDTO(
                        "Café",
                        new BigDecimal("12.50"),
                        8
                )
        );

        Producto guardado = productoRepository
                .findById(producto.getId())
                .orElseThrow();

        assertThat(guardado.getNombre()).isEqualTo("Café");
        assertThat(guardado.getPrecio()).isEqualByComparingTo("12.50");
        assertThat(guardado.getStock()).isEqualTo(8);
        assertThat(guardado.getActivo()).isTrue();
    }

    @Test
    void obtenerProductosSoloDevuelveProductosActivos() {

        Producto activo = productoService.guardar(
                new ProductoDTO("Café", new BigDecimal("12.50"), 8)
        );

        Producto inactivo = productoService.guardar(
                new ProductoDTO("Té", new BigDecimal("5.00"), 4)
        );

        productoService.desactivar(inactivo.getId());

        List<Producto> productos = productoService.obtenerTodos();

        assertThat(productos)
                .extracting(Producto::getId)
                .contains(activo.getId())
                .doesNotContain(inactivo.getId());
    }

    @Test
    void desactivarProductoNoLoEliminaDeLaBaseDeDatos() {

        Producto producto = productoService.guardar(
                new ProductoDTO("Leche", new BigDecimal("4.00"), 3)
        );

        productoService.desactivar(producto.getId());

        Producto encontrado = productoRepository
                .findById(producto.getId())
                .orElseThrow();

        assertThat(encontrado.getActivo()).isFalse();
        assertThat(productoService.obtenerTodos()).isEmpty();
    }

    // --------------------------------------------------
    // 3. REGISTRO DE VENTAS
    // --------------------------------------------------

    @Test
    void registrarVentaEnEsperaNoDescuentaStockYCalculaTotal() {

        Producto producto = productoService.guardar(
                new ProductoDTO("Café", new BigDecimal("12.50"), 8)
        );

        Venta venta = ventaService.guardar(
                new VentaDTO(
                        EstadoVenta.EN_ESPERA,
                        new BigDecimal("10.00"),
                        List.of(new DetalleVentaDTO(producto.getId(), 2))
                )
        );

        assertThat(venta.getEstado()).isEqualTo(EstadoVenta.EN_ESPERA);
        assertThat(venta.getTotal()).isEqualByComparingTo("25.00");
        assertThat(venta.getMontoPagado()).isEqualByComparingTo("10.00");
        assertThat(venta.getSaldoPendiente()).isEqualByComparingTo("15.00");

        Producto actualizado = productoRepository
                .findById(producto.getId())
                .orElseThrow();

        assertThat(actualizado.getStock()).isEqualTo(8);
    }

    @Test
    void ventaEnEsperaPuedeRegistrarCantidadSuperiorAlStock() {

        Producto producto = productoService.guardar(
                new ProductoDTO("Té", new BigDecimal("5.00"), 1)
        );

        Venta venta = ventaService.guardar(
                new VentaDTO(
                        EstadoVenta.EN_ESPERA,
                        BigDecimal.ZERO,
                        List.of(new DetalleVentaDTO(producto.getId(), 5))
                )
        );

        assertThat(venta.getTotal()).isEqualByComparingTo("25.00");

        assertThat(productoRepository
                .findById(producto.getId())
                .orElseThrow()
                .getStock()).isEqualTo(1);
    }

    @Test
    void noPermiteRegistrarVentaSinDetalles() {

        assertThatThrownBy(() ->
                ventaService.guardar(
                        new VentaDTO(
                                EstadoVenta.EN_ESPERA,
                                BigDecimal.ZERO,
                                List.of()
                        )
                )
        ).isInstanceOf(IllegalArgumentException.class);

        assertThat(ventaRepository.count()).isZero();
    }

    @Test
    void noPermiteRegistrarVentaConProductoInexistente() {

        assertThatThrownBy(() ->
                ventaService.guardar(
                        new VentaDTO(
                                EstadoVenta.EN_ESPERA,
                                BigDecimal.ZERO,
                                List.of(new DetalleVentaDTO(999L, 2))
                        )
                )
        ).isInstanceOf(NoSuchElementException.class);

        assertThat(ventaRepository.count()).isZero();
    }

    @Test
    void noPermiteRegistrarVentaConProductoInactivo() {

        Producto producto = productoService.guardar(
                new ProductoDTO("Leche", new BigDecimal("4.00"), 3)
        );

        productoService.desactivar(producto.getId());

        assertThatThrownBy(() ->
                ventaService.guardar(
                        new VentaDTO(
                                EstadoVenta.EN_ESPERA,
                                BigDecimal.ZERO,
                                List.of(new DetalleVentaDTO(producto.getId(), 1))
                        )
                )
        ).isInstanceOf(NoSuchElementException.class);

        assertThat(ventaRepository.count()).isZero();
    }

    // --------------------------------------------------
    // 4. VALIDACIONES DE CANTIDAD Y PAGO
    // --------------------------------------------------

    @Test
    void noPermiteCantidadCero() {

        Producto producto = productoService.guardar(
                new ProductoDTO("Café", new BigDecimal("12.50"), 8)
        );

        assertThatThrownBy(() ->
                ventaService.guardar(
                        new VentaDTO(
                                EstadoVenta.EN_ESPERA,
                                BigDecimal.ZERO,
                                List.of(new DetalleVentaDTO(producto.getId(), 0))
                        )
                )
        ).isInstanceOf(IllegalArgumentException.class);

        assertThat(ventaRepository.count()).isZero();
    }

    @Test
    void noPermiteCantidadNegativa() {

        Producto producto = productoService.guardar(
                new ProductoDTO("Café", new BigDecimal("12.50"), 8)
        );

        assertThatThrownBy(() ->
                ventaService.guardar(
                        new VentaDTO(
                                EstadoVenta.EN_ESPERA,
                                BigDecimal.ZERO,
                                List.of(new DetalleVentaDTO(producto.getId(), -2))
                        )
                )
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void noPermiteMontoPagadoNegativo() {

        Producto producto = productoService.guardar(
                new ProductoDTO("Café", new BigDecimal("12.50"), 8)
        );

        assertThatThrownBy(() ->
                ventaService.guardar(
                        new VentaDTO(
                                EstadoVenta.EN_ESPERA,
                                new BigDecimal("-5.00"),
                                List.of(new DetalleVentaDTO(producto.getId(), 1))
                        )
                )
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void noPermiteMontoPagadoMayorAlTotal() {

        Producto producto = productoService.guardar(
                new ProductoDTO("Café", new BigDecimal("12.50"), 8)
        );

        assertThatThrownBy(() ->
                ventaService.guardar(
                        new VentaDTO(
                                EstadoVenta.EN_ESPERA,
                                new BigDecimal("20.00"),
                                List.of(new DetalleVentaDTO(producto.getId(), 1))
                        )
                )
        ).isInstanceOf(IllegalArgumentException.class);

        assertThat(ventaRepository.count()).isZero();

        assertThat(productoRepository
                .findById(producto.getId())
                .orElseThrow()
                .getStock()).isEqualTo(8);
    }

    @Test
    void noPermiteCrearVentaDirectamenteEnPedido() {

        Producto producto = productoService.guardar(
                new ProductoDTO("Café", new BigDecimal("12.50"), 8)
        );

        assertThatThrownBy(() ->
                ventaService.guardar(
                        new VentaDTO(
                                EstadoVenta.PEDIDO,
                                BigDecimal.ZERO,
                                List.of(new DetalleVentaDTO(producto.getId(), 1))
                        )
                )
        ).isInstanceOf(IllegalArgumentException.class);

        assertThat(ventaRepository.count()).isZero();

        assertThat(productoRepository
                .findById(producto.getId())
                .orElseThrow()
                .getStock()).isEqualTo(8);
    }
}
