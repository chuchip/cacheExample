package com.profesorp.cacheexample.dtos;

import org.springframework.http.HttpStatus;

import com.profesorp.cacheexample.entities.Invoiceheader;

import lombok.Data;

@Data
public class DtoResponse {
	long interval;
	HttpStatus httpStatus;
	Invoiceheader invoiceHeader; 
}
