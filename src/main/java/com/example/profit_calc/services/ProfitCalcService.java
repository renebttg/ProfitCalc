package com.example.profit_calc.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ProfitCalcService {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final Map<String, String> CODIGOS_SERIES = Map.of(
            "SELIC", "11",
            "CDI", "12",
            "IPCA", "433"
    );

    private String obterCodigoSerie(String indicador) {
        return CODIGOS_SERIES.getOrDefault(indicador.toUpperCase(), "11");
    }

    private double obterTaxaAtual(String codigoSerie, String dataInicial, String dataFinal, String indicador) {
        String url;

        if (indicador.equalsIgnoreCase("SELIC") || indicador.equalsIgnoreCase("CDI")) {
            url = String.format("https://api.bcb.gov.br/dados/serie/bcdata.sgs.%s/dados/ultimos/1?formato=json", codigoSerie);
        } else {
            url = (dataInicial == null || dataFinal == null) ?
                    String.format("https://api.bcb.gov.br/dados/serie/bcdata.sgs.%s/dados/ultimos/1?formato=json", codigoSerie) :
                    String.format("https://api.bcb.gov.br/dados/serie/bcdata.sgs.%s/dados?formato=json&dataInicial=%s&dataFinal=%s", codigoSerie, dataInicial, dataFinal);
        }

        ResponseEntity<JsonNode[]> response = restTemplate.getForEntity(url, JsonNode[].class);
        JsonNode[] dados = response.getBody();

        if (dados != null && dados.length > 0) {
            if (indicador.equalsIgnoreCase("IPCA") && dataInicial != null && dataFinal != null) {
                double soma = 0;
                for (JsonNode dado : dados) {
                    soma += dado.get("valor").asDouble();
                }
                return soma;
            } else {
                return dados[dados.length - 1].get("valor").asDouble();
            }
        }

        throw new RuntimeException("Nenhum dado encontrado para o indicador especificado.");
    }

    public Map<String, Object> calcularRendimento(double capital, int dias, String indicador, String dataInicial, String dataFinal) {
        if (capital <= 0 || dias <= 0) {
            throw new IllegalArgumentException("Capital e dias devem ser maiores que zero.");
        }

        String codigoSerie = obterCodigoSerie(indicador);
        double taxaBruta = obterTaxaAtual(codigoSerie, dataInicial, dataFinal, indicador);
        double taxaDecimal = taxaBruta / 100.0;

        double valorFinal;

        if (indicador.equalsIgnoreCase("IPCA")) {
            valorFinal = capital * (1 + taxaDecimal);
        } else {
            valorFinal = capital * Math.pow(1 + taxaDecimal, dias / 252.0);
        }

        double rendimento = valorFinal - capital;
        double percentual = (rendimento / capital) * 100;
        String percentualFormatado = String.format("%.2f%%", percentual).replace('.', ',');

        return Map.of(
                "capital", capital,
                "dias", dias,
                "indicador", indicador.trim().toUpperCase(),
                "taxa", taxaBruta,
                "rendimento", Math.round(rendimento * 100.0) / 100.0,
                "valorFinal", Math.round(valorFinal * 100.0) / 100.0,
                "percentualRendimento", percentualFormatado
        );
    }
}
