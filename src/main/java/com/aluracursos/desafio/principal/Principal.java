package com.aluracursos.desafio.principal;

import com.aluracursos.desafio.model.Datos;
import com.aluracursos.desafio.model.DatosLibros;
import com.aluracursos.desafio.service.ConsumoAPI;
import com.aluracursos.desafio.service.ConvierteDatos;
import com.aluracursos.desafio.model.DatosAutor;

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

        // Mostrar opciones al usuario
        System.out.println("-------------------------------------------------");
        System.out.println("Seleccione una opción:");
        System.out.println("1. Mostrar los 10 libros más descargados");
        System.out.println("2. Mostrar los autores de los 10 libros más descargados");
        System.out.println("3. Buscar un libro por título");
        System.out.println("-------------------------------------------------");

        int opcion = teclado.nextInt();
        teclado.nextLine(); // Limpiar el buffer

        switch (opcion) {
            case 1:
                mostrarTop10Libros(datos);
                break;
            case 2:
                mostrarAutoresTop10Libros(datos);
                break;
            case 3:
                buscarLibroPorTitulo();
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }

    private void mostrarTop10Libros(Datos datos) {
        System.out.println("LOS LIBROS MAS DESCARGADOS DE LA PAGINA GUTENDEX ");
        System.out.println("-------------------------------------------------");

        final int[] contador = {1};
        datos.resultados().stream()
                .sorted(Comparator.comparing(DatosLibros::numeroDeDescargas).reversed())
                .limit(10)
                .map(l -> l.titulo().toUpperCase())
                .forEachOrdered(titulo -> {
                    System.out.println(contador[0] + " .- " + titulo);
                    contador[0]++;
                });
    }

    private void mostrarAutoresTop10Libros(Datos datos) {
        System.out.println("AUTORES DE LOS 10 LIBROS MAS DESCARGADOS");
        System.out.println("-------------------------------------------------");

        final int[] contador = {1};
        datos.resultados().stream()
                .sorted(Comparator.comparing(DatosLibros::numeroDeDescargas).reversed())
                .limit(10)
                .forEachOrdered(libro -> {
                    String autores = libro.autor().stream()
                            .map(DatosAutor::nombre) // Usamos el método nombre() de DatosAutor
                            .collect(Collectors.joining(", "));
                    System.out.println(contador[0] + " .- " + libro.titulo().toUpperCase() + " - Autores: " + autores);
                    contador[0]++;
                });
    }

    private void buscarLibroPorTitulo() {
        System.out.println("*** INGRESA EL NOMBRE DEL LIBRO QUE BUSCAS ***");
        var tituloLibro = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + tituloLibro.replace(" ", "+"));
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        Optional<DatosLibros> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();
        if (libroBuscado.isPresent()) {
            System.out.println("** LIBRO SOLICITADO FUÉ ENCONTRADO");
            System.out.println(libroBuscado.get());
        } else {
            System.out.println("*** LO SENTIMOS, EL LIBRO SOLICITADO NO FUÉ ENCONTRADO ***");
        }
    }
}