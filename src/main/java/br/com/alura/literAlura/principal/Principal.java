package br.com.alura.literAlura.principal;

import br.com.alura.literAlura.model.DadosAutor;
import br.com.alura.literAlura.model.DadosLivro;
import br.com.alura.literAlura.model.DadosResultado;
import br.com.alura.literAlura.service.ConsumoApi;
import br.com.alura.literAlura.service.ConverteDados;

import java.util.Scanner;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados converte = new ConverteDados();
    private final String ENDERECO = "https://gutendex.com/books/?search=";


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
            }


        }

    }

    private void buscarLivroWeb() {
        System.out.print("Digite o nome do livro para busca: ");
        var nomeLivro = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeLivro.replace(" ", "+"));
        DadosResultado resposta = converte.obterDados(json, DadosResultado.class);

        if (resposta != null && !resposta.resultados().isEmpty()){
            DadosLivro dadosLivro = resposta.resultados().get(0);
            System.out.println("\n--- DADOS DO LIVRO ENCONTRADO ---");
            System.out.println("Título: "+ dadosLivro.titulo());

            // Itera sobre a lista de autores para imprimir cada um
            if (dadosLivro.autores() != null && !dadosLivro.autores().isEmpty()) {
                DadosAutor primeiroAutor = dadosLivro.autores().get(0);
                System.out.println("Autor: " + primeiroAutor.nome());
            } else {
                System.out.println("Autores: Não informado");
            }

            System.out.println("Idioma: " + dadosLivro.idioma());

            System.out.println("Número de Downloads: " + dadosLivro.numeroDownloads());
            System.out.println("----------------------------------\n");

        } else {
            System.out.println("Livro não encontrado ou sem resultados na API.");
        }

    }

}
