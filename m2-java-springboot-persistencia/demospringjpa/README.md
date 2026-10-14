# Demo Spring JPA: arquitectura de una aplicación de productos

Este proyecto es una aplicación web educativa para administrar productos. Permite
crear, listar, editar y eliminar registros: las cuatro operaciones conocidas como
**CRUD** (*Create, Read, Update, Delete*).

La guía está dirigida a los que comienzan a construir software. Su objetivo
es explicar no solo cómo ejecutar el proyecto, sino también por qué el código está
organizado de esta manera.

## 1. Tecnologías utilizadas

| Tecnología | Función en el proyecto |
|---|---|
| Java 25 | Lenguaje y versión configurada en `pom.xml`. |
| Spring Boot 4.1.1 | Arranque de la aplicación y configuración automática de sus componentes. |
| Spring MVC | Atención de solicitudes HTTP mediante controladores. |
| Thymeleaf | Generación de páginas HTML con datos del servidor. |
| Spring Data JPA | Acceso a datos mediante una interfaz de repositorio. |
| Hibernate | Implementación de JPA que convierte operaciones sobre objetos en operaciones sobre tablas. |
| MySQL | Almacenamiento persistente de productos. |
| Maven y Maven Wrapper | Gestión de dependencias, compilación y ejecución de pruebas. |
| JUnit, Mockito y MockMvc | Pruebas del servicio, del controlador y de las vistas. |
| CSS local | Apariencia compartida del listado y del formulario, sin servicios externos. |

Un **framework** ofrece estructuras y herramientas para tareas habituales.
Spring Boot integra estas herramientas y reduce la configuración manual.

## 2. Vista general de la arquitectura

La aplicación es un **monolito organizado en capas**: el código del servidor y las
plantillas se distribuyen juntos, pero sus responsabilidades están separadas.
MySQL se ejecuta como un servicio independiente.

```text
Navegador
   | Solicitud HTTP
   v
Spring MVC / DispatcherServlet
   |
   v
ProductoControlador --> ProductoServicio --> ProductoRepositorio
   |                                              |
   | Modelo con datos                             v
   v                                        JPA / Hibernate
Thymeleaf                                         |
   |                                              v
   v                                            MySQL
HTML generado
   |
   v
Navegador + CSS
```

`DispatcherServlet` es el punto de entrada de Spring MVC: dirige cada solicitud
al método del controlador correspondiente.

En una consulta, los datos regresan desde la base de datos al repositorio, al
servicio y al controlador. Thymeleaf los incorpora al HTML que recibe el navegador.

**No es una API REST ni una aplicación de página única:** el controlador devuelve
nombres de vistas o redirecciones, no respuestas JSON. Las páginas se generan
en el servidor.

### Capas y responsabilidades

| Parte | Archivo principal | Responsabilidad |
|---|---|---|
| Presentación | `lista.html`, `formulario.html`, `productos.css` | Mostrar datos, recibir entradas y presentar las acciones. |
| Control web | `ProductoControlador.java` | Asociar rutas HTTP con operaciones y preparar datos para las vistas. |
| Servicio | `ProductoServicio.java` | Coordinar operaciones sobre productos y sus transacciones. |
| Persistencia | `ProductoRepositorio.java` | Consultar, guardar y eliminar entidades en la base de datos. |
| Modelo persistente | `Producto.java` | Representar los datos de un producto y su mapeo JPA. |
| Arranque | `DemospringjpaApplication.java` | Iniciar Spring Boot. |

Esta separación evita, por ejemplo, que una plantilla consulte directamente MySQL.
También permite probar el servicio sin iniciar el servidor web.

## 3. Organización del proyecto

```text
demospringjpa
|-- pom.xml
|-- mvnw
|-- mvnw.cmd
|-- demospringjpa-script-basedatos-tablas.sql
|-- src
|   |-- main
|   |   |-- java
|   |   |   `-- com\example\demospringjpa
|   |   |       |-- DemospringjpaApplication.java
|   |   |       `-- productos
|   |   |           |-- Producto.java
|   |   |           |-- ProductoControlador.java
|   |   |           |-- ProductoServicio.java
|   |   |           `-- ProductoRepositorio.java
|   |   `-- resources
|   |       |-- application.properties
|   |       |-- templates\productos
|   |       |   |-- lista.html
|   |       |   `-- formulario.html
|   |       `-- static\css
|   |           `-- productos.css
|   `-- test
|       `-- java\com\example\demospringjpa
|           |-- DemospringjpaApplicationTests.java
|           `-- productos
|               |-- ProductoServicioTests.java
|               `-- ProductoControladorTests.java
`-- target (generado por Maven)
```

`src\main` contiene la aplicación; `src\test`, las pruebas.
`target` contiene resultados de compilación y no debe editarse como código fuente.

