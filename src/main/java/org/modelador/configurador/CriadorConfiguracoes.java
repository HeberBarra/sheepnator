package org.modelador.configurador;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.modelador.configurador.verificador.json.AtributoJsonConfiguracao;
import org.modelador.configurador.verificador.json.AtributoJsonPaleta;
import org.modelador.configurador.verificador.json.JsonVerificadorConfiguracoes;
import org.modelador.configurador.verificador.json.JsonVerificadorPaleta;
import org.modelador.logger.JavaLogger;
import org.slf4j.LoggerFactory;

public class CriadorConfiguracoes {

    private static final Logger logger = JavaLogger.obterLogger(CriadorConfiguracoes.class.getName());
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CriadorConfiguracoes.class);
    private final Map<String, List<Map<String, String>>> configuracaoPadrao;
    private final Map<String, List<Map<String, String>>> paletaPadrao;

    public CriadorConfiguracoes() {
        configuracaoPadrao = new LinkedHashMap<>();
        paletaPadrao = new LinkedHashMap<>();
        paletaPadrao.put("paleta", new ArrayList<>());
    }

    public void criarPastaConfiguracao(String pastaConfiguracao) {
        if (new File(pastaConfiguracao).mkdir()) {
            logger.info("Pasta %s criada com sucesso.%n".formatted(pastaConfiguracao));
        }
    }

    public void pegarConfiguracaoPadrao(JsonVerificadorConfiguracoes verificadorConfiguracoes) {
        for (AtributoJsonConfiguracao atributo : verificadorConfiguracoes.getAtributos()) {
            if (!configuracaoPadrao.containsKey(atributo.getCategoria())) {
                configuracaoPadrao.put(atributo.getCategoria(), new ArrayList<>());
            }

            configuracaoPadrao.get(atributo.getCategoria()).add(atributo.converterParaMap());
        }
    }

    public void criarArquivoConfiguracoes(String pastaConfiguracao, String arquivoConfiguracoes) {
        String dadosToml = ConversorToml.converterMapConfiguracoesParaStringToml(configuracaoPadrao);
        criarArquivo(dadosToml, pastaConfiguracao + "/" + arquivoConfiguracoes);
    }

    public void criarArquivoPaleta(String pastaConfiguracao, String arquivoPaleta) {
        String dadosToml = ConversorToml.converterMapPaletaParaStringToml(paletaPadrao);
        criarArquivo(dadosToml, pastaConfiguracao + "/" + arquivoPaleta);
    }

    public void pegarPaletaPadrao(JsonVerificadorPaleta verificadorPaleta) {
        List<Map<String, String>> atributos = paletaPadrao.get("paleta");

        for (AtributoJsonPaleta atributo : verificadorPaleta.getAtributos()) {
            atributos.add(atributo.converterParaMap());
        }
    }

    public void criarArquivo(String dadosToml, String caminhoArquivo) {
        File arquivo = new File(caminhoArquivo);

        if (arquivo.length() != 0) {
            return;
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            bufferedWriter.write(dadosToml);
        } catch (IOException e) {
            logger.severe("Falha ao tentar criar o arquivo %s. Erro: %s.".formatted(caminhoArquivo, e.getMessage()));
            logger.severe("Encerrando o programa...");
            System.exit(0);
        }
    }
}
