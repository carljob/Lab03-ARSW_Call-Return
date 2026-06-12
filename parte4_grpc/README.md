#  Parte 4 - gRPC

## Descripción
Servicio gRPC para gestionar solicitudes de citas de bienestar universitario,
usando un contrato formal definido en `appointment.proto` con Protocol Buffers.

## Entidades

| Entidad | Campos |
|---------|--------|
| Student | id, name, institutionalEmail (no usado directamente en el RPC, identificado por studentId) |
| Appointment | id, studentId, serviceType, date, status |
| ServiceType | MEDICINE, PSYCHOLOGY, DENTISTRY |
| AppointmentStatus | REQUESTED, CANCELLED, ATTENDED |

## Contrato (appointment.proto)

```proto
service AppointmentService {
  rpc RequestAppointment (AppointmentRequest) returns (AppointmentResponse);
  rpc CancelAppointment (CancelRequest) returns (CancelResponse);
  rpc GetAppointments (StudentRequest) returns (AppointmentList);
}
```

## Cómo ejecutar

Compilar (genera las clases a partir del .proto):
```powershell
cd parte4_grpc
mvn clean compile
```

Servidor (terminal 1, puerto 50054):
```powershell
mvn compile exec:java '-Dexec.mainClass=edu.eci.arsw.appointment.AppointmentGrpcServer'
```

Cliente (terminal 2):
```powershell
mvn compile exec:java '-Dexec.mainClass=edu.eci.arsw.appointment.AppointmentGrpcClient'
```

## Salida obtenida
=== Solicitando cita de psicologia ===
Cita creada: 4d7f2c01 - Estado: REQUESTED
=== Solicitando cita de medicina ===
Cita creada: 11f08fbb - Estado: REQUESTED
=== Citas del estudiante EST001 ===
11f08fbb | MEDICINE | 2026-06-18 | REQUESTED
4d7f2c01 | PSYCHOLOGY | 2026-06-15 | REQUESTED
=== Cancelando primera cita ===
Cita cancelada correctamente
=== Citas del estudiante EST001 (despues de cancelar) ===
11f08fbb | MEDICINE | 2026-06-18 | REQUESTED
4d7f2c01 | PSYCHOLOGY | 2026-06-15 | CANCELLED

## Verificación de reglas del negocio

- Una cita solicitada queda en `REQUESTED`: confirmado al crear ambas citas.
- Una cita cancelada cambia a `CANCELLED` y deja de comportarse como activa:
  la cita `4d7f2c01` pasa de `REQUESTED` a `CANCELLED` tras cancelarla.
- Es posible consultar todas las citas de un estudiante específico mediante
  `GetAppointments`.

## Preguntas de reflexión

### ¿Por qué el archivo .proto se considera un contrato?

El archivo `.proto` define de forma explícita y formal los servicios
disponibles, los métodos remotos (`RequestAppointment`, `CancelAppointment`,
`GetAppointments`), y la estructura exacta de cada mensaje (tipos, nombres y
números de campo). Tanto el cliente como el servidor generan su código a
partir del mismo archivo, por lo que ambos comparten exactamente la misma
definición de tipos y operaciones. A diferencia de RMI, donde el contrato
está atado a una interfaz Java, aquí el contrato es un archivo de texto
independiente del lenguaje, versionable, y que sirve como única fuente de
verdad para generar clientes y servidores en cualquier lenguaje soportado por
gRPC.

### ¿Qué tan fácil sería crear un cliente en otro lenguaje?

Sería relativamente sencillo. El mismo archivo `appointment.proto` puede
compilarse con `protoc` para generar código cliente en Python, Go,
JavaScript, C#, etc., sin modificar una sola línea del servidor. El cliente
generado en cualquiera de esos lenguajes podría invocar
`RequestAppointment`, `CancelAppointment` y `GetAppointments` exactamente con
los mismos nombres de campo y tipos definidos en el `.proto`, comunicándose
con el servidor Java por HTTP/2 y Protocol Buffers de forma transparente. Esto
contrasta directamente con RMI, donde un cliente no-Java no podría
interoperar sin un puente adicional.

### ¿Qué diferencias encuentra entre RMI y gRPC?

RMI está limitado al ecosistema Java: tanto el contrato (interfaz `Remote`)
como la serialización de objetos dependen de la JVM, y el protocolo de
transporte (JRMP) no es interoperable con otros lenguajes. gRPC, en cambio,
define el contrato en un archivo `.proto` neutral al lenguaje, serializa los
mensajes con Protocol Buffers (un formato binario compacto y eficiente), y
usa HTTP/2 como transporte, lo que le da soporte multiplataforma real.
Además, gRPC separa claramente la definición del contrato (`.proto`) de su
implementación, mientras que en RMI la interfaz Java cumple ambos roles. Por
otro lado, RMI permite pasar objetos Java complejos directamente gracias a
`Serializable`, mientras que en gRPC todo debe modelarse explícitamente como
mensajes Protocol Buffers.