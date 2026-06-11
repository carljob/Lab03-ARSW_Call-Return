package edu.eci.arsw.parte3_rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class LabRmiClient {
    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 23000);
        LabEquipmentService service = (LabEquipmentService) registry.lookup("labService");

        System.out.println("=== Todos los equipos ===");
        List<String> equipos = service.consultarEquipos();
        equipos.forEach(System.out::println);

        System.out.println("\n=== Reservando PC01 ===");
        System.out.println(service.reservarEquipo("PC01") ? "Reserva exitosa" : "No se pudo reservar");

        System.out.println("\n=== Estado PC01 ===");
        System.out.println(service.consultarEquipo("PC01"));

        System.out.println("\n=== Liberando PC01 ===");
        System.out.println(service.liberarEquipo("PC01") ? "Liberacion exitosa" : "No se pudo liberar");
    }
}