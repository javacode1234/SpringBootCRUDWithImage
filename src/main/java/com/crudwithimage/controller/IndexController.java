package com.crudwithimage.controller;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.crudwithimage.dto.ProductDto;
import com.crudwithimage.model.Product;
import com.crudwithimage.services.ProductService;

import jakarta.validation.Valid;



@Controller
@RequestMapping("/products")
public class IndexController {
	
	@Autowired
	private ProductService productService;
	
	@GetMapping("/")
	public String getHomePage(Model model) {
		model.addAttribute("header", "Products");
		return "/index";
	}
	
	@GetMapping("/product-list-page")
	public String getProductList(Model model) {
		List<Product> products = productService.getAllProducts();
		model.addAttribute("products", products);
		return "/products/product_list_page";
	}

	@GetMapping("/create-product-page")
	public String getCreateProductPage(Model model) {
		ProductDto productDto = new ProductDto();
		model.addAttribute("productDto", productDto);
		return "/products/create_product_page";
	}
	
	@PostMapping("/create-product")
	public String createProduct(@Valid @ModelAttribute ProductDto productDto, BindingResult result) {
		
		if(productDto.getImageFileName().isEmpty()) {
			result.addError(new FieldError("productDto", "imageFileName", "The image File is Requared"));
		}
		
		if(result.hasErrors()) { return "/products/create_product_page";}
		
		//Save image file
		MultipartFile image = productDto.getImageFileName();
		Date createdDate = new Date();
		String storageFileName = createdDate.getTime() + "_" + image.getOriginalFilename();
		try {
			String uploadDir = "src/main/resources/static/img/";
			Path uploadPath = Paths.get(uploadDir);
			if(!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			try (InputStream inputStream = image.getInputStream()){
				
				Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (Exception e) {
			System.out.println("Exception : " + e.getMessage());
		}
		
		productService.saveProduct(productDto, createdDate, storageFileName);
		
		
		return "redirect:/products/product-list-page";
	}
	
	@GetMapping("/edit-product")
	public String getEditProductPage(Model model, @RequestParam Integer id) {
		
		try {
			
			Product product = productService.finProductByid(id);
			model.addAttribute("product", product);
			
			ProductDto productDto = new ProductDto();
			productDto.setName(product.getName());
			productDto.setBrand(product.getBrand());
			productDto.setCategory(product.getCategory());
			productDto.setDescription(product.getDescription());
			productDto.setPrice(product.getPrice());
			
			model.addAttribute("productDto", productDto);
			
		}
		catch (Exception e) {
			System.out.println("Exception : " + e.getMessage());
			return "redirect:/product-list-page";
		}
		
		return "/products/edit_product_page";
	}
	
	@PostMapping("/edit-product")
	public String editProduct(Model model, @RequestParam Integer id, @Valid @ModelAttribute ProductDto productDto,
								BindingResult result) {
		
		String uploadDir;
		String storageFileName = null;
		Date createdAt = null;

		try {
			Product product = productService.finProductByid(id);
			model.addAttribute("product", product);
			
			if(result.hasErrors()) { 
				return "/products/edit_product_page"; 
			}			
			
			if(!productDto.getImageFileName().isEmpty()) {
				//Delete old image
				uploadDir = "src/main/resources/static/img/";
				Path olImagePath = Paths.get(uploadDir + product.getImageFileName());
				
				try {
					Files.delete(olImagePath);
				} catch (Exception e) {
					System.out.println("Exception : " + e.getMessage());
				}
				
				//Save new image file
				MultipartFile image = productDto.getImageFileName();
				createdAt = new Date();
				storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
				
				try (InputStream inputStream = image.getInputStream()){					
					Files.copy(inputStream, Paths.get(uploadDir + storageFileName), 
							StandardCopyOption.REPLACE_EXISTING);
				}
				
				product.setImageFileName(storageFileName);
			}		
			
			product.setName(productDto.getName());
			product.setBrand(productDto.getBrand());
			product.setCategory(productDto.getCategory());
			product.setPrice(productDto.getPrice());
			product.setDescription(productDto.getDescription());
			
			productService.editProduct(product);
			//productService.saveProduct(productDto, createdAt, storageFileName);
		} 
	
		catch (Exception e) {
		System.out.println("Exception : " + e.getMessage());			
		}
		
		return "redirect:/products/product-list-page";
	}	
	
	@GetMapping("/delete-product")
	public String deleteProduct(@RequestParam Integer id) {
		
		try {
			
			Product product = productService.finProductByid(id);
			
			Path imagePath = Paths.get("src/main/resources/static/img/" + product.getImageFileName());
			
			try {
				Files.delete(imagePath);
				productService.deleteProduct(id);
			} catch (Exception e) {
				System.out.println("Exception : " + e.getMessage());
			}
			
		} catch (Exception e) {
			System.out.println("Exception : " + e.getMessage());
		}
		
		return "redirect:/products/product-list-page";
	}
	
	
}
