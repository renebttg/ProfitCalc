package com.example.profit_calc.controllers;

import com.example.profit_calc.services.ProfitCalcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/profit")
public class ProfitController {

    @Autowired
    ProfitCalcService profitCalcService;

    @GetMapping("/calcular")
    public Map<String, Object> calcularRendimento(
            @RequestParam double capital,
            @RequestParam int dias,
            @RequestParam String indicador,
            @RequestParam(required = false) String dataInicial,
            @RequestParam(required = false) String dataFinal) {

        return profitCalcService.calcularRendimento(capital, dias, indicador, dataInicial, dataFinal);

    }


}
