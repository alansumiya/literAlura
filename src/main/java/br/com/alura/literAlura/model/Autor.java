package br.com.alura.literAlura.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "autores")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String nome;
    private Integer dataNascimento;
    private Integer dataFalecimento;
    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Livro> livros = new ArrayList<>();

    public Autor(){}

    public Autor(DadosAutor dadosAutor) {
        this.nome = dadosAutor.nome();
        this.dataNascimento = dadosAutor.dataNascimento();
        this.dataFalecimento = dadosAutor.dataFalecimento();
    }

    public void adicionarLivro(Livro livro){
        this.livros.add(livro);
        livro.setAutor(this);
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Integer dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Integer getDataFalecimento() {
        return dataFalecimento;
    }

    public void setDataFalecimento(Integer dataFalecimento) {
        this.dataFalecimento = dataFalecimento;
    }

    public List<Livro> getLivros() {
        return livros;
    }

    public void setLivros(List<Livro> livros) {
        this.livros = livros;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n----------- AUTOR ------------");
        sb.append("\nNome= ").append(nome);
        sb.append("\nData de Nascimento= ").append(dataNascimento != null ? dataNascimento : "N/A");
        sb.append("\nData de Falecimento= ").append(dataFalecimento != null ? dataFalecimento : "N/A");

        sb.append("\nLivros: ");
        if (livros != null && !livros.isEmpty()){
            for (int i = 0; i < livros.size(); i++) {
                sb.append("\n - ").append(livros.get(i).getTitulo());
            }
        }else {
            sb.append("Nenhum livro registrado.");
        }
        sb.append("\n-----------------------------------");
        return sb.toString();
    }
}
