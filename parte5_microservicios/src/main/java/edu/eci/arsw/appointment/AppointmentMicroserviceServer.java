package edu.eci.arsw.appointment;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AppointmentMicroserviceServer {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50054)
                .addService(new AppointmentServiceImpl())
                .build();
        server.start();
        System.out.println("AppointmentService (microservicio) iniciado en puerto 50054");
        server.awaitTermination();
    }

    static class AppointmentServiceImpl extends AppointmentServiceGrpc.AppointmentServiceImplBase {
        private Map<String, Appointment> appointments = new HashMap<>();

        @Override
        public void requestAppointment(AppointmentRequest request, StreamObserver<AppointmentResponse> responseObserver) {
            String id = UUID.randomUUID().toString().substring(0, 8);

            Appointment appointment = Appointment.newBuilder()
                    .setId(id)
                    .setStudentId(request.getStudentId())
                    .setServiceType(request.getServiceType())
                    .setDate(request.getDate())
                    .setStatus(AppointmentStatus.REQUESTED)
                    .build();

            appointments.put(id, appointment);

            AppointmentResponse response = AppointmentResponse.newBuilder()
                    .setAppointmentId(id)
                    .setStatus(AppointmentStatus.REQUESTED)
                    .setSuccess(true)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void cancelAppointment(CancelRequest request, StreamObserver<CancelResponse> responseObserver) {
            Appointment appointment = appointments.get(request.getAppointmentId());
            CancelResponse response;

            if (appointment == null) {
                response = CancelResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("Cita no encontrada")
                        .build();
            } else {
                Appointment cancelada = appointment.toBuilder()
                        .setStatus(AppointmentStatus.CANCELLED)
                        .build();
                appointments.put(request.getAppointmentId(), cancelada);

                response = CancelResponse.newBuilder()
                        .setSuccess(true)
                        .setMessage("Cita cancelada correctamente")
                        .build();
            }

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void getAppointments(StudentRequest request, StreamObserver<AppointmentList> responseObserver) {
            List<Appointment> resultado = new ArrayList<>();
            for (Appointment a : appointments.values()) {
                if (a.getStudentId().equals(request.getStudentId())) {
                    resultado.add(a);
                }
            }

            AppointmentList list = AppointmentList.newBuilder()
                    .addAllAppointments(resultado)
                    .build();

            responseObserver.onNext(list);
            responseObserver.onCompleted();
        }
    }
}