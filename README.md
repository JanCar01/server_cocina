# server_cocina

Primero arrancamos el contenedor con la imagen de la base de datos con:
docker-compose up -d

Luego lanzamos springbot con:
mvn spring-boot:run

El servidor aun no tiene ip publica, solo dentro de la red local
