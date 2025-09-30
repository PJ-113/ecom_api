package com.ecom2.api.dto;

public record UserDto(
	    Long id,
	    String name,
	    String email,
	    String phone,
	    String role
	) {}