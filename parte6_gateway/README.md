# Parte 6 - API Gateway

## Descripción
Gateway que centraliza el acceso a los microservicios de bienestar
universitario (`AppointmentService` y `GymService`), evitando que el cliente
final conozca los puertos y contratos individuales de cada servicio interno.



## Operaciones del Gateway

| Operación | Descripción | Estado |
|-----------|-------------|--------|
| `requestAppointment(studentId, serviceType, date)` | Solicita una cita a traves de AppointmentService | Implementado |
| `reserveGymSession(studentId, timeSlot)` | Reserva una sesion de gimnasio a traves de GymService | Implementado |
| `reserveRecreationResource(studentId, resourceId)` | Reserva un recurso recreativo | Pendiente (RecreationService no implementado en Parte 5) |
| `getStudentWellnessSummary(studentId)` | Devuelve un resumen unificado: citas + sesiones de gimnasio + recursos recreativos | Implementado (recreativos como pendiente) |

## Cómo ejecutar

Se necesitan los dos microservicios de la Parte 5 corriendo, mas el Gateway.

Compilar:
```powershell
cd parte6_gateway
mvn clean compile
```

AppointmentService (terminal 1, desde parte5_microservicios, puerto 50054):
```powershell
mvn compile exec:java '-Dexec.mainClass=edu.eci.arsw.appointment.AppointmentMicroserviceServer'
```

GymService (terminal 2, desde parte5_microservicios, puerto 50056):
```powershell
mvn compile exec:java '-Dexec.mainClass=edu.eci.arsw.gym.GymMicroserviceServer'
```

WellnessGateway (terminal 3, desde parte6_gateway):
```powershell
mvn compile exec:java '-Dexec.mainClass=edu.eci.arsw.gateway.WellnessGateway'
```

## Salida obtenida
=== 1. Solicitando cita medica via Gateway ===
Cita creada: 9302fad6 - Estado: REQUESTED
=== 2. Reservando sesion de gimnasio via Gateway ===
Reserva: 59d1bdbe - Reserva de gimnasio exitosa
=== 3. Reservando recurso recreativo via Gateway ===
RecreationService no implementado
=== 4. Resumen unificado de bienestar via Gateway ===
Resumen de bienestar para EST002:
Citas:

MEDICINE | 2026-06-25 | REQUESTED

Sesiones de gimnasio:

Miercoles 7:00am

Recursos recreativos:
(RecreationService no implementado)

## Preguntas de reflexión

### ¿Qué simplifica el Gateway para el cliente?

El cliente solo necesita conocer la clase `WellnessGateway` y sus 4 métodos
de alto nivel; no necesita saber que existen dos servicios gRPC distintos
corriendo en los puertos 50054 y 50056, ni manejar dos canales y dos stubs
por separado. La operación `getStudentWellnessSummary` es el ejemplo más
claro: una sola llamada del cliente desencadena internamente dos llamadas
RPC (una a `AppointmentService.GetAppointments` y otra a
`GymService.GetSessions`), y el Gateway devuelve un resultado ya combinado y
listo para mostrar. El cliente pasa de "orquestar varios servicios" a
"llamar un metodo".

### ¿Qué complejidad agrega al sistema?

El Gateway introduce un componente adicional que debe desplegarse,
mantenerse y mantenerse disponible. Ahora existe un punto donde conviven las
dependencias de *todos* los servicios internos (sus stubs gRPC, sus
direcciones, sus versiones de `.proto`), lo que significa que cualquier
cambio en un microservicio (nuevo campo, nuevo metodo, cambio de puerto)
puede requerir actualizar el Gateway. Tambien agrega latencia adicional: una
peticion del cliente al Gateway puede traducirse en varias llamadas RPC
internas antes de devolver una respuesta. Como se ve en
`getStudentWellnessSummary`, el Gateway hace 2 llamadas secuenciales
(Appointment y Gym) para construir una sola respuesta.

### ¿Qué pasaría si el Gateway empieza a contener demasiada lógica de negocio?

El Gateway dejaría de ser un simple punto de entrada y se convertiria en un
"monolito disfrazado": toda la logica de combinar, validar y transformar
datos de negocio quedaria concentrada ahi, mientras los microservicios
quedarian reducidos a simples repositorios de datos (anti-patron conocido
como "smart gateway, dumb services"). Esto reintroduce el problema que la
arquitectura de microservicios buscaba resolver: un componente unico que
concentra demasiada responsabilidad, dificil de escalar y de modificar de
forma independiente, y que se vuelve un cuello de botella tanto en
rendimiento como en desarrollo (cualquier cambio de negocio requiere tocar el
Gateway sin importar a que dominio pertenezca).