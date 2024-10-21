package com.crudwithimage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crudwithimage.model.Product;


public interface ProductRepository extends JpaRepository<Product, Integer> {

}
