package com.crudwithimage.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crudwithimage.dto.ProductDto;
import com.crudwithimage.model.Product;
import com.crudwithimage.repository.ProductRepository;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository productRepository;
	
	public void editProduct(Product product) {
		productRepository.save(product);
	}
	
	public void saveProduct(ProductDto productDto,Date createdAt,String imageFileName) {
		
		Product product = new Product();
		product.setName(productDto.getName());
		product.setBrand(productDto.getBrand());
		product.setCategory(productDto.getCategory());
		product.setPrice(productDto.getPrice());
		product.setDescription(productDto.getDescription());
		product.setCreatedAt(createdAt);
		product.setImageFileName(imageFileName);
		
		productRepository.save(product);
	}

	public List<Product> getAllProducts() {
		return productRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}

	public Product finProductByid(Integer id) {
		return productRepository.findById(id).get();
	}
	
	public void deleteProduct(Integer id) {
		productRepository.deleteById(id);
	}

}
