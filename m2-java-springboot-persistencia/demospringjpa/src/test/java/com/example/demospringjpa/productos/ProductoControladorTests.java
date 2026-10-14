package com.example.demospringjpa.productos;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class ProductoControladorTests {
    @Mock
    private ProductoServicio servicio;

    private MockMvc mvc;

    @BeforeEach
    void prepararControlador() {
        ClassLoaderTemplateResolver plantillas = new ClassLoaderTemplateResolver();
        plantillas.setPrefix("templates/");
        plantillas.setSuffix(".html");
        plantillas.setCharacterEncoding("UTF-8");
        SpringTemplateEngine motor = new SpringTemplateEngine();
        motor.setTemplateResolver(plantillas);
        ThymeleafViewResolver vistas = new ThymeleafViewResolver();
        vistas.setTemplateEngine(motor);
        vistas.setCharacterEncoding("UTF-8");
        mvc = MockMvcBuilders.standaloneSetup(new ProductoControlador(servicio))
                .setViewResolvers(vistas).build();
    }

    @Test
    void listadoIncluyeAccionesPorRegistro() throws Exception {
        Producto producto = productoExistente();
        when(servicio.listarProductos()).thenReturn(List.of(producto));

        mvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"/css/productos.css\"")))
                .andExpect(content().string(containsString("href=\"/productos/1/editar\"")))
                .andExpect(content().string(containsString("action=\"/productos/1/eliminar\"")))
                .andExpect(content().string(containsString("method=\"post\"")))
                .andExpect(content().string(containsString("return confirm(")));
    }

    @Test
    void listadoVacioMuestraUnMensaje() throws Exception {
        when(servicio.listarProductos()).thenReturn(List.of());

        mvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Aún no hay productos")))
                .andExpect(content().string(containsString("0 productos")));
    }

    @Test
    void formularioNuevoMantieneLaCreacion() throws Exception {
        mvc.perform(get("/productos/nuevo"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"/css/productos.css\"")))
                .andExpect(content().string(containsString("Agregar producto")))
                .andExpect(content().string(containsString("action=\"/productos\"")));
    }

    @Test
    void formularioEdicionRecuperaLosDatosYLaRutaDeActualizacion() throws Exception {
        Producto producto = productoExistente();
        when(servicio.buscarProducto(1L)).thenReturn(producto);

        mvc.perform(get("/productos/1/editar"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/formulario"))
                .andExpect(model().attribute("producto", producto))
                .andExpect(content().string(containsString("Editar producto")))
                .andExpect(content().string(containsString("action=\"/productos/1\"")))
                .andExpect(content().string(containsString("value=\"Original\"")))
                .andExpect(content().string(containsString("value=\"10\"")))
                .andExpect(content().string(containsString("Detalle")));
    }

    @Test
    void crearProductoRedirigeAlListado() throws Exception {
        mvc.perform(post("/productos")
                        .param("nombre", "Nuevo").param("precio", "10")
                        .param("descripcion", "Detalle"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos"));

        verify(servicio).guardarProducto(any(Producto.class));
    }

    @Test
    void actualizarEnviaLosDatosYElIdentificadorAlServicio() throws Exception {
        mvc.perform(post("/productos/1")
                        .param("nombre", "Modificado").param("precio", "25.50")
                        .param("descripcion", "Nuevo detalle"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos"));

        ArgumentCaptor<Producto> datos = ArgumentCaptor.forClass(Producto.class);
        verify(servicio).actualizarProducto(eq(1L), datos.capture());
        assertEquals("Modificado", datos.getValue().getNombre());
        assertEquals(new BigDecimal("25.50"), datos.getValue().getPrecio());
        assertEquals("Nuevo detalle", datos.getValue().getDescripcion());
    }

    @Test
    void eliminarRedirigeAlListado() throws Exception {
        mvc.perform(post("/productos/1/eliminar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos"));

        verify(servicio).eliminarProducto(1L);
    }

    @Test
    void editarProductoInexistenteDevuelve404() throws Exception {
        when(servicio.buscarProducto(1L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mvc.perform(get("/productos/1/editar")).andExpect(status().isNotFound());
    }

    @Test
    void actualizarProductoInexistenteDevuelve404() throws Exception {
        when(servicio.actualizarProducto(eq(1L), any(Producto.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mvc.perform(post("/productos/1")
                        .param("nombre", "Modificado").param("precio", "10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminarProductoInexistenteDevuelve404() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                .when(servicio).eliminarProducto(1L);

        mvc.perform(post("/productos/1/eliminar")).andExpect(status().isNotFound());
    }

    private Producto productoExistente() {
        Producto producto = spy(new Producto("Original", BigDecimal.TEN, "Detalle"));
        when(producto.getId()).thenReturn(1L);
        return producto;
    }
}
