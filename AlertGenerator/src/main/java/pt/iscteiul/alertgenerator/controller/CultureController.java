package pt.iscteiul.alertgenerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.iscteiul.alertgenerator.dao.CultureDao;
import pt.iscteiul.alertgenerator.model.Alert;
import pt.iscteiul.alertgenerator.model.Culture;

import java.util.List;

@RestController
@RequestMapping("/culture")
public class CultureController {

    @Autowired
    private CultureDao cultureDao;

    @GetMapping("/getAllCultures")
    public List<Culture> getAllCultures(Culture culture) {
        return (List<Culture>) cultureDao.findAll();
    }

}
