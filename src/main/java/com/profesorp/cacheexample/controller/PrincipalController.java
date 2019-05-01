package com.profesorp.cacheexample.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.profesorp.cacheexample.dtos.DtoRequest;
import com.profesorp.cacheexample.dtos.DtoResponse;
import com.profesorp.cacheexample.services.ICacheData;

@RestController
public class PrincipalController {
	
	@Autowired
	ICacheData cacheData;
	
	@GetMapping("/{id}")
	public ResponseEntity<DtoResponse> get(@PathVariable int id)
	{
		long timeInit=System.currentTimeMillis();
		DtoResponse response=cacheData.getDataCache(id);		
		long timeEnd=System.currentTimeMillis();
		response.setInterval(timeEnd-timeInit);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	
	@PutMapping("/")
	public ResponseEntity<DtoResponse> put(@RequestBody DtoRequest dtoRequest )
	{		
		cacheData.update(dtoRequest);
		return new ResponseEntity<>(null,HttpStatus.OK);
	}
		
	@GetMapping("/flushcache")
	public String flushCache()
	{
		cacheData.flushCache();
		return "Cache Flushed!";
	}
}
