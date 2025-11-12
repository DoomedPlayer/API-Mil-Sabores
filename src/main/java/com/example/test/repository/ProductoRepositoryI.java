package com.example.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.test.model.Producto;

@Repository
public interface ProductoRepositoryI  extends JpaRepository<Producto, Integer>{

}
