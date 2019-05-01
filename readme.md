### Usando cache en Spring  Boot

Vamos a imaginar una aplicación web, donde en cada petición recibida, debe  leer ciertos datos de configuración desde una base de datos. Esos datos de configuración no cambiaran normalmente pero nuestra aplicación, en cada petición, debe conectar con la base de datos, ejecutar las sentencias adecuadas para leer los datos, traerlos por la red, etc. Imaginemos, además, que la base de datos a la que nos conectamos esta saturada o la conexión de red que nos une a la base de datos es inestable. ¿Qué pasaría?. Pues que tendríamos una aplicación lenta por el hecho de leer continuamente unos datos que sabemos que apenas cambian.

Para solucionar ese problema podríamos utilizar una **Cache**, pero ¿ como implementarlo ?. En este articulo explicare como usar una cache básica que Spring provee.

#### Un poco de teoría

La cache se aplica sobre funciones, donde para un mismo valor de entrada esperamos un mismo valor de salida. Es por ello que siempre debemos tener al menos un  parámetro de entrada y devolver  algo.

Un ejemplo típico seria este:

Esta es una función cacheada:

```java
 @Cacheable(cacheNames="headers")
 public int funcionCacheada(int valor)
 {
  ... calculos muy complejos y costosos ....
  return N;
 }
```

Y ahora supongamos que tenemos el siguiente código donde llamamos a esa función:

```
int valor=funcionCacheada(1);
int otroValor=funcionCacheada(2);
int tercerValor=funcionCacheada(1);
```

Al ejecutar el programa, en la primera línea, Spring,  ejecutara la función y se guardara el resultado que devuelve. En la segunda línea, como no sabe el valor que se debe devolver para la entrada con valor "2" hará lo mismo. Sin embargo en la tercera línea Spring detectara que esa función ya ha sido llamada con el valor "1" y por lo tanto no ejecutara la función, simplemente devolverá el valor que en la primera llamada se guardo. 

El nombre de la cache es importante pues, entre otras cosas, nos permitirá limpiar la cache, para obligar a a Spring a ejecutar de nuevo las funciones.

La idea básicamente es que Spring en cada llamada a la función guarda en una tabla interna los  resultados para cada llamada, de tal manera que si ya tiene la salida para una entrada, no llama a la función. 

#### Practica

Y ahora, vamos a la práctica:

El proyecto de ejemplo sobre el que esta basado este articulo esta en:  [https://github.com/chuchip/cacheExample](https://github.com/chuchip/cacheExample )

Lo primero que se necesita es incluir la siguiente dependencia en nuestro proyecto:

```maven
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

Ahora ya podremos utilizar las etiquetas que nos permitirán usar **Cache** en nuestra aplicación.



La primera etiqueta a poner es **@EnableCaching**. Con esta etiqueta le indicamos a Spring que prepare el soporte para usar cache. Si no se la ponemos simplemente no la usara, independientemente de si indicamos posteriormente que cachee ciertos datos.

````java
@SpringBootApplication
@EnableCaching
public class CacheExampleApplication {
	public static void main(String[] args) {
		SpringApplication.run(CacheExampleApplication.class, args);
	}
}
````

En este ejemplo se leerán unos datos de una base de datos a través de unas peticiones REST.

Los datos como tal se leen en la clase **CacheDataImpl.java**  que esta en el paquete **com.profesorp.cacheexample.impl** 

La función que lee los datos es la siguiente:

```java

 @Cacheable(cacheNames="headers", condition="#id > 1")
 public DtoResponse getDataCache(int id) {	
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}		
		DtoResponse requestResponse=new DtoResponse();		
		Optional<Invoiceheader> invoice=invoiceHeaderRepository.findById(id);
		..... MAS CODIGO NO IMPORTANTE ...
	}
```

Como se puede ver tenemos la etiqueta **@Cacheable(cacheNames="headers", condition="#id > 1")**

Con ella, le estamos indicando a Spring dos cosas.

1. Que deseamos que cachee el resultado de esta función. 
2. Le ponemos como condición, que solo cachee los resultados si el valor de entrada es superior a 1.

En la función **flushCache** le ponemos la etiqueta @**CacheEvict** la cual limpia la cache indicada. En este caso, además, le indicamos que borre todas las entradas que tengan cacheadas.

```
@CacheEvict(cacheNames="headers", allEntries=true)
public void flushCache() {	}	
```



En la función **update** actualizamos la base de datos y con la etiqueta **@CachePut** le indicamos a Spring que actualice los datos para el valor que hay en  **dtoRequest.id** 

```
@CachePut(cacheNames="headers", key="#dtoRequest.id")
public  DtoResponse update(DtoRequest dtoRequest)
{
.... ACTUALIZADA LA BASE DE DATOS ...
}
```

Por supuesto esta función tiene que devolver un objeto igual al de la marcada con la etiqueta **@Cacheable** y debemos indicarle el valor de entrada, para el que se desea actualizar los datos.