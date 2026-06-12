# Parte 5 - Microservicios

## Descripción
Descomposición del sistema de bienestar universitario en microservicios
pequeños y cohesivos, cada uno con una responsabilidad clara y ejecutándose
en su propio puerto.

## Servicios

| Servicio | Responsabilidad | Puerto | Estado |
|----------|------------------|--------|--------|
| AppointmentService | Gestionar citas y turnos de atención (solicitar, cancelar, consultar) | 50054 | Implementado |
| MedicalService | Gestionar información de especialidades médicas disponibles | 50055 | Diseñado (no implementado) |
| GymService | Gestionar reservas de sesiones de gimnasio | 50056 | Implementado |
| RecreationService | Gestionar préstamo/reserva de recursos recreativos | 50057 | Diseñado (no implementado) |

### Servicios implementados

**AppointmentService** (reutilizado de la Parte 4): contrato `appointment.proto`
con `RequestAppointment`, `CancelAppointment`, `GetAppointments`.

**GymService** (nuevo): contrato `gym.proto` con `ReserveSession` y
`GetSessions`.

```proto
service GymService {
  rpc ReserveSession (GymReservationRequest) returns (GymReservationResponse);
  rpc GetSessions (StudentGymRequest) returns (GymSessionList);
}
```

### Servicios diseñados (no implementados)

**MedicalService**: expondría una operación `GetAvailableSpecialties()` que
devuelve la lista de especialidades médicas disponibles (medicina general,
odontología, etc.) y horarios. Sus datos (especialidades, horarios) son
independientes de las citas concretas, que pertenecen a `AppointmentService`.

**RecreationService**: expondría operaciones `ReserveResource(studentId,
resourceId)` y `ReturnResource(reservationId)` para gestionar el préstamo de
recursos recreativos (balones, mesas de ping pong, etc.), con su propio
inventario en memoria independiente de los demás servicios.

## Cómo ejecutar

Compilar:
```powershell
cd parte5_microservicios
mvn clean compile
```

AppointmentService (terminal 1, puerto 50054):
```powershell
mvn compile exec:java '-Dexec.mainClass=edu.eci.arsw.appointment.AppointmentMicroserviceServer'
```

GymService (terminal 2, puerto 50056):
```powershell
mvn compile exec:java '-Dexec.mainClass=edu.eci.arsw.gym.GymMicroserviceServer'
```

Cliente (terminal 3, con ambos servicios corriendo):
```powershell
mvn compile exec:java '-Dexec.mainClass=edu.eci.arsw.client.WellnessMicroserviceClient'
```

## Salida obtenida
=== Solicitando cita de psicologia (AppointmentService:50054) ===
Cita creada: 1417fdbe - Estado: REQUESTED
=== Reservando sesion de gimnasio (GymService:50056) ===
Reserva: ca852625 - Reserva de gimnasio exitosa
=== Citas del estudiante EST001 ===
1417fdbe | PSYCHOLOGY | 2026-06-20 | REQUESTED
=== Sesiones de gimnasio del estudiante EST001 ===
ca852625 | Lunes 6:00am

## Preguntas de reflexión

### ¿Por qué decidió separar esos servicios y no otros?

La separación sigue el criterio de **responsabilidad única y cohesión de
datos**: cada servicio gestiona un tipo de recurso completamente
independiente de los demás (citas médicas/psicológicas, especialidades
médicas, sesiones de gimnasio, recursos recreativos). No hay relaciones de
datos que obliguen a mantenerlos juntos: una reserva de gimnasio no necesita
saber nada sobre una cita médica, y viceversa. Si se hubieran agrupado, por
ejemplo, `GymService` y `RecreationService` en un solo "ActivitiesService",
se mezclarían dos dominios distintos (ejercicio físico vs préstamo de
recursos) sin necesidad real de compartir lógica o datos.

### ¿Qué datos pertenecen a cada servicio?

- **AppointmentService**: citas (`Appointment`) con su id, estudiante,
  tipo de servicio, fecha y estado.
- **MedicalService**: catálogo de especialidades médicas y sus horarios
  disponibles (no depende de qué estudiante pidió qué cita).
- **GymService**: sesiones de gimnasio reservadas (`GymSession`) con id,
  estudiante y franja horaria.
- **RecreationService**: inventario de recursos recreativos y sus reservas
  activas.

Cada servicio mantiene su propio estado en memoria (`Map`), sin compartir
estructuras de datos con los demás — esto es justamente lo que permite que
cada uno se pueda desplegar, escalar y modificar de forma independiente.

### ¿Qué riesgo aparece cuando el cliente conoce todos los servicios?

El cliente (`WellnessMicroserviceClient`) ya conoce las direcciones y puertos
de ambos servicios (`localhost:50054` y `localhost:50056`), y crea un canal
gRPC distinto para cada uno. Esto genera **acoplamiento directo entre el
cliente y la topología de despliegue**: si un servicio cambia de puerto, se
mueve a otra máquina, o se agrega un nuevo microservicio, el cliente debe
modificarse y recompilarse. Además, si el cliente necesitara consultar los 4
servicios en lugar de 2, tendría que manejar 4 canales gRPC y conocer 4
direcciones distintas, lo cual escala mal a medida que crece el sistema. Este
es precisamente el problema que resuelve un API Gateway (Parte 6): centraliza
el conocimiento de la topología interna en un único punto, y el cliente solo
necesita conocer una dirección.