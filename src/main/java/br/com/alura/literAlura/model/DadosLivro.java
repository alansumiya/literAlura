package br.com.alura.literAlura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosLivro(@JsonAlias("title") String titulo,
                         @JsonAlias("authors") List<DadosAutor> autores,
                         @JsonAlias("languages") List<String> idiomasApi,
                         @JsonAlias("download_count") Integer numeroDownloads) {

    public String idioma(){
        return (idiomasApi != null && !idiomasApi.isEmpty()) ? idiomasApi.get(0) : "N/A";
    }
}
