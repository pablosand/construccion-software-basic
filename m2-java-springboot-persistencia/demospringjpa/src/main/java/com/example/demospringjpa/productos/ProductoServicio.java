package com.example.demospringjpa.productos;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Servicio que implementa las operaciones de gestión de productos.
 *
 * <p>Usa {@link ProductoRepositorio} para acceder a los datos y ejecuta
 * sus operaciones dentro de transacciones.</p>
 */
@Service
@Transactional
public class ProductoServicio {
    private final ProductoRepositorio repositorio;

    /**
     * Crea el servicio con el repositorio que utilizará para gestionar productos.
     *
     * @param repositorio repositorio de productos
     */
    public ProductoServicio(ProductoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Obtiene todos los productos registrados.
     *
     * @return lista de productos
     */
    @Transactional(readOnly = true)
    public List<Producto> listarProductos() {
        return repositorio.findAll();
    }

    /**
     * Guarda un producto nuevo o actualiza uno existente.
     *
     * @param producto producto que se guardará
     * @return producto guardado
     */
    public Producto guardarProducto(Producto producto) {
        return repositorio.save(producto);
    }

    /**
     * Busca un producto por su identificador.
     *
     * @param id identificador del producto
     * @return producto encontrado
     * @throws ResponseStatusException si el producto no existe (404)
     */
    @Transactional(readOnly = true)
    public Producto buscarProducto(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    /**
     * Actualiza los datos de un producto existente sin cambiar su identificador.
     *
     * @param id identificador del producto que se actualizará
     * @param datos nuevos datos del producto
     * @return producto actualizado
     * @throws ResponseStatusException si el producto no existe (404)
     */
    public Producto actualizarProducto(Long id, Producto datos) {
        Producto producto = buscarProducto(id);
        producto.setNombre(datos.getNombre());
        producto.setPrecio(datos.getPrecio());
        producto.setDescripcion(datos.getDescripcion());
        return repositorio.save(producto);
    }

    /**
     * Elimina un producto existente.
     *
     * @param id identificador del producto que se eliminará
     * @throws ResponseStatusException si el producto no existe (404)
     */
    public void eliminarProducto(Long id) {
        Producto producto = buscarProducto(id);
        repositorio.delete(producto);
    }
}
