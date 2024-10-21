package com.crudwithimage.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {

	@NotEmpty(message="The Name Is Requared !!!")
	private String name;
	@NotEmpty(message="The Brand Is Requared !!!")
	private String brand;
	@NotEmpty(message="The Category Is Requared !!!")
	private String category;
	@Min(0)
	private String price;
	
	@Size(min=10, message="The description should be at least 10 caracters !!!")
	@Size(max=1000, message="The description can not exceed 1000 caracters !!!")
	private String description;
	private MultipartFile imageFileName;
}
