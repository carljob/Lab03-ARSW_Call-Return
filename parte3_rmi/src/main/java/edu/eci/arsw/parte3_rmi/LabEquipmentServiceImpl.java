package edu.eci.arsw.parte3_rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LabEquipmentServiceImpl extends UnicastRemoteObject implements LabEquipmentService {
    private Map<String, LabEquipment> equipos = new HashMap<>();

    public LabEquipmentServiceImpl() throws RemoteException {
        equipos.put("PC01", new LabEquipment("PC01", "Computador Dell", "Lab-A"));
        equipos.put("PC02", new LabEquipment("PC02", "Computador HP", "Lab-A"));
        equipos.put("RPI01", new LabEquipment("RPI01", "Raspberry Pi 4", "Lab-B"));
        equipos.put("ARD01", new LabEquipment("ARD01", "Arduino Mega", "Lab-B"));
    }

    @Override
    public List<String> consultarEquipos() throws RemoteException {
        List<String> lista = new ArrayList<>();
        equipos.values().forEach(e -> lista.add(e.toString()));
        return lista;
    }

    @Override
    public String consultarEquipo(String codigo) throws RemoteException {
        LabEquipment e = equipos.get(codigo);
        return e == null ? "ERROR: equipo no encontrado" : e.toString();
    }

    @Override
    public boolean reservarEquipo(String codigo) throws RemoteException {
        LabEquipment e = equipos.get(codigo);
        if (e == null || !e.isDisponible()) return false;
        e.setDisponible(false);
        return true;
    }

    @Override
    public boolean liberarEquipo(String codigo) throws RemoteException {
        LabEquipment e = equipos.get(codigo);
        if (e == null || e.isDisponible()) return false;
        e.setDisponible(true);
        return true;
    }
}