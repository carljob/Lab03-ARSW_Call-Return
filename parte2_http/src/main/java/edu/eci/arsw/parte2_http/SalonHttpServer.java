package edu.eci.arsw.parte2_http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class SalonHttpServer {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        SalonRepository repository = new SalonRepository();

        server.createContext("/rooms", new RoomsHandler(repository));
        server.createContext("/rooms/reserve", new ReserveHandler(repository));
        server.createContext("/rooms/release", new ReleaseHandler(repository));

        server.setExecutor(null);
        server.start();
        System.out.println("SalonHttpServer escuchando en http://localhost:8080/rooms");
    }

    static class RoomsHandler implements HttpHandler {
        private SalonRepository repository;
        public RoomsHandler(SalonRepository r) { this.repository = r; }

        @Override
        public void handle(HttpExchange exchange) {
            try {
                String query = exchange.getRequestURI().getQuery();
                String response;

                if (query != null && query.startsWith("id=")) {
                    String id = query.substring(3);
                    Salon salon = repository.findById(id);
                    if (salon == null) {
                        response = "<html><body><h1>ERROR_SALON_NO_EXISTE</h1></body></html>";
                    } else {
                        response = "<html><body><h1>" + salon.toText() + "</h1></body></html>";
                    }
                } else {
                    StringBuilder sb = new StringBuilder("<html><body><ul>");
                    for (Salon s : repository.findAll()) {
                        sb.append("<li>").append(s.toText()).append("</li>");
                    }
                    sb.append("</ul></body></html>");
                    response = sb.toString();
                }

                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class ReserveHandler implements HttpHandler {
        private SalonRepository repository;
        public ReserveHandler(SalonRepository r) { this.repository = r; }

        @Override
        public void handle(HttpExchange exchange) {
            try {
                String query = exchange.getRequestURI().getQuery();
                String response;

                if (query != null && query.startsWith("id=")) {
                    String id = query.substring(3);
                    Salon salon = repository.findById(id);
                    if (salon == null) {
                        response = "ERROR_SALON_NO_EXISTE";
                    } else if (!salon.isDisponible()) {
                        response = "ERROR_OPERACION_INVALIDA";
                    } else {
                        salon.setDisponible(false);
                        response = "RESERVA_EXITOSA";
                    }
                } else {
                    response = "ERROR_OPERACION_INVALIDA";
                }

                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class ReleaseHandler implements HttpHandler {
        private SalonRepository repository;
        public ReleaseHandler(SalonRepository r) { this.repository = r; }

        @Override
        public void handle(HttpExchange exchange) {
            try {
                String query = exchange.getRequestURI().getQuery();
                String response;

                if (query != null && query.startsWith("id=")) {
                    String id = query.substring(3);
                    Salon salon = repository.findById(id);
                    if (salon == null) {
                        response = "ERROR_SALON_NO_EXISTE";
                    } else if (salon.isDisponible()) {
                        response = "ERROR_OPERACION_INVALIDA";
                    } else {
                        salon.setDisponible(true);
                        response = "LIBERACION_EXITOSA";
                    }
                } else {
                    response = "ERROR_OPERACION_INVALIDA";
                }

                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}