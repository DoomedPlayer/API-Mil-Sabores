package com.example.test.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.test.model.Usuario;
import com.example.test.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

import java.util.List;

@Service
@Transactional
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> getUsuarios(){
        return usuarioRepository.findAll();
    }

    public Usuario agregarUsuario(Usuario usuario){
        return usuarioRepository.save(usuario);
    }

    public Usuario obtenerUsuarioPorRut(Long rut){
        return usuarioRepository.findById(rut).get();
    }

    public Usuario modificarUsuario(Usuario usuario){
        return usuarioRepository.save(usuario);
    }

    public void eliminarUsuario(Long rut){
        usuarioRepository.deleteById(rut);
    }

}
