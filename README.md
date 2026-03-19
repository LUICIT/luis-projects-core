# Luis Projects Core

Librería base reutilizable para proyectos **Spring Boot API REST**.

Este módulo concentra componentes técnicos comunes que pueden compartirse entre distintos proyectos, evitando duplicación de código y permitiendo que los proyectos consumidores mantengan una estructura más limpia y enfocada en su lógica de negocio.

---

## Objetivo

Proveer una base común para proyectos backend con Spring Boot que incluya capacidades reutilizables como:

- configuración tipada del core
- seguridad JWT
- filtros de autenticación
- rate limit para login
- auditoría en entidades
- repositorios base
- manejo centralizado de errores
- converters genéricos
- configuración reusable de rutas protegidas

---

## Alcance de la librería

Esta librería **no es una aplicación ejecutable**.

No contiene:

- clase principal `@SpringBootApplication`
- archivos `application.yml`
- migraciones Flyway del proyecto consumidor
- controllers específicos del dominio del proyecto final
- lógica de negocio propia del proyecto consumidor

El proyecto que consuma esta librería será responsable de:

- definir su clase principal
- declarar sus archivos `application*.yml`
- definir sus migraciones
- crear sus controllers, services, entidades y repositorios específicos
- personalizar las rutas protegidas o públicas según su necesidad

---

## Tecnologías principales

- Java 21
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA / Hibernate
- Jakarta Validation
- Bucket4j
- Caffeine
- Lombok
- Maven

---

## Estructura base del módulo

```text
src/main/java/com/luisraguilar/luisprojectscore
├── config
├── converter
├── domain
│   ├── entity
│   └── repository
├── exception
└── security
```

---

## Componentes principales

### `config`
Contiene configuraciones reutilizables del core.

#### `CoreProperties`
Clase de propiedades tipadas del core bajo el prefijo:

```yaml
app:
```

Propiedades disponibles:

```yaml
app:
  database:
    soft-delete: true
  jwt:
    secret: your-base64-secret
    expiration: 3600000
  security:
    login-enabled: true
    login-identifier: email
```

#### `CoreConfiguration`
Registra las propiedades del core mediante `@EnableConfigurationProperties`.

#### `SecurityConfiguration`
Configura la seguridad base reutilizable:

- `SecurityFilterChain`
- `PasswordEncoder`
- `AuthenticationManager`
- integración con JWT
- integración con rate limit
- reglas de acceso por rutas

#### `RouteAuthorizationConfig`
Permite definir qué rutas requieren token y cuáles no.

Ejemplo de uso desde el proyecto consumidor:

```java
@Bean
public RouteAuthorizationConfig routeAuthorizationConfig() {
    return new RouteAuthorizationConfig()
            .withToken("/users/**")
            .withToken("/orders/**")
            .withoutToken("/public/**")
            .withoutToken("/catalog/**");
}
```

Reglas:

- `withToken(...)` -> requiere autenticación
- `withoutToken(...)` -> no requiere autenticación
- si `login-enabled=false`, todas las rutas se permiten
- si no se define ninguna ruta, por defecto todas quedan abiertas

---

### `domain/entity`
Contiene entidades reutilizables.

#### `AuditableEntity`
Entidad base auditable con:

- `created_at`
- `updated_at`
- `deleted_at`

Soporta auditoría con Spring Data JPA.

#### `BaseUserEntity`
Entidad base de usuario para autenticación reutilizable.

Campos principales:

- `id`
- `name`
- `username`
- `email`
- `password`
- `enabled`
- `failedAttempts`
- `lockedUntil`
- `lastFailedAt`
- auditoría heredada

---

### `domain/repository`
Contiene repositorios base reutilizables.

#### `DatabaseRepository`
Repositorio genérico con soporte para:

- `JpaRepository`
- `JpaSpecificationExecutor`
- soft delete
- búsquedas sobre registros no eliminados lógicamente

#### `BaseUserRepository`
Repositorio base reutilizable para usuarios.

Métodos incluidos:

- búsqueda por `email`
- búsqueda por `username`
- validación de existencia por `email`
- validación de existencia por `username`

---

### `exception`
Contiene el manejo estándar de errores del core.

#### `ErrorResponse`
Estructura estándar de respuesta de error:

```json
{
  "timestamp": "2026-03-13T20:00:00Z",
  "message": "Validation incorrect",
  "errors": {
    "email": "must be a well-formed email address"
  }
}
```

#### `GlobalExceptionHandler`
Maneja errores comunes del core y transforma excepciones a `ErrorResponse`.

