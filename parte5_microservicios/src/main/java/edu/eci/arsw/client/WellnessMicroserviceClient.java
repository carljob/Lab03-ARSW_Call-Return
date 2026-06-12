package edu.eci.arsw.client;

import edu.eci.arsw.appointment.*;
import edu.eci.arsw.gym.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class WellnessMicroserviceClient {
    public static void main(String[] args) {
        // Canal hacia AppointmentService (puerto 50054)
        ManagedChannel appointmentChannel = ManagedChannelBuilder
                .forAddress("localhost", 50054)
                .usePlaintext()
                .build();

        // Canal hacia GymService (puerto 50056)
        ManagedChannel gymChannel = ManagedChannelBuilder
                .forAddress("localhost", 50056)
                .usePlaintext()
                .build();

        AppointmentServiceGrpc.AppointmentServiceBlockingStub appointmentStub =
                AppointmentServiceGrpc.newBlockingStub(appointmentChannel);

        GymServiceGrpc.GymServiceBlockingStub gymStub =
                GymServiceGrpc.newBlockingStub(gymChannel);

        String studentId = "EST001";

        System.out.println("=== Solicitando cita de psicologia (AppointmentService:50054) ===");
        AppointmentResponse appResp = appointmentStub.requestAppointment(AppointmentRequest.newBuilder()
                .setStudentId(studentId)
                .setServiceType(ServiceType.PSYCHOLOGY)
                .setDate("2026-06-20")
                .build());
        System.out.println("Cita creada: " + appResp.getAppointmentId() + " - Estado: " + appResp.getStatus());

        System.out.println("\n=== Reservando sesion de gimnasio (GymService:50056) ===");
        GymReservationResponse gymResp = gymStub.reserveSession(GymReservationRequest.newBuilder()
                .setStudentId(studentId)
                .setTimeSlot("Lunes 6:00am")
                .build());
        System.out.println("Reserva: " + gymResp.getReservationId() + " - " + gymResp.getMessage());

        System.out.println("\n=== Citas del estudiante " + studentId + " ===");
        AppointmentList appList = appointmentStub.getAppointments(StudentRequest.newBuilder()
                .setStudentId(studentId)
                .build());
        for (Appointment a : appList.getAppointmentsList()) {
            System.out.println(a.getId() + " | " + a.getServiceType() + " | " + a.getDate() + " | " + a.getStatus());
        }

        System.out.println("\n=== Sesiones de gimnasio del estudiante " + studentId + " ===");
        GymSessionList gymList = gymStub.getSessions(StudentGymRequest.newBuilder()
                .setStudentId(studentId)
                .build());
        for (GymSession s : gymList.getSessionsList()) {
            System.out.println(s.getId() + " | " + s.getTimeSlot());
        }

        appointmentChannel.shutdown();
        gymChannel.shutdown();
    }
}