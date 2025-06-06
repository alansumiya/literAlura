package br.com.alura.literAlura.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "livros")
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private Autor autor;
    private String idioma;
    private Integer numeroDonwloads;

    public Livro(){}

    public Livro(DadosLivro dadosLivro){
        this.titulo = dadosLivro.titulo();
        if(dadosLivro.autores() != null && !dadosLivro.autores().isEmpty()){
            this.autor = new Autor(dadosLivro.autores().get(0));
        } else {
            this.autor = null;
        }
        this.idioma = dadosLivro.idioma();
        this.numeroDonwloads = dadosLivro.numeroDownloads();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public Integer getNumeroDonwloads() {
        return numeroDonwloads;
    }

    public void setNumeroDonwloads(Integer numeroDonwloads) {
        this.numeroDonwloads = numeroDonwloads;
    }

    @Override
    public String toString() {
        String autorNome = (autor != null) ? autor.getNome() : "Desconhecido";
        String primeiroIdioma = (idioma != null && !idioma.isEmpty()) ? idioma : "N/A";


        return "\n----------- LIVRO -----------------"+
                "\nTítulo: '" + titulo + '\'' +
                "\nAutor: '" + autorNome + '\'' +
                "\nIdiomas: '" + primeiroIdioma + '\'' +
                "\nDownloads: " + numeroDonwloads +
                "\n----------------------------------" +
                "\n";
    }
}
