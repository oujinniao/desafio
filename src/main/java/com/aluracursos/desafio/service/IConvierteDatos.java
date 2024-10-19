package com.aluracursos.desafio.service;

public interface IConvierteDatos {

    //la T indica datos de tipo generico
    <T>T obtenerDatos(String json, Class <T> clase);
}
