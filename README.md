# API REST de tarjetas

API sencilla para consultar y gestionar tarjetas. Está desarrollada con Java 17, Spring Boot 3.5 y Maven. Se ejecuta en el puerto `8080`.

## Requisitos

- JDK 17
- Maven 3.6 o superior

## Datos

Las tarjetas se almacenan en una `ArrayList` en memoria. Al iniciar la aplicación se cargan cinco tarjetas de ejemplo en `TarjetaRepository`. No se utiliza una base de datos y los registros creados durante la ejecución se pierden al reiniciar la aplicación.

## Compilar y ejecutar

Desde la carpeta raíz del proyecto:

```bash
mvn clean package
mvn spring-boot:run
```

También puedes ejecutar el método `main` de `TarjetasApiApplication` desde el IDE.

La API quedará disponible en `http://localhost:8080`. Para compilar sin generar el paquete:

```bash
mvn clean compile
```

## Endpoints

| Método | Ruta | Respuesta |
|---|---|---|
| `GET` | `/api/v1/tarjetas` | `200 OK` con todas las tarjetas. |
| `GET` | `/api/v1/tarjetas/{numero}` | `200 OK` con la tarjeta o `404 Not Found` si no existe. |
| `POST` | `/api/v1/tarjetas` | `201 Created` con la tarjeta guardada. |
| `PUT` | `/api/v1/tarjetas/{numero}/bloquear` | `200 OK` con la tarjeta bloqueada o `404 Not Found` si no existe. |

### Validaciones al registrar

El endpoint `POST /api/v1/tarjetas` recibe un objeto JSON con `numero`, `titular`, `tipo` y `limite`. Rechaza con `400 Bad Request` si el número no tiene 16 caracteres, el titular está vacío, el tipo no es `CREDITO` o `DEBITO`, o si una tarjeta de crédito tiene límite menor o igual a cero. Si el número ya existe, responde `409 Conflict`. El estado se establece como `ACTIVA` al guardar.

Ejemplo de solicitud:

```json
{
  "numero": "4111000000000006",
  "titular": "Pedro Salas",
  "tipo": "CREDITO",
  "limite": 2500.00
}
```

## Probar la API

Puedes usar las solicitudes del archivo `requests.http` desde un IDE compatible, o acceder a Swagger UI en:

`http://localhost:8080/swagger-ui.html`
