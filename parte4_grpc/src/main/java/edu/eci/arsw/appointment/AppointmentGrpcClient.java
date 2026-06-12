package edu.eci.arsw.appointment;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class AppointmentGrpcClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50054)
                .usePlaintext()
                .build();

        AppointmentServiceGrpc.AppointmentServiceBlockingStub stub =
                AppointmentServiceGrpc.newBlockingStub(channel);

        System.out.println("=== Solicitando cita de psicologia ===");
        AppointmentResponse resp1 = stub.requestAppointment(AppointmentRequest.newBuilder()
                .setStudentId("EST001")
                .setServiceType(ServiceType.PSYCHOLOGY)
                .setDate("2026-06-15")
                .build());
        System.out.println("Cita creada: " + resp1.getAppointmentId() + " - Estado: " + resp1.getStatus());

        System.out.println("\n=== Solicitando cita de medicina ===");
        AppointmentResponse resp2 = stub.requestAppointment(AppointmentRequest.newBuilder()
                .setStudentId("EST001")
                .setServiceType(ServiceType.MEDICINE)
                .setDate("2026-06-18")
                .build());
        System.out.println("Cita creada: " + resp2.getAppointmentId() + " - Estado: " + resp2.getStatus());

        System.out.println("\n=== Citas del estudiante EST001 ===");
        AppointmentList list = stub.getAppointments(StudentRequest.newBuilder()
                .setStudentId("EST001")
                .build());
        for (Appointment a : list.getAppointmentsList()) {
            System.out.println(a.getId() + " | " + a.getServiceType() + " | " + a.getDate() + " | " + a.getStatus());
        }

        System.out.println("\n=== Cancelando primera cita ===");
        CancelResponse cancelResp = stub.cancelAppointment(CancelRequest.newBuilder()
                .setAppointmentId(resp1.getAppointmentId())
                .build());
        System.out.println(cancelResp.getMessage());

        System.out.println("\n=== Citas del estudiante EST001 (despues de cancelar) ===");
        AppointmentList list2 = stub.getAppointments(StudentRequest.newBuilder()
                .setStudentId("EST001")
                .build());
        for (Appointment a : list2.getAppointmentsList()) {
            System.out.println(a.getId() + " | " + a.getServiceType() + " | " + a.getDate() + " | " + a.getStatus());
        }

        channel.shutdown();
    }
}