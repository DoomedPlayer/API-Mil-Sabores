package com.example.test.model;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="usuario")
@Data


public abstract class Usuario {

    @Id
    @Column(unique = true,length = 13,nullable = false)
    private Long rut;
    @Column(length=1,nullable = false)
    private String dv;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = true)
    private Long telefono;
    @Column(nullable = false)
    private String correo;
    @Column(nullable = false)
    private String nombreUsuario;
    @Column(nullable = false)
    private String contraseña;
    @Column(nullable = false)
    private int tipoUser;


    public Usuario(){
        this.rut =0L;
    }

    public Usuario(Long rut,String dv,String nombre,Long telefono,String correo, String nombreUsuario, String contraseña, int tipoUser){
        this.rut=rut;
        this.dv=dv;
        this.nombre=nombre;
        this.telefono=telefono;
        this.correo=correo;
        this.nombreUsuario=nombreUsuario;
        this.contraseña= contraseña;
        this.tipoUser= tipoUser;
    }
}

