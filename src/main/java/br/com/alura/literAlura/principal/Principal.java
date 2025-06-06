package br.com.alura.literAlura.principal;

import br.com.alura.literAlura.model.*;
import br.com.alura.literAlura.repository.AutorRepository;
import br.com.alura.literAlura.repository.LivroRepository;
import br.com.alura.literAlura.service.ConsumoApi;
import br.com.alura.literAlura.service.ConverteDados;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
@Component
public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados converte = new ConverteDados();
    private final String ENDERECO = "https://gutendex.com/books/?search=";

    private Autor autor = null;
    private DadosAutor dadosPrimeiroAutor = null;

    private List<Livro> livros = new ArrayList<>();

    @Autowired
    private LivroRepository livroRepository;
    @Autowired
    private AutorRepository autorRepository;


    public void exibeMenu(){
        var opcao = 0;
        while (opcao != 6) {
            var menu = ("""
                    /////////////////////////////////////////////
                    ###                                       ###
                    ###               Pesquisa                ###
                    ###                  de                   ###
                    ###                Livros                 ###
                    ###                                       ###
                    /////////////////////////////////////////////
                    Escolha as opções abaixo:
                    1 - Buscar livro pelo título
                    2 - Listar livros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos em um determinado ano
                    5 - Listar livros em um determinado idioma
                    6 - sair
                    """);
            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao){
                case 1:
                    buscarLivroWeb();
                    break;
                case 2:
                    listarLivrosRegistrados();
                    break;
                case 6:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }


        }

    }

    private void buscarLivroWeb() {
        System.out.print("Digite o nome do livro para busca: ");
        var nomeLivro = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeLivro.replace(" ", "+"));
        DadosResultado resposta = converte.obterDados(json, DadosResultado.class);


        if (resposta != null && !resposta.resultados().isEmpty()) {
            DadosLivro dadosLivro = resposta.resultados().get(0);

            if (dadosLivro.autores() != null && !dadosLivro.autores().isEmpty()) {
                dadosPrimeiroAutor = dadosLivro.autores().get(0);
                String nomePrimeiroAutor = dadosPrimeiroAutor.nome();

                Optional<Autor> autorExistente = autorRepository.findByNome(nomePrimeiroAutor);
                if (autorExistente.isPresent()) {
                    autor = autorExistente.get();
                    System.out.println("Autor já existente no banco de dados: " + autor.getNome());
                } else {
                    autor = new Autor(dadosPrimeiroAutor);
                    autorRepository.save(autor);
                    System.out.println("Novo autor salvo: " + autor.getNome());
                }

            } else {
                System.out.println("Autores: Não informado");
            }

            Livro livro = new Livro(dadosLivro);
            if (autor != null) {
                livro.setAutor(autor);
                autor.adicionarLivro(livro);
            }
            livroRepository.save(livro);
            imprimirDadosLivro(dadosLivro);

        } else {
            System.out.println("Livro não encontrado ou sem resultados na API.");
        }
    }

    private void listarLivrosRegistrados() {
        livros = livroRepository.findAll();

        if (livros.isEmpty()){
            System.out.println("\nNão há livros registrados no banco de dados.");
        }else {
            System.out.println("\n--- LIVROS REGISTRADOS ---");
            livros.forEach(livro -> System.out.println(livro));
            System.out.println("----------------------------");
        }

    }

    private void imprimirDadosLivro(DadosLivro dadosLivro){
        System.out.println("\n--- DADOS DO LIVRO ENCONTRADO ---");
        System.out.println("Título: " + dadosLivro.titulo());
        System.out.println("Autor: " + dadosPrimeiroAutor.nome());
        System.out.println("Idioma: " + dadosLivro.idioma());
        System.out.println("Número de Downloads: " + dadosLivro.numeroDownloads());
        System.out.println("----------------------------------\n");
    }

}
