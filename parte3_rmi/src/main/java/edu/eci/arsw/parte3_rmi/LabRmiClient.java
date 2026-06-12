package edu.eci.arsw.parte3_rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

public class LabRmiClient {
    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 23000);
        LabEquipmentService service = (LabEquipmentService) registry.lookup("labService");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nInventario de Laboratorios");
            System.out.println("1. Consultar todos los equipos");
            System.out.println("2. Consultar un equipo");
            System.out.println("3. Reservar un equipo");
            System.out.println("4. Liberar un equipo");
            System.out.println("0. Salir");
            System.out.print("Opcion: ");
            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    List<String> equipos = service.consultarEquipos();
                    equipos.forEach(System.out::println);
                    break;
                case "2":
                    System.out.print("Codigo del equipo: ");
                    String codigo1 = scanner.nextLine();
                    System.out.println(service.consultarEquipo(codigo1));
                    break;
                case "3":
                    System.out.print("Codigo del equipo a reservar: ");
                    String codigo2 = scanner.nextLine();
                    boolean reservado = service.reservarEquipo(codigo2);
                    System.out.println(reservado ? "Reserva exitosa" : "No se pudo reservar (no existe o ya esta reservado)");
                    break;
                case "4":
                    System.out.print("Codigo del equipo a liberar: ");
                    String codigo3 = scanner.nextLine();
                    boolean liberado = service.liberarEquipo(codigo3);
                    System.out.println(liberado ? "Liberacion exitosa" : "No se pudo liberar (no existe o ya esta disponible)");
                    break;
                case "0":
                    System.out.println("Cliente cerrado.");
                    return;
                default:
                    System.out.println("Opcion invalida");
            }
        }
    }
}