package com.example.test.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.test.model.Usuario;
import com.example.test.services.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/cotizacion/Usuario")
@Tag(name = "Usuario", description = "Operaciones relacionadas con los Usuarios")
public class UsuarioController {
    @Autowired
    public UsuarioService UsuarioService;

    @GetMapping
    @Operation(summary = "Obtener todos los Usuarios", description = "Obtiene una lista de todos los Usuarios")
    public ResponseEntity<List<Usuario>> obtenerUsuarios(){
        List<Usuario> Usuarios = UsuarioService.getUsuarios();
        if(Usuarios.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(Usuarios);
    }

    @GetMapping("{id}")
    @Operation(summary = "Obtener un Usuario por su id", description = "Busca un Usuario por su id")
    public ResponseEntity<Usuario> buscarUsuario(@PathVariable Long id){
        try{
            Usuario Usuario = UsuarioService.obtenerUsuarioPorRut(id);
            return ResponseEntity.ok(Usuario);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Crea un nuevo Usuario", description = "Agrega un nuevo Usuario")
    public ResponseEntity<Usuario> agregarUsuario(@RequestBody Usuario Usuario){
        Usuario UsuarioNuevo = UsuarioService.agregarUsuario(Usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioNuevo);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Elimina un Usuario", description = "Elimina un Usuario por su id")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id){
        try{
            UsuarioService.eliminarUsuario(id);
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/act/{id}")
    @Operation(summary = "Actualizar un Usuario", description = "Actualiza un Usuario por su id")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id,@RequestBody Usuario Usuario){
        try{
            Usuario pro = UsuarioService.obtenerUsuarioPorRut(id);
            pro.setRut(id);
            pro.setDv(Usuario.getDv());
            pro.setNombre(Usuario.getNombre());
            pro.setTelefono(Usuario.getTelefono());
            pro.setCorreo(Usuario.getCorreo());
            pro.setNombreUsuario(Usuario.getNombreUsuario());
            pro.setContraseña(Usuario.getContraseña());
            pro.setTipoUser(Usuario.getTipoUser());
            return ResponseEntity.ok(Usuario);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }
}
