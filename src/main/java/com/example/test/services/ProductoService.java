package com.example.test.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.test.model.Producto;
import com.example.test.repository.ProductoRepositoryI;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductoService {

    @Autowired
    public ProductoRepositoryI productoRepository;

    public List<Producto> getProductos(){
        return productoRepository.findAll();
    }

    public Producto buscarProducto(int id){
        return productoRepository.findById(id).get();
    }

    public Producto agregarProducto(Producto producto){
        return productoRepository.save(producto);
    }

    public Producto modificaProducto(Producto producto){
        return productoRepository.save(producto);
    }

    public void eliminarProducto(int id){
        productoRepository.deleteById(id);
    }
}
