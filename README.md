# Proyecto final: sistema de stock y ventas

API REST construida con Java 21, Spring Boot, Spring Data JPA y PostgreSQL/H2.

## Ejecutar

Por defecto se usa una base H2 en memoria, por lo que no hace falta instalar una base de datos:

```bash
./mvnw spring-boot:run
```

Para PostgreSQL, configura las variables `DB_URL`, `DB_USER`, `DB_PASSWORD` y `DB_DRIVER` antes de iniciar. Ejemplo: `DB_URL=jdbc:postgresql://localhost:5432/stock_ventas`, `DB_USER=postgres`, `DB_DRIVER=org.postgresql.Driver`.

Ejecutar pruebas: `./mvnw test`.

## API

- `GET /api/productos` — listar productos activos.
- `GET /api/productos/{id}` — consultar producto.
- `GET /api/productos/stock-bajo` — productos con stock menor o igual a 5.
- `POST /api/productos` — crear: `{"nombre":"Café","precio":1200.00,"stock":10}`.
- `PUT /api/productos/{id}` — editar nombre, precio y stock.
- `DELETE /api/productos/{id}` — desactivar sin borrar el historial.
- `GET /api/ventas?producto=cafe&estado=DEBE&desde=2026-01-01&hasta=2026-12-31` — historial con filtros opcionales.
- `GET /api/ventas/{id}` — consultar venta con sus detalles.
- `POST /api/ventas` — registrar venta y descontar stock en una transacción: `{"estado":"EN_ESPERA","montoPagado":0,"detalles":[{"productoId":1,"cantidad":2}]}`.
- `PATCH /api/ventas/{id}/pago` — actualizar estado/pago: `{"estado":"VENDIDO","montoPagado":2400.00}`.
- `GET /api/reportes` — totales, deuda, cantidad de ventas, producto más vendido, ventas por mes/estado y stock bajo.

Los precios y montos utilizan `BigDecimal`; el stock se bloquea durante el registro de ventas para evitar que dos operaciones simultáneas vendan las mismas unidades.
