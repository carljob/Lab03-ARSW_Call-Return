package edu.eci.arsw.parte1_tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SalonServer {
    public static void main(String[] args) throws Exception {
        SalonRepository repository = new SalonRepository();
        ServerSocket serverSocket = new ServerSocket(35000);
        System.out.println("SalonServer TCP escuchando en puerto 35000...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String request = in.readLine();
            String response = processRequest(request, repository);
            out.println(response);

            in.close();
            out.close();
            clientSocket.close();
        }
    }

    private static String processRequest(String request, SalonRepository repository) {
        if (request == null) return "ERROR_OPERACION_INVALIDA";

        String[] parts = request.split(",");
        if (parts.length != 2) return "ERROR_OPERACION_INVALIDA";

        String operacion = parts[0].trim();
        String salonId = parts[1].trim();

        Salon salon = repository.findById(salonId);
        if (salon == null) return "ERROR_SALON_NO_EXISTE";

        switch (operacion) {
            case "CONSULTAR_SALON":
                return salon.isDisponible() ? "SALON_DISPONIBLE" : "SALON_RESERVADO";
            case "RESERVAR_SALON":
                if (!salon.isDisponible()) return "ERROR_OPERACION_INVALIDA";
                salon.setDisponible(false);
                return "RESERVA_EXITOSA";
            case "LIBERAR_SALON":
                if (salon.isDisponible()) return "ERROR_OPERACION_INVALIDA";
                salon.setDisponible(true);
                return "LIBERACION_EXITOSA";
            default:
                return "ERROR_OPERACION_INVALIDA";
        }
    }
}