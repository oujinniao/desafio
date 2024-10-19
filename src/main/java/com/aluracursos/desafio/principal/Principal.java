package com.aluracursos.desafio.principal;

import com.aluracursos.desafio.model.Datos;
import com.aluracursos.desafio.model.DatosLibros;
import com.aluracursos.desafio.service.ConsumoAPI;
import com.aluracursos.desafio.service.ConvierteDatos;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private Scanner teclado = new Scanner(System.in);

    public void muestraElMenu() {
        var json = consumoAPI.obtenerDatos(URL_BASE);
        System.out.println(json);
        var datos = conversor.obtenerDatos(json, Datos.class);
        System.out.println(datos);

        // GENERACION DE LOS LIBROS MAS DESCARGADOS
        System.out.println("_________________________________________________");
        System.out.println("LOS LIBROS MAS DESCARGADOS DE LA PAGINA GUTENDEX ");
        System.out.println("-------------------------------------------------");

        // Usar un contador para numerar los libros
        final int[] contador = {1}; // Array que permite modificar dentro de la función lambda

        datos.resultados().stream()
                .sorted(Comparator.comparing(DatosLibros::numeroDeDescargas).reversed())
                .limit(10)
                .map(l -> l.titulo().toUpperCase())
                .forEachOrdered(titulo -> {
                    System.out.println(contador[0] + " .- " + titulo);
                    contador[0]++;
                });

        // METODO QUE SOLICITA AL USUARIO INGRESAR UN NOMBRE DEL LIBRO QUE BUSCA
        System.out.println("*** INGRESA EL NOMBRE DEL LIBRO QUE BUSCAS ***");
        
        var tituloLibro = teclado.nextLine();
        json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + tituloLibro.replace(" ", "+"));
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        Optional<DatosLibros> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();
        if (libroBuscado.isPresent()) {
            System.out.println("** LIBRO SOLICITADO FUÉ ENCONTRADO");
            System.out.println(libroBuscado.get());
        } else {
            System.out.println("*** LO SENTIMOS, EL LIBRO SOLICITADO NO FUÉ ENCONTRADO ***");
            System.out.println("**********************************************************");
        }

        // METODOS DE ESTADISTICAS
        DoubleSummaryStatistics est = datos.resultados().stream()
                .filter(d -> d.numeroDeDescargas() > 0)
                .collect(Collectors.summarizingDouble(DatosLibros::numeroDeDescargas));
        System.out.println("****************************************************");
        System.out.println("CANTIDAD PROMEDIO DE DESCARGAS: " + est.getAverage());
        System.out.println("NUMERO MAXIMA DE DESCARGAS: "+est.getMax());
        System.out.println("CANTIDAD MINIMA DE DESCARGAS FUÉ DE: "+est.getMin());
        System.out.println("CANTIDAD MAXIMA DE SUMAS FUE DE : " + est.getCount());
        System.out.println("****************************************************");

    }
}