#### Excepciones incluidas
- `UnauthorizedException`
- `ForbiddenException`
- `ConflictException`
- `TooManyRequestsException`
- `ServiceUnavailableException`
- `GatewayTimeoutException`

El proyecto consumidor puede agregar excepciones propias de negocio y handlers adicionales si lo requiere.

---

### `converter`
Contiene utilidades de conversión reutilizables.

#### `GenericConverter`
Permite mapear propiedades compatibles entre entidad y modelo usando reflexión, getters y setters.

Funciona bien cuando:

- los nombres de propiedades coinciden
- los tipos son compatibles
- no se requiere lógica de transformación compleja

Si se necesita una conversión más específica, el proyecto consumidor puede crear converters concretos.

---

### `security`
Contiene los componentes de seguridad reutilizables del core.

#### `JwtUtil`
Encapsula:

- generación de tokens JWT
- validación de tokens
- lectura de claims y subject

#### `JwtFilter`
Filtro que:

- lee el header `Authorization`
- valida tokens `Bearer`
- resuelve el usuario autenticado
- carga el contexto de seguridad

#### `CustomUserDetailsService`
Resuelve usuarios reutilizando `BaseUserRepository` y `CoreProperties`.

Soporta login por:

- `email`
- `username`

según el valor de:

```yaml
app.security.login-identifier
```

#### `LoginRateLimitFilter`
Aplica rate limit al endpoint de login para mitigar fuerza bruta básica.

---

### `web/model`
Contiene modelos reutilizables del core.

Dependiendo de la implementación actual del módulo, puede incluir modelos como:

- `BaseUserModel`
- `LoginModel`
- `TokenModel`

Estos modelos sirven como base para autenticación reutilizable y pueden extenderse o complementarse desde el proyecto consumidor.

---

## Propiedades del core

La librería espera que el proyecto consumidor defina propiedades bajo `app`.

### Ejemplo mínimo

```yaml
app:
  database:
    soft-delete: true
  jwt:
    secret: your-base64-secret
    expiration: 3600000
  security:
    login-enabled: true
    login-identifier: email
```

### Descripción

#### `app.database.soft-delete`
- `true`: habilita borrado lógico en el comportamiento esperado del core
- `false`: permite que el proyecto consumidor implemente otra estrategia

#### `app.jwt.secret`
Clave JWT en Base64.

#### `app.jwt.expiration`
Tiempo de expiración del token en milisegundos.

#### `app.security.login-enabled`
- `true`: activa la seguridad del login base
- `false`: desactiva la exigencia de autenticación

#### `app.security.login-identifier`
- `email`
- `username`

---

## Cómo usar la librería en un proyecto Spring Boot

### 1. Instalar la librería localmente

Desde la raíz de `luis-projects-core`:

```bash
mvn clean install
```

### 2. Agregar la dependencia al proyecto consumidor

```xml
<dependency>
    <groupId>com.luis-r-aguilar</groupId>
    <artifactId>luis-projects-core</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 3. Configurar escaneo del paquete del core

En la clase principal del proyecto consumidor:

```java
@SpringBootApplication(scanBasePackages = {
        "com.mycompany.myproject",
        "com.luisraguilar.luisprojectscore"
})
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {
        "com.mycompany.myproject.domain.repository",
        "com.luisraguilar.luisprojectscore.domain.repository"
})
@EntityScan(basePackages = {
        "com.mycompany.myproject.domain.entity",
        "com.luisraguilar.luisprojectscore.domain.entity"
})
public class MyProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyProjectApplication.class, args);
    }
}
```

---

## Qué debe vivir en el proyecto consumidor

El proyecto que use esta librería debe encargarse de:

- `@SpringBootApplication`
- archivos `application.yml`, `application-dev.yml`, `application-prod.yml`
- migraciones Flyway
- controllers propios
- services de negocio
- entidades y repositorios específicos del dominio
- handlers adicionales de excepciones de negocio
- personalización de rutas protegidas

---

## Recomendaciones de uso

- no modificar directamente el código del core desde el proyecto consumidor
- usar la librería para centralizar comportamiento reutilizable
- mantener las reglas de negocio propias fuera del core
- crear excepciones específicas del proyecto cuando sea necesario
- dejar los controllers en el proyecto consumidor
- usar Flyway, datasource y configuración de entorno desde el proyecto final

---

## Compilación del módulo

```bash
mvn clean compile
```

## Instalación local

```bash
mvn clean install
```

---

## Notas finales

`luis-projects-core` es una base técnica reutilizable, no un proyecto final.

Su función es servir como núcleo común para múltiples proyectos Spring Boot API REST, permitiendo que cada proyecto consumidor se enfoque en su propio dominio sin duplicar infraestructura transversal.
