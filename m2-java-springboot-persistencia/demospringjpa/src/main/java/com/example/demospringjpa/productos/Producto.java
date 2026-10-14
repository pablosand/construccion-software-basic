package com.example.demospringjpa.productos;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;

/**
 * Entidad que representa un producto del catálogo.
 *
 * <p>Su identificador se genera automáticamente y almacena el nombre,
 * el precio y la descripción del producto.</p>
 */
@Entity
public class Producto {
    /** Identificador único generado por la base de datos. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /** Nombre del producto. */
    private String nombre;
    /** Precio del producto. */
    private BigDecimal precio;
    /** Descripción del producto. */
    private String descripcion;

    /** Constructor requerido por JPA. */
    public Producto () {
    }

    /**
     * Crea un producto con sus datos principales.
     *
     * @param nombre nombre del producto
     * @param precio precio del producto
     * @param descripcion descripción del producto
     */
    public Producto (String nombre, BigDecimal precio, String descripcion) {
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
    }

    /** @return identificador único del producto */
    public Long getId() {
        return id;
    }

    /** @return nombre del producto */
    public String getNombre() {
        return nombre;
    }

    /** @return precio del producto */
    public BigDecimal getPrecio() {
        return precio;
    }

    /** @return descripción del producto */
    public String getDescripcion() {
        return descripcion;
    }

    /** @param nombre nuevo nombre del producto */
    public void setNombre (String nombre) {
        this.nombre = nombre;
    }

    /** @param precio nuevo precio del producto */
    public void setPrecio (BigDecimal precio) {
        this.precio = precio;
    }

    /** @param descripcion nueva descripción del producto */
    public void setDescripcion (String descripcion) {
        this.descripcion = descripcion;
    }
}