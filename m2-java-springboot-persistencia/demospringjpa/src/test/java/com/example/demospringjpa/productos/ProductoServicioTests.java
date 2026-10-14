package com.example.demospringjpa.productos;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ProductoServicioTests {
    @Mock
    private ProductoRepositorio repositorio;

    private ProductoServicio servicio;

    @BeforeEach
    void prepararServicio() {
        servicio = new ProductoServicio(repositorio);
    }

    @Test
    void listarProductos() {
        List<Producto> productos = List.of(new Producto("Original", BigDecimal.ONE, "Detalle"));
        when(repositorio.findAll()).thenReturn(productos);

        assertSame(productos, servicio.listarProductos());
    }

    @Test
    void guardarProductoNuevo() {
        Producto producto = new Producto("Nuevo", BigDecimal.TEN, "Detalle");
        when(repositorio.save(producto)).thenReturn(producto);

        assertSame(producto, servicio.guardarProducto(producto));
        verify(repositorio).save(producto);
    }

    @Test
    void buscarProductoExistente() {
        Producto producto = new Producto("Original", BigDecimal.ONE, "Detalle");
        when(repositorio.findById(1L)).thenReturn(Optional.of(producto));

        assertSame(producto, servicio.buscarProducto(1L));
    }

    @Test
    void actualizarModificaLaEntidadExistente() {
        Producto existente = new Producto("Original", BigDecimal.ONE, "Detalle");
        Producto datos = new Producto("Modificado", BigDecimal.TEN, "Nuevo detalle");
        when(repositorio.findById(1L)).thenReturn(Optional.of(existente));
        when(repositorio.save(existente)).thenReturn(existente);

        assertSame(existente, servicio.actualizarProducto(1L, datos));
        assertEquals("Modificado", existente.getNombre());
        assertEquals(BigDecimal.TEN, existente.getPrecio());
        assertEquals("Nuevo detalle", existente.getDescripcion());
        verify(repositorio).save(existente);
        verify(repositorio, never()).save(datos);
    }

    @Test
    void eliminarProductoExistente() {
        Producto producto = new Producto("Original", BigDecimal.ONE, "Detalle");
        when(repositorio.findById(1L)).thenReturn(Optional.of(producto));

        servicio.eliminarProducto(1L);

        verify(repositorio).delete(producto);
    }

    @Test
    void buscarProductoInexistenteDevuelve404() {
        when(repositorio.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> servicio.buscarProducto(1L));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
    }

    @Test
    void actualizarProductoInexistenteNoInsertaUnRegistro() {
        when(repositorio.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> servicio.actualizarProducto(1L, new Producto()));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        verify(repositorio, never()).save(any(Producto.class));
    }

    @Test
    void eliminarProductoInexistenteDevuelve404() {
        when(repositorio.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> servicio.eliminarProducto(1L));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        verify(repositorio, never()).delete(any(Producto.class));
    }
}
