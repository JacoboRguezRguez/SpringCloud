package com.bdi.springboot.app.productos.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.bdi.springboot.app.commons.models.entity.Producto;

public interface ProductoDao extends CrudRepository<Producto, Long>{

}
