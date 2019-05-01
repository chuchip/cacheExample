package com.profesorp.cacheexample.services;

import com.profesorp.cacheexample.dtos.DtoRequest;
import com.profesorp.cacheexample.dtos.DtoResponse;

public interface ICacheData {
	
	public DtoResponse  getDataCache(int id);
	public  DtoResponse update(DtoRequest dtoRequest);
	public void flushCache();
}
