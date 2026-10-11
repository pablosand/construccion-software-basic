package com.example.demospringboot.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.demospringboot.dominio.Elemento;
import com.example.demospringboot.servicios.ElementoServicio;
import java.util.List;

@Controller
public class Controlador {

    private final ElementoServicio elementoServicio;

    public Controlador(ElementoServicio elementoServicio) {
        this.elementoServicio = elementoServicio;
    }

    @GetMapping("/")
    public String info(Model modelo) {
        modelo.addAttribute("mensaje", "Demo Spring Boot");
        List<Elemento> elementos = elementoServicio.obtenerElementos();
        modelo.addAttribute("elementos", elementos);
        return "info";
    }
}