El paquete `productos` agrupa una funcionalidad completa. Dentro de él, las clases
separan las responsabilidades por capa.

### Paquete raíz y detección de componentes

La clase `DemospringjpaApplication` está en `com.example.demospringjpa` y tiene
`@SpringBootApplication`. Por defecto, Spring detecta componentes en ese paquete
y sus subpaquetes.

Como las clases de productos pertenecen a
`com.example.demospringjpa.productos`, no se necesitan `scanBasePackages`,
`@EntityScan` ni `@EnableJpaRepositories` explícitos: la configuración automática
de Spring Boot también busca allí las entidades y los repositorios.

## 4. Modelo de datos: Producto

Una **entidad** es un objeto cuya información se almacena en la base de datos.

| Campo | Tipo Java | Significado |
|---|---|---|
| `id` | `Long` | Identificador único generado por la base de datos. |
| `nombre` | `String` | Nombre del producto. |
| `precio` | `BigDecimal` | Valor decimal del precio. |
| `descripcion` | `String` | Información adicional. |

Las anotaciones principales son:

- `@Entity`: indica que JPA debe persistir esta clase.
- `@Id`: identifica la clave primaria.
- `@GeneratedValue(strategy = GenerationType.IDENTITY)`: delega la generación
  del ID a la base de datos.

El constructor vacío permite que JPA cree instancias. Los getters y setters
permiten acceder a los datos y modificarlos. Se usa `BigDecimal` para representar
precios sin las aproximaciones binarias propias de tipos como `double`.

No hay un setter público para el ID. La actualización busca el producto existente
por su identificador y modifica sus otros campos, sin reemplazar su identidad.

## 5. Repositorio y persistencia

```java
public interface ProductoRepositorio extends JpaRepository<Producto, Long>
{
}
```

Los parámetros indican la entidad (`Producto`) y el tipo de su ID (`Long`).
Spring Data JPA proporciona la implementación de esta interfaz en tiempo de
ejecución; no hace falta escribir una clase con SQL para cada operación básica.

El proyecto utiliza los siguientes métodos heredados:

| Método | Uso |
|---|---|
| `findAll()` | Obtener todos los productos. |
| `findById(id)` | Buscar un producto; devuelve un `Optional`, que puede estar vacío. |
| `save(producto)` | Persistir un producto nuevo o guardar uno existente. |
| `delete(producto)` | Eliminar la entidad indicada. |

**JPA** define la forma de mapear objetos a datos persistentes. **Hibernate**
implementa esa especificación y genera el SQL. El controlador no necesita
conocer ese SQL ni los detalles de conexión.

## 6. Servicio, inyección de dependencias y transacciones

`ProductoServicio` está anotado con `@Service`. Centraliza las operaciones de
listar, guardar, buscar, actualizar y eliminar.

Para actualizar, primero recupera el registro, copia nombre, precio y descripción,
y lo guarda. Para eliminar, primero comprueba que el registro exista.
Si no lo encuentra, lanza `ResponseStatusException` con estado HTTP **404**.
Así, una actualización de un ID inexistente no se convierte en una inserción.

### Inyección por constructor

El servicio recibe `ProductoRepositorio` en su constructor, y el controlador
recibe `ProductoServicio`. Spring crea y conecta estos objetos automáticamente.
Esto se llama **inyección de dependencias**.

La ventaja es que las clases no deben construir sus propias dependencias. En las
pruebas se pueden sustituir por objetos simulados.

### Transacciones

`@Transactional` en el servicio establece límites transaccionales para sus
operaciones públicas invocadas a través de Spring. Una transacción agrupa
operaciones de base de datos para confirmarlas juntas o revertirlas ante errores
que desencadenen rollback, como las excepciones de ejecución.

`listarProductos` y `buscarProducto` declaran `readOnly = true` porque solo
consultan datos. Esta opción expresa la intención y puede permitir optimizaciones;
no sustituye los permisos de la base de datos.

Las llamadas internas del servicio, como la búsqueda durante una actualización,
participan en la transacción de la operación exterior.

## 7. Controlador y rutas HTTP

`@Controller` registra el controlador web y `@RequestMapping("/productos")`
establece el prefijo común de sus rutas.

| Método HTTP | Ruta | Acción | Resultado |
|---|---|---|---|
| GET | `/productos` | Listar productos. | Vista `productos/lista`. |
| GET | `/productos/nuevo` | Preparar un producto nuevo. | Vista `productos/formulario`. |
| POST | `/productos` | Guardar un producto nuevo. | Redirección a `/productos`. |
| GET | `/productos/{id}/editar` | Buscar y cargar un producto. | Vista `productos/formulario`. |
| POST | `/productos/{id}` | Actualizar el producto indicado. | Redirección a `/productos`. |
| POST | `/productos/{id}/eliminar` | Eliminar el producto indicado. | Redirección a `/productos`. |

