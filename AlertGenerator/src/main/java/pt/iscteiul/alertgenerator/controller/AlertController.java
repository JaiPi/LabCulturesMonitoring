package pt.iscteiul.alertgenerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pt.iscteiul.alertgenerator.dao.AlertDao;
import pt.iscteiul.alertgenerator.model.Alert;
import pt.iscteiul.alertgenerator.service.AlertService;
import pt.iscteiul.alertgenerator.service.PredictiveAlertService;

import java.util.List;

@RestController
@RequestMapping("/alert")
public class AlertController {

    @Autowired
    private AlertDao alertDao;

    @Autowired
    AlertService alertService;

    @Autowired
    PredictiveAlertService predictiveAlertService;

    @PostMapping("/saveAlert")
    public String saveAlert(@RequestBody Alert alert) {
        alertDao.save(alert);
        return "Success injecting alert";
    }

    @GetMapping("/getAlerts")
    public List<Alert> getAlerts() {
        return (List<Alert>) alertDao.findAll();
    }

    @GetMapping("/generateRealTimeAlerts")
    public String generateRealTimeAlerts() throws InterruptedException {
        alertService.startRealTimeAlert();
        return "Generating Real Time Alerts";
    }

    @GetMapping("/generatePredictiveAlerts")
    public String generatePredictiveAlerts() throws InterruptedException {
        predictiveAlertService.startPredictiveAlert();
        return "Generating Predictive Alerts";
    }
}
