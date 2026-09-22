package utn.back.mordiscoapi.service;

public interface MaintenanceService {

    MaintenanceResult runMaintenance();

    record MaintenanceResult(String status) {
    }
}
