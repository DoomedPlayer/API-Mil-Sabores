package com.example.test.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import com.example.test.model.Producto;
import com.example.test.services.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/producto")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Producto", description = "Operaciones relacionadas con los productos")
public class ProductoController {
    @Autowired
    public ProductoService productoService;

    @GetMapping
    @Operation(summary = "Obtener todos los productos", description = "Obtiene una lista de todos los productos")
    public ResponseEntity<List<Producto>> obtenerProductos(){
        List<Producto> productos = productoService.getProductos();
        if(productos.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @GetMapping("{id}")
    @Operation(summary = "Obtener un producto por su id", description = "Busca un producto por su id")
    public ResponseEntity<Producto> buscarProducto(@PathVariable int id){
        try{
            Producto producto = productoService.buscarProducto(id);
            return ResponseEntity.ok(producto);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @Value("${file.upload-dir}")
    private String UPLOAD_DIR;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Crea un nuevo producto", description = "Agrega un nuevo producto")
    public ResponseEntity<Producto> agregarProducto(@RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam("price") int price,
            @RequestParam("type") String type,
            @RequestParam("description") String description,
            @RequestParam(value = "file", required = false) MultipartFile file
    ){
        try {
            Producto producto = new Producto();
            producto.setName(name);
            producto.setCategory(category);
            producto.setPrice(price);
            producto.setType(type);
            producto.setDescription(description);

            // Lógica para guardar la imagen si existe
            if (file != null && !file.isEmpty()) {
                // 1. Generar nombre único para evitar colisiones (opcional pero recomendado)
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                // 2. Crear la ruta destino
                Path path = Paths.get(UPLOAD_DIR + fileName);
                // 3. Guardar el archivo físico
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                // 4. Guardar SOLO el nombre del archivo en la BD
                producto.setImage(fileName);
            } else {
                producto.setImage("default.jpg"); // Imagen por defecto si no suben nada
            }

            Producto productoNuevo = productoService.agregarProducto(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(productoNuevo);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Elimina un producto", description = "Elimina un producto por su id")
    public ResponseEntity<?> eliminarProducto(@PathVariable int id){
        try{
            productoService.eliminarProducto(id);
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/act/{id}")
    @Operation(summary = "Actualizar un producto", description = "Actualiza un producto por su id")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable int id,@RequestBody Producto producto){
        try{
            Producto pro = productoService.buscarProducto(id);
            pro.setCode(id);
            pro.setName(producto.getName());
            pro.setCategory(producto.getCategory());
            pro.setPrice(producto.getPrice());
            pro.setType(producto.getType());
            pro.setDescription(producto.getDescription());
            pro.setImage(producto.getImage());
            return ResponseEntity.ok(producto);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }
}