`{id}` es una variable de la ruta. Por ejemplo, `/productos/7/editar` solicita
editar el producto con ID `7`.

**GET** consulta o muestra páginas. **POST** envía datos y, en este proyecto,
realiza las modificaciones. Eliminar no se implementa como un enlace GET.

Otras piezas importantes:

- `Model`: lleva datos del controlador a la plantilla; no es la entidad ni la
  base de datos.
- `@ModelAttribute`: enlaza los campos enviados por el formulario con un objeto
  `Producto`.
- `@PathVariable`: extrae el identificador de la URL.
- `return "productos/lista"`: selecciona una plantilla; no devuelve su contenido
  como texto literal.
- `return "redirect:/productos"`: indica al navegador que solicite el listado.

Tras un POST se usa el patrón **Post/Redirect/Get**: guardar, redirigir y consultar.
Esto evita que actualizar la página de resultado vuelva a enviar el formulario.

**La ruta `/` no tiene controlador.** La entrada a la aplicación es
`http://localhost:8080/productos`; acceder solo a `http://localhost:8080/`
puede devolver 404.

## 8. Vistas y estilos

Las plantillas están en `src\main\resources\templates\productos`.
Thymeleaf las procesa en el servidor:

| Expresión | Uso en el proyecto |
|---|---|
| `th:each` | Generar una fila por producto. |
| `th:text` | Mostrar datos como texto escapado. |
| `th:href` y `th:action` | Generar enlaces y destinos de formularios. |
| `th:object` | Asociar el formulario con el producto. |
| `th:field` | Enlazar cada campo con una propiedad del producto. |
| `th:if` | Mostrar el mensaje cuando el listado está vacío. |

`formulario.html` sirve tanto para crear como para editar. Si `producto.id` es
nulo, muestra el modo de creación y envía a `/productos`; si tiene ID, muestra
el modo de edición y envía a `/productos/{id}`.

El listado ofrece acciones por registro y una confirmación JavaScript antes de
eliminar. **Cancelar** vuelve al listado sin enviar el formulario.

La hoja de estilos está en `src\main\resources\static\css\productos.css` y se
sirve en `/css/productos.css`. Incluye tarjetas, botones, estados de foco,
adaptación a móviles y desplazamiento horizontal para tablas anchas.

## 9. Ejemplo completo: editar un producto

1. El usuario pulsa **Editar** en la fila del producto con ID `7`.
2. El navegador envía `GET /productos/7/editar`.
3. El controlador llama a `servicio.buscarProducto(7L)`.
4. El servicio consulta el repositorio. Hibernate obtiene los datos de MySQL.
5. El controlador agrega el producto al `Model`.
6. Thymeleaf genera el formulario con los valores existentes.
7. El usuario cambia los datos y pulsa **Guardar**.
8. El navegador envía `POST /productos/7` con nombre, precio y descripción.
9. El servicio recupera la entidad con ID `7`, modifica sus datos y la guarda.
10. El controlador redirige a `/productos`, donde aparece el registro actualizado.

Si el producto fue eliminado antes de buscarlo o guardarlo, se devuelve 404.

## 10. Configuración y ejecución local

### Requisitos

Se necesita un **JDK 25**, MySQL en ejecución y una conexión a Internet para que
Maven pueda descargar las dependencias la primera vez. El Wrapper permite usar
Maven sin instalarlo globalmente.

Los siguientes comandos se ejecutan desde PowerShell, en la carpeta del proyecto.

### Preparar la base de datos

Ejecutar en MySQL:

```sql
CREATE DATABASE IF NOT EXISTS demo_springjpa;
```

El archivo `demospringjpa-script-basedatos-tablas.sql`, ubicado en la raíz del
proyecto, contiene un script de ejemplo listo para ejecutar en MySQL. Crea la
base de datos `demo_springjpa`, otorga permisos al usuario `root` local, crea
la tabla `producto` y agrega una consulta de ejemplo sobre esa tabla:

```sql
/*-- crear base de datos y asignar permisos a usuario root --*/
create database demo_springjpa character set utf8;

grant all on demo_springjpa.* to 'root'@'localhost';

flush privileges;

/*-- crear tabla producto --*/
use demo_springjpa;

CREATE TABLE producto (
  id INT PRIMARY KEY,
  nombre VARCHAR(255),
  precio DECIMAL(8, 2),
  descripcion VARCHAR(255)
);


/*-- querys --*/
use demo_springjpa;

select *
from producto;
```

