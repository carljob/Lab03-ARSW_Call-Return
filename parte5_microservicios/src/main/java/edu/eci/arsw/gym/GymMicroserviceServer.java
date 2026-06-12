package edu.eci.arsw.gym;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GymMicroserviceServer {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50056)
                .addService(new GymServiceImpl())
                .build();
        server.start();
        System.out.println("GymService (microservicio) iniciado en puerto 50056");
        server.awaitTermination();
    }

    static class GymServiceImpl extends GymServiceGrpc.GymServiceImplBase {
        private Map<String, GymSession> sessions = new HashMap<>();

        @Override
        public void reserveSession(GymReservationRequest request, StreamObserver<GymReservationResponse> responseObserver) {
            String id = UUID.randomUUID().toString().substring(0, 8);

            GymSession session = GymSession.newBuilder()
                    .setId(id)
                    .setStudentId(request.getStudentId())
                    .setTimeSlot(request.getTimeSlot())
                    .build();

            sessions.put(id, session);

            GymReservationResponse response = GymReservationResponse.newBuilder()
                    .setReservationId(id)
                    .setSuccess(true)
                    .setMessage("Reserva de gimnasio exitosa")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void getSessions(StudentGymRequest request, StreamObserver<GymSessionList> responseObserver) {
            List<GymSession> resultado = new ArrayList<>();
            for (GymSession s : sessions.values()) {
                if (s.getStudentId().equals(request.getStudentId())) {
                    resultado.add(s);
                }
            }

            GymSessionList list = GymSessionList.newBuilder()
                    .addAllSessions(resultado)
                    .build();

            responseObserver.onNext(list);
            responseObserver.onCompleted();
        }
    }
}