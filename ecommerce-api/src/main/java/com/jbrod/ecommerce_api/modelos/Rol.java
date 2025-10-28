package com.jbrod.ecommerce_api.modelos;

import jakarta.persistence.*;


@Entity
@Table(name = "rol")
public class Rol {

    // Anotaciones para la llave primaria
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indica que es autoincremental
    @Column(name = "id_rol")
    private Integer id;

    // Mapeo de la columna nombre_rol
    @Column(name = "nombre_rol", nullable = false, unique = true)
    private String nombre;



    // Constructor vacio, requerido por JPA
    public Rol() {
    }

    public Rol(String nombre) {
        this.nombre = nombre;
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}