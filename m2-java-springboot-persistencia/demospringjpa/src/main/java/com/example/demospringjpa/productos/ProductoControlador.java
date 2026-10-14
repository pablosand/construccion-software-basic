package com.example.demospringjpa.productos;

import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador web para las operaciones de gestión de productos.
 *
 * <p>Atiende las solicitudes bajo {@code /productos} y coordina con
 * {@link ProductoServicio} la consulta y el guardado de productos.</p>
 */
@Controller
@RequestMapping("/productos")
public class ProductoControlador {
    private final ProductoServicio servicio;

    /**
     * Crea el controlador con el servicio de productos requerido.
     *
     * @param servicio servicio utilizado para gestionar productos
     */
    public ProductoControlador(ProductoServicio servicio) {
        this.servicio = servicio;
    }

    /**
     * Lista los productos registrados.
     *
     * @param modelo modelo al que se agrega la lista para la vista
     * @return nombre de la vista de listado
     */
    @GetMapping
    public String listarProductos(Model modelo) {
        modelo.addAttribute("productos", servicio.listarProductos());
        return "productos/lista";
    }

    /**
     * Muestra el formulario para crear un producto nuevo.
     *
     * @param modelo modelo al que se agrega un producto inicial
     * @return nombre de la vista del formulario
     */
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model modelo) {
        modelo.addAttribute("producto", new Producto("", BigDecimal.ZERO, ""));
        return "productos/formulario";
    }

    /**
     * Guarda el producto enviado desde el formulario y redirige al listado.
     *
     * @param producto producto recibido del formulario
     * @return ruta de redirección al listado de productos
     */
    @PostMapping
    public String guardarProducto(@ModelAttribute Producto producto) {
        servicio.guardarProducto(producto);
        return "redirect:/productos";
    }

    /**
     * Carga un producto existente en el formulario de edición.
     *
     * @param id identificador del producto
     * @param modelo modelo al que se agrega el producto
     * @return nombre de la vista del formulario
     */
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicion(@PathVariable("id") Long id, Model modelo) {
        modelo.addAttribute("producto", servicio.buscarProducto(id));
        return "productos/formulario";
    }

    /**
     * Actualiza el producto y redirige al listado.
     *
     * @param id identificador del producto que se actualizará
     * @param producto nuevos datos recibidos del formulario
     * @return ruta de redirección al listado
     */
    @PostMapping("/{id}")
    public String actualizarProducto(@PathVariable("id") Long id,
                                     @ModelAttribute Producto producto) {
        servicio.actualizarProducto(id, producto);
        return "redirect:/productos";
    }

    /**
     * Elimina el producto y redirige al listado.
     *
     * @param id identificador del producto que se eliminará
     * @return ruta de redirección al listado
     */
    @PostMapping("/{id}/eliminar")
    public String eliminarProducto(@PathVariable("id") Long id) {
        servicio.eliminarProducto(id);
        return "redirect:/productos";
    }
}
