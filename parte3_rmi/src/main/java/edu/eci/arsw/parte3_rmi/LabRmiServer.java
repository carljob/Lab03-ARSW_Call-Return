package edu.eci.arsw.parte3_rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LabRmiServer {
    public static void main(String[] args) throws Exception {
        LabEquipmentService service = new LabEquipmentServiceImpl();
        Registry registry = LocateRegistry.createRegistry(23000);
        registry.rebind("labService", service);
        System.out.println("LabEquipmentService RMI publicado en puerto 23000...");
    }
}