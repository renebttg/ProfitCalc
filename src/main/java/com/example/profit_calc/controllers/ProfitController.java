package com.example.profit_calc.controllers;

import com.example.profit_calc.services.ProfitCalcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profit-calc")
public class ProfitController {

    @Autowired
    ProfitCalcService profitCalcService;


    

}
