package com.example.demospringjpa.productos;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio para consultar y persistir entidades {@link Producto}.
 *
 * <p>Hereda de {@link JpaRepository} las operaciones CRUD y de paginación
 * para productos identificados mediante valores {@link Long}.</p>
 */
public interface ProductoRepositorio extends JpaRepository<Producto, Long>
{
}
