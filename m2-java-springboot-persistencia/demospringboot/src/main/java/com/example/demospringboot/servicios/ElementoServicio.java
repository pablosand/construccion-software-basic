package com.example.demospringboot.servicios;

import com.example.demospringboot.dominio.Elemento;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class ElementoServicio {
    public List<Elemento> obtenerElementos() {
        return Arrays.asList(
                new Elemento("Elemento 1"),
                new Elemento("Elemento 2"),
                new Elemento("Elemento 3")
        );
    }
}