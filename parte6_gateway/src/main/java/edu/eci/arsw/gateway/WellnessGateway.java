package edu.eci.arsw.gateway;

import edu.eci.arsw.appointment.*;
import edu.eci.arsw.gym.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class WellnessGateway {

    private ManagedChannel appointmentChannel;
    private ManagedChannel gymChannel;
    private AppointmentServiceGrpc.AppointmentServiceBlockingStub appointmentStub;
    private GymServiceGrpc.GymServiceBlockingStub gymStub;

    public WellnessGateway() {
        appointmentChannel = ManagedChannelBuilder.forAddress("localhost", 50054).usePlaintext().build();
        gymChannel = ManagedChannelBuilder.forAddress("localhost", 50056).usePlaintext().build();
        appointmentStub = AppointmentServiceGrpc.newBlockingStub(appointmentChannel);
        gymStub = GymServiceGrpc.newBlockingStub(gymChannel);
    }

    public AppointmentResponse requestAppointment(String studentId, ServiceType serviceType, String date) {
        return appointmentStub.requestAppointment(AppointmentRequest.newBuilder()
                .setStudentId(studentId)
                .setServiceType(serviceType)
                .setDate(date)
                .build());
    }

    public GymReservationResponse reserveGymSession(String studentId, String timeSlot) {
        return gymStub.reserveSession(GymReservationRequest.newBuilder()
                .setStudentId(studentId)
                .setTimeSlot(timeSlot)
                .build());
    }

    public String reserveRecreationResource(String studentId, String resourceId) {
        // TODO: implementar cuando RecreationService este disponible (puerto 50057)
        return "RecreationService no implementado";
    }

    public void getStudentWellnessSummary(String studentId) {
        System.out.println("Resumen de bienestar para " + studentId + ":");

        System.out.println("\nCitas:");
        AppointmentList appointments = appointmentStub.getAppointments(
                StudentRequest.newBuilder().setStudentId(studentId).build());
        if (appointments.getAppointmentsList().isEmpty()) {
            System.out.println("  (sin citas registradas)");
        } else {
            for (Appointment a : appointments.getAppointmentsList()) {
                System.out.println("  - " + a.getServiceType() + " | " + a.getDate() + " | " + a.getStatus());
            }
        }

        System.out.println("\nSesiones de gimnasio:");
        GymSessionList sessions = gymStub.getSessions(
                StudentGymRequest.newBuilder().setStudentId(studentId).build());
        if (sessions.getSessionsList().isEmpty()) {
            System.out.println("  (sin sesiones registradas)");
        } else {
            for (GymSession s : sessions.getSessionsList()) {
                System.out.println("  - " + s.getTimeSlot());
            }
        }

        System.out.println("\nRecursos recreativos:");
        System.out.println("  (RecreationService no implementado)");
    }

    public void shutdown() {
        appointmentChannel.shutdown();
        gymChannel.shutdown();
    }

    public static void main(String[] args) {
        WellnessGateway gateway = new WellnessGateway();
        String studentId = "EST002";

        System.out.println("=== 1. Solicitando cita medica via Gateway ===");
        AppointmentResponse appResp = gateway.requestAppointment(studentId, ServiceType.MEDICINE, "2026-06-25");
        System.out.println("Cita creada: " + appResp.getAppointmentId() + " - Estado: " + appResp.getStatus());

        System.out.println("\n=== 2. Reservando sesion de gimnasio via Gateway ===");
        GymReservationResponse gymResp = gateway.reserveGymSession(studentId, "Miercoles 7:00am");
        System.out.println("Reserva: " + gymResp.getReservationId() + " - " + gymResp.getMessage());

        System.out.println("\n=== 3. Reservando recurso recreativo via Gateway ===");
        System.out.println(gateway.reserveRecreationResource(studentId, "BALON01"));

        System.out.println("\n=== 4. Resumen unificado de bienestar via Gateway ===");
        gateway.getStudentWellnessSummary(studentId);

        gateway.shutdown();
    }
}