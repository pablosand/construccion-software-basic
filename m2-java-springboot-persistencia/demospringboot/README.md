# Demo Spring Boot

Aplicación web de ejemplo desarrollada con Spring Boot. En la página principal muestra el mensaje **Demo Spring Boot** y una lista de tres elementos de ejemplo mediante una plantilla Thymeleaf.

## Requisitos

- Java 25
- No es necesario instalar Maven: el proyecto incluye Maven Wrapper.

## Ejecutar la aplicación

Desde la carpeta raíz del proyecto:

```powershell
.\mvnw.cmd spring-boot:run
```

En Linux o macOS:

```bash
./mvnw spring-boot:run
```

Luego abre [http://localhost:8080](http://localhost:8080) en el navegador. La aplicación escucha en el puerto `8080`, configurado en `src/main/resources/application.properties`.

## Pruebas

En Windows:

```powershell
.\mvnw.cmd test
```

En Linux o macOS:

```bash
./mvnw test
```

## Estructura del proyecto

```text
pom.xml
src/
├── main/
│   ├── java/com/example/demospringboot/
│   │   ├── controladores/   # Controlador de la página principal
│   │   ├── dominio/         # Modelo Elemento
│   │   ├── servicios/       # Servicio que proporciona elementos de ejemplo
│   │   └── DemospringbootApplication.java
│   └── resources/
│       ├── templates/       # Plantillas Thymeleaf
│       └── application.properties
└── test/                    # Pruebas de la aplicación
```

Los elementos mostrados son datos de ejemplo definidos en `ElementoServicio`; no se utiliza una base de datos.
