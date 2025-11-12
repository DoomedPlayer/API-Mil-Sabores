package com.example.test.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.test.assemblers.ProductoModelAssembler;
import com.example.test.model.Producto;
import com.example.test.services.ProductoService;


@RestController
@RequestMapping("/producto")
public class ProductoControllerV2 {

    @Autowired
    public ProductoService productoService;

    @Autowired
    private ProductoModelAssembler assembler;

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<Producto>> getAllProductos(){
        List<EntityModel<Producto>> producto= productoService.getProductos().stream()
        .map(assembler::toModel).collect(Collectors.toList());
        return CollectionModel.of(producto,
                linkTo(methodOn(ProductoControllerV2.class).getAllProductos()).withSelfRel());
    }

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Producto>> createCot(@RequestBody Producto producto){
        Producto newProducto= productoService.agregarProducto(producto);
        return ResponseEntity.created(linkTo(methodOn(ProductoControllerV2.class).getProductoByCodigo(newProducto.getCode())).toUri())
        .body(assembler.toModel(newProducto));
    }

    @GetMapping(value = "/{id}",produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<Producto> getProductoByCodigo(@PathVariable int id){
        Producto producto= productoService.buscarProducto(id);
        return assembler.toModel(producto);
    }

    @PutMapping(value = "/act/{id}",produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Producto>> actualizarCot(@PathVariable int id,@RequestBody Producto producto){
            producto.setCode(id);
            Producto updateProducto = productoService.modificaProducto(producto);
            return ResponseEntity.ok(assembler.toModel(updateProducto));
    }

    @DeleteMapping(value = "{id}",produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<?> eliminarCot(@PathVariable int id){
            productoService.eliminarProducto(id);
            return ResponseEntity.noContent().build();
    }

}
