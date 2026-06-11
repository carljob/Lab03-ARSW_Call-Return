package edu.eci.arsw.parte1_tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class SalonClient {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Operaciones disponibles:");
        System.out.println("  CONSULTAR_SALON,E301");
        System.out.println("  RESERVAR_SALON,E301");
        System.out.println("  LIBERAR_SALON,E301");
        System.out.println("Escribe 'salir' para terminar");

        while (true) {
            System.out.print("Ingrese su solicitud: ");
            String request = scanner.nextLine();
            if (request.equalsIgnoreCase("salir")) break;

            Socket socket = new Socket("127.0.0.1", 35000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            out.println(request);
            System.out.println("Respuesta: " + in.readLine());

            in.close();
            out.close();
            socket.close();
        }

        System.out.println("Cliente cerrado.");
    }
}