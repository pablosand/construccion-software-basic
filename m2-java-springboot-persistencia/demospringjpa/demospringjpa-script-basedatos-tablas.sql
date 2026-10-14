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
