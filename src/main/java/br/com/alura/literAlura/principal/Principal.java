package br.com.alura.literAlura.principal;

import br.com.alura.literAlura.model.*;
import br.com.alura.literAlura.repository.AutorRepository;
import br.com.alura.literAlura.repository.LivroRepository;
import br.com.alura.literAlura.service.ConsumoApi;
import br.com.alura.literAlura.service.ConverteDados;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados converte = new ConverteDados();
    private final String ENDERECO = "https://gutendex.com/books/?search=";
    private Autor autor = null;
    private DadosAutor dadosPrimeiroAutor = null;
    private List<Livro> livros = new ArrayList<>();
    private List<Autor> autores = new ArrayList<>();



    @Autowired
    private LivroRepository livroRepository;
    @Autowired
    private AutorRepository autorRepository;


    public void exibeMenu(){
        int opcao = -1;
        while (opcao != 0) {
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
                                    EXTRA
                    6 - Exibir estatísticas de Downloads
                    7 - TOP 10 dos livros mais baixados
                    8 - Buscar autor pelo nome
                    9 - Buscar autor pelo ano de falecimento
                    
                    
                    0 - sair
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
                case 3:
                    listasAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivosDeterminadoAno();
                    break;
                case 5:
                    listarLivroDeterminadoIdioma();
                    break;
                case 6:
                    exibirEstatisticasDeDownloads();
                    break;
                case 7:
                    top10LivrosMaisBaixados();
                    break;
                case 8:
                    buscarAutorPorNome();
                    break;
                case 9:
                    buscarAutorPelaDataFalecimento();
                    break;
                case 0:
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
            System.out.println("\n--- DADOS DO LIVRO ENCONTRADO ---");
            System.out.println("Título: " + dadosLivro.titulo());
            System.out.println("Autor: " + dadosPrimeiroAutor.nome());
            System.out.println("Idioma: " + dadosLivro.idioma());
            System.out.println("Número de Downloads: " + dadosLivro.numeroDownloads());
            System.out.println("----------------------------------\n");
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
        }

    }

    private void listasAutoresRegistrados() {
        autores = autorRepository.findAll();
        if (autores.isEmpty()){
            System.out.println("\nNão há autores registrados no banco de dados.");
        }else {
            System.out.println("\n--- AUTORES REGISTRADOS ---");
            autores.forEach(autor1 -> System.out.println(autor1));
        }
    }

    private void listarAutoresVivosDeterminadoAno() {
        Integer anoReferencia = null;
        boolean entradaValida = false;

        while (!entradaValida) {
            System.out.print("Digite o ano desejado para saber se o(a) autor(a) estava vivo(a): ");
            try {
                anoReferencia = leitura.nextInt();
                leitura.nextLine();
                if (anoReferencia < 0 || anoReferencia > 2025) {
                    System.out.println("Ano inválido. Por favor digite um ano entre 0 e 2025");
                } else {
                    entradaValida = true;
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida! Por favor, digite um ano válido.");
                leitura.nextLine();
            }
        }
            //usando JPQL
            List<Autor> autoresVivos = autorRepository.autorVivoDeterminadoAno(anoReferencia);
            //usando derived query
            //List<Autor> autoresVivos = autorRepository.findByDataNascimentoLessThanEqualAndDataFalecimentoGreaterThanEqualOrDataFalecimentoIsNull(anoReferencia, anoReferencia);
            if (autoresVivos.isEmpty()) {
                System.out.println("\nNão há autores vivos nesse ano no banco de dados.");
            } else {
                System.out.println("\n--- AUTORES VIVOS EM " + anoReferencia + " ---");
                autoresVivos.forEach(System.out::println);
                System.out.println("----------------------------------------------");
            }


    }

    private void listarLivroDeterminadoIdioma() {
        String idioma = null;
        boolean idiomaValido = false;

        while (!idiomaValido) {
            System.out.println("\n--- LISTAR LIVROS POR IDIOMA ---");
            System.out.print("Escolha o idioma (ex: en para inglês, es para espanhol, pt para português): ");
            idioma = leitura.nextLine().trim().toLowerCase();
            if (idioma.isEmpty()){
                System.out.println("O idioma não pode ser vazio. Por favor, digite um código de idioma válido.");
            } else if (idioma.length() > 2) {
                System.out.println("Código de idioma inválido. Por favor, use um código de 2 letras (ex: en, es, pt).");

            } else {
                idiomaValido = true;
            }
        }
        List<Livro> livroIdioma = livroRepository.findByIdioma(idioma);
        if(livroIdioma.isEmpty()){
            System.out.println("\nNão há livros registrados para o idioma "+ idioma);
        }else {
            System.out.println("\nLIVROS REGISTRADOS NO IDIOMA "+ idioma);
            livroIdioma.forEach(System.out::println);
            System.out.println("----------------------------------------------------\n");
        }
    }

    private void exibirEstatisticasDeDownloads() {
        List<Livro> livros = livroRepository.findAll();
        if (livros.isEmpty()){
            System.out.println("\nNão há livros registrados para exibir estatísticas.");
            return;
        }

        DoubleSummaryStatistics estatisticas = livros.stream()
                .filter(l -> l.getNumeroDownloads() != null)
                .mapToDouble(Livro::getNumeroDownloads)
                .summaryStatistics();

        System.out.println("\n--- ESTATÍSTICAS DE DOWNLOADS ---");
        System.out.println("Total de livros com downloads registrados: " + estatisticas.getCount());
        if (estatisticas.getCount() > 0) { // Evitar divisão por zero se não houver downloads
            System.out.println("Total de downloads: " + estatisticas.getSum());
            System.out.println("Média de downloads por livro: " + String.format("%.2f", estatisticas.getAverage()));
            System.out.println("Maior número de downloads: " + estatisticas.getMax());
            System.out.println("Menor número de downloads: " + estatisticas.getMin());
        } else {
            System.out.println("Não há dados de downloads válidos para calcular estatísticas.");
        }
        System.out.println("-----------------------------------\n");
    }

    private void top10LivrosMaisBaixados() {
        List<Livro> top10Livros = livroRepository.findTop10ByOrderByNumeroDownloadsDesc();
        System.out.println("\n----------------- TOP 10 MAIS BAIXADOS");
        top10Livros.forEach(l ->
                System.out.println(l.getTitulo() + "Número de downloads: " + l.getNumeroDownloads() + "\n"));
                System.out.println("-----------------------------------------------------------------\n");
    }

    private void buscarAutorPorNome() {
        System.out.print("Digite um trecho do nome do autor(a) para busca: ");
        var nomeAutor = leitura.nextLine();

        List<Autor> procuraAutor = autorRepository.findByNomeContainingIgnoreCase(nomeAutor);
        if (!procuraAutor.isEmpty()){
            System.out.println("\n--- AUTORES ENCONTRADOS ---");
            procuraAutor.forEach(autor -> {
                System.out.println(autor);
            });
        }else {
            System.out.println("Autor(a) não encontrado(a) com o nome: " + nomeAutor);
        }

        System.out.println("------------------------------------------------------\n\n");
    }

    private void buscarAutorPelaDataFalecimento() {
        Integer anoFalecimento = null;
        boolean entradaValida = false;

        while (!entradaValida) {
            System.out.print("Digite o ano desejado para listar autores que já faleceram:  ");
            try {
                anoFalecimento = leitura.nextInt();
                leitura.nextLine();
                if (anoFalecimento < 0 || anoFalecimento > 2025){
                    System.out.println("Ano inválido. Por favor, digite um ano entre 0 e 2025");
                } else {
                    entradaValida = true;
                }
            }catch (InputMismatchException e){
                System.out.println("Entrada inválida! Por favor, digite um ano válido (somente números inteiros");
                leitura.nextLine();
            }

        }

        List<Autor> buscaFalecidos = autorRepository.autoresFalecidosDeterminadoAno(anoFalecimento);
        if (buscaFalecidos.isEmpty()){
            System.out.println("\nNão há autores registrados que tenham morrido até o ano "+ anoFalecimento);
        }
        System.out.println("\n--- AUTORES MORTOS ATÉ "+ anoFalecimento + " ---");
        buscaFalecidos.forEach(System.out::println);
        System.out.println("---------------------------------------------------\n");
    }



}