Se puede ejecutar completo desde un cliente de MySQL (por ejemplo, MySQL
Workbench o el cliente `mysql`) para dejar la base de datos y la tabla listas
antes de iniciar la aplicación. El usuario y los permisos otorgados en el script
son un ejemplo para un entorno local de desarrollo: en otros entornos conviene
ajustar el usuario, el host y los privilegios según las políticas de seguridad
correspondientes.

Configurar la conexión en `src\main\resources\application.properties` o mediante
variables de entorno. La URL actual apunta a:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/demo_springjpa
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

`ddl-auto=update` permite a Hibernate actualizar el esquema según las entidades;
no crea el servidor MySQL ni la base de datos indicada en la URL. Es cómodo para
esta demostración, pero no reemplaza un sistema de migraciones en producción.
`show-sql=true` muestra las sentencias SQL para observar el acceso a datos.

Para usar credenciales locales sin incorporarlas al código, se pueden definir
variables de entorno en la misma sesión de PowerShell:

```powershell
$env:SPRING_DATASOURCE_USERNAME = 'usuario_local'
$env:SPRING_DATASOURCE_PASSWORD = 'reemplazar_por_la_clave_local'
```

El usuario debe existir en MySQL y contar con los permisos necesarios para operar
sobre la base de datos y actualizar su esquema. No publicar credenciales reales.

### Iniciar la aplicación

Si `JAVA_HOME` no está configurado, establecerlo apuntando al directorio del JDK,
no a su carpeta `bin`. Reemplazar la ruta de ejemplo por la instalación real:

```powershell
$env:JAVA_HOME = 'C:\ruta\al\jdk-25'
& .\mvnw.cmd spring-boot:run
```

También se puede ejecutar `DemospringjpaApplication` desde el IDE con el JDK
configurado. Spring Boot inicia un servidor web embebido, por lo que no hace falta
instalar Tomcat por separado.

Abrir **http://localhost:8080/productos**.

## 11. Pruebas del proyecto

| Clase | Qué comprueba | Dependencias utilizadas |
|---|---|---|
| `ProductoServicioTests` | Consultas, creación, actualización, eliminación y errores 404. | Repositorio simulado con Mockito; no necesita MySQL. |
| `ProductoControladorTests` | Rutas, formularios, HTML generado, redirecciones y errores 404. | Servicio simulado, MockMvc y plantillas reales; no necesita MySQL. |
| `DemospringjpaApplicationTests` | Que el contexto completo de Spring Boot pueda iniciar. | Configuración de la aplicación y conexión a la base de datos. |

Un **mock** simula una dependencia para probar una clase de forma aislada.
MockMvc permite enviar solicitudes al controlador sin levantar un servidor real.
Las pruebas de plantillas comprueban el HTML generado, no el aspecto visual en
un navegador.

Para ejecutar solo las pruebas de productos:

```powershell
& .\mvnw.cmd '-Dtest=ProductoServicioTests,ProductoControladorTests' test
```

Para ejecutar todas las pruebas, con MySQL y las credenciales configurados:

```powershell
& .\mvnw.cmd test
```

## 12. Alcance y posibles mejoras

El proyecto es una base de aprendizaje, no una solución completa de producción.
Actualmente:

- No implementa autenticación, autorización ni protección CSRF.
- Usa `required` y el tipo numérico del formulario como ayudas del navegador,
  pero no tiene validación de negocio en el servidor. No hay una regla implementada
  que impida, por ejemplo, guardar un precio negativo.
- Utiliza la entidad directamente para recibir datos del formulario; no hay DTO
  (objeto de transferencia de datos) separado.
- Devuelve 404 para productos inexistentes, pero no incorpora una página de
  error personalizada.
- Muestra todos los productos sin paginación, aunque el repositorio hereda soporte
  para ella.
- No incluye migraciones versionadas ni pruebas de persistencia contra una base
  de datos aislada.

Estas características pueden incorporarse como ejercicios posteriores. Para
mantener la separación de responsabilidades: los cambios visuales pertenecen a
las vistas y CSS; las rutas, al controlador; las reglas y operaciones, al servicio;
y las consultas, al repositorio.

## 13. Orden sugerido para estudiar el código

1. Leer `Producto` y reconocer los datos que representa.
2. Revisar `ProductoRepositorio` y sus métodos heredados.
3. Seguir las operaciones de `ProductoServicio`.
4. Relacionar las rutas de `ProductoControlador` con esas operaciones.
5. Examinar las expresiones Thymeleaf en las dos plantillas.
6. Leer las pruebas para identificar comportamientos esperados y casos de error.
7. Crear, editar y eliminar un producto observando las solicitudes del navegador
   y el SQL mostrado por la aplicación.

La idea central es simple: **cada parte tiene una responsabilidad definida y
colabora con las demás mediante interfaces claras**.
