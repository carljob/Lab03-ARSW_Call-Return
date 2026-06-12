# Parte 3 - RMI

## Descripción
Sistema de inventario de equipos de laboratorio implementado con Java RMI.
El cliente invoca métodos remotos directamente sobre un objeto publicado en
un RMI Registry, sin diseñar un protocolo de mensajes propio.

## Equipos disponibles

| Código | Nombre | Laboratorio |
|--------|--------|-------------|
| PC01 | Computador Dell | Lab-A |
| PC02 | Computador HP | Lab-A |
| RPI01 | Raspberry Pi 4 | Lab-B |
| ARD01 | Arduino Mega | Lab-B |

Todos inician como DISPONIBLE.

## Interfaz remota (LabEquipmentService)

```java
List consultarEquipos()
String consultarEquipo(String codigo)
boolean reservarEquipo(String codigo)
boolean liberarEquipo(String codigo)
```

## Cómo ejecutar

Compilar:

```powershell
cd parte3_rmi
mvn compile
```

Servidor (terminal 1, publica el servicio en el puerto 23000):

```powershell
java -cp target\classes edu.eci.arsw.parte3_rmi.LabRmiServer
```

Cliente interactivo (terminal 2):

```powershell
java -cp target\classes edu.eci.arsw.parte3_rmi.LabRmiClient
```

El cliente muestra un menú con las 4 operaciones:
=== Inventario de Laboratorios ===

Consultar todos los equipos
Consultar un equipo
Reservar un equipo
Liberar un equipo
Salir


## Ejemplo de ejecución
Opcion: 1
PC02 | Computador HP | Lab-A | DISPONIBLE
PC01 | Computador Dell | Lab-A | DISPONIBLE
RPI01 | Raspberry Pi 4 | Lab-B | DISPONIBLE
ARD01 | Arduino Mega | Lab-B | DISPONIBLE
Opcion: 3
Codigo del equipo a reservar: PC02
Reserva exitosa
Opcion: 1
PC02 | Computador HP | Lab-A | RESERVADO
...
Opcion: 3
Codigo del equipo a reservar: PC02
No se pudo reservar (no existe o ya esta reservado)
Opcion: 4
Codigo del equipo a liberar: PC02
Liberacion exitosa

## Preguntas de reflexión

### ¿Qué cambió al pasar de HTTP a RMI?

En HTTP el cliente construye una petición textual (URL, query string) que el
servidor interpreta y traduce a una operación interna; el "contrato" sigue
siendo, en el fondo, un conjunto de rutas y parámetros de texto. En RMI esa
traducción desaparece: el cliente obtiene una referencia remota al objeto
`LabEquipmentService` y llama directamente a sus métodos Java
(`reservarEquipo("PC01")`), como si el objeto estuviera en la misma máquina.
La comunicación pasa de ser "enviar un mensaje y parsear una respuesta" a ser
"invocar un método remoto", con tipos fuertes verificados en tiempo de
compilación.

### ¿Dónde está definido el contrato de comunicación?

El contrato es la interfaz Java `LabEquipmentService`, que extiende `Remote`.
Esa interfaz, compartida entre cliente y servidor, define exactamente qué
métodos existen, qué parámetros reciben y qué devuelven. Es un contrato
formal verificado por el compilador, a diferencia de las Partes 1 y 2 donde
el contrato eran convenciones de texto sin verificación automática.

### ¿Qué problemas tendría este sistema si un cliente no está escrito en Java?

RMI depende de la serialización de objetos Java (`Serializable`) y del
protocolo JRMP, que son específicos del ecosistema Java. Un cliente escrito
en otro lenguaje (Python, JavaScript, C#, etc.) no podría generar un stub
compatible ni deserializar los objetos `LabEquipment` tal como están, porque
no existe una implementación de RMI para esos lenguajes que sea
interoperable con la JVM de forma nativa. Para integrar clientes no-Java
sería necesario exponer el servicio mediante un protocolo neutral al
lenguaje, como HTTP/JSON o gRPC (que se aborda en la Parte 4).