package p2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<String> arquivosLidos = new ArrayList<>();

    public static void main(String[] args) {
        Path path = obterCaminho(args);
        if (path == null) {
            System.out.println("Nenhum caminho informado. Encerrando.");
            return;
        }

        ArvoreAVLStringCounter arvore = new ArvoreAVLStringCounter();
        carregar(arvore, path);

        if (arvore.vazia()) {
            System.out.println("Árvore vazia, erro ao preencher dados.");
            return;
        }

        ArvoreAVLOrderByValue arvoreValor = arvore.retornarOrdenadaPorValor();
        System.out.println();
        System.out.println("Indexação concluída: " + arvore.tamanho() + " palavras distintas em "
                + arquivosLidos.size() + " arquivo(s).");

        menu(arvore, arvoreValor);
    }

    // ------------------------------------------------------------------ menu

    private static void menu(ArvoreAVLStringCounter arvore, ArvoreAVLOrderByValue arvoreValor) {
        boolean encerrar = false;
        while (!encerrar) {
            imprimirOpcoes();
            String opcao = lerLinha("Opção: ");
            System.out.println();
            switch (opcao) {
                case "1" -> buscarPalavra(arvore);
                case "2" -> ranking(arvoreValor, true);
                case "3" -> ranking(arvoreValor, false);
                case "4" -> extremos(arvoreValor);
                case "5" -> buscarPorFrequencia(arvoreValor);
                case "6" -> estatisticas(arvore);
                case "7" -> listarArquivos();
                case "0", "" -> encerrar = true;
                default -> System.out.println("Opção inválida: " + opcao);
            }
            System.out.println();
        }
        System.out.println("Encerrando. Até mais!");
    }

    private static void imprimirOpcoes() {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║        ÍNDICE DE PALAVRAS - ÁRVORE AVL       ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.println("║  1 - Buscar palavra                          ║");
        System.out.println("║  2 - Top N mais frequentes                   ║");
        System.out.println("║  3 - Top N menos frequentes                  ║");
        System.out.println("║  4 - Palavra mais / menos frequente          ║");
        System.out.println("║  5 - Palavras com N ocorrências exatas       ║");
        System.out.println("║  6 - Estatísticas                            ║");
        System.out.println("║  7 - Arquivos indexados                      ║");
        System.out.println("║  0 - Sair                                    ║");
        System.out.println("╚══════════════════════════════════════════════╝");
    }

    // --------------------------------------------------------------- opções

    private static void buscarPalavra(ArvoreAVLStringCounter arvore) {
        String palavra = lerLinha("Palavra: ").toLowerCase();
        if (palavra.isEmpty()) {
            System.out.println("Busca cancelada.");
            return;
        }
        arvore.infoPalavra(palavra);
    }

    private static void ranking(ArvoreAVLOrderByValue arvoreValor, boolean decrescente) {
        int limite = lerInteiro("Quantas palavras? (enter = 10): ", 10);
        if (limite <= 0) {
            System.out.println("Quantidade inválida.");
            return;
        }
        List<ArvoreAVLStringCounter.No> lista = decrescente
                ? arvoreValor.listarDecrescente(limite)
                : arvoreValor.listarCrescente(limite);
        imprimirLista(decrescente ? "MAIS FREQUENTES" : "MENOS FREQUENTES", lista);
    }

    private static void extremos(ArvoreAVLOrderByValue arvoreValor) {
        ArvoreAVLStringCounter.No maior = arvoreValor.retornarMaior();
        ArvoreAVLStringCounter.No menor = arvoreValor.retornarMenor();
        if (maior == null || menor == null) {
            System.out.println("Árvore vazia.");
            return;
        }
        System.out.println("Mais frequente: " + maior.palavra + " (" + maior.valor + " ocorrência(s))");
        System.out.println("Menos frequente: " + menor.palavra + " (" + menor.valor + " ocorrência(s))");
    }

    private static void buscarPorFrequencia(ArvoreAVLOrderByValue arvoreValor) {
        int valor = lerInteiro("Número de ocorrências: ", -1);
        if (valor <= 0) {
            System.out.println("Informe um número maior que zero.");
            return;
        }
        List<ArvoreAVLStringCounter.No> lista = arvoreValor.buscarPorValor(valor);
        if (lista.isEmpty()) {
            System.out.println("Nenhuma palavra com exatamente " + valor + " ocorrência(s).");
            return;
        }
        imprimirLista("PALAVRAS COM " + valor + " OCORRÊNCIA(S)", lista);
    }

    private static void estatisticas(ArvoreAVLStringCounter arvore) {
        int distintas = arvore.tamanho();
        int total = arvore.totalOcorrencias();
        System.out.println("=== ESTATÍSTICAS ===");
        System.out.println("Arquivos indexados ....... " + arquivosLidos.size());
        System.out.println("Palavras distintas ....... " + distintas);
        System.out.println("Total de ocorrências ..... " + total);
        System.out.println("Média por palavra ........ " + media(total, distintas));
        System.out.println("Altura da árvore ......... " + arvore.altura());
    }

    private static void listarArquivos() {
        System.out.println("=== ARQUIVOS INDEXADOS (" + arquivosLidos.size() + ") ===");
        arquivosLidos.forEach(arquivo -> System.out.println(" - " + arquivo));
    }

    private static void imprimirLista(String titulo, List<ArvoreAVLStringCounter.No> lista) {
        System.out.println("=== " + titulo + " ===");
        int posicao = 1;
        for (ArvoreAVLStringCounter.No no : lista) {
            System.out.println(direita(posicao++ + ".", 4) + " " + esquerda(no.palavra, 25)
                    + " " + direita(String.valueOf(no.valor), 6) + " ocorrência(s) em "
                    + no.files.size() + " arquivo(s)");
        }
    }

    // ------------------------------------------------------- formatação

    private static String media(int total, int divisor) {
        if (divisor == 0) return "0,00";
        long centesimos = Math.round(100.0 * total / divisor);
        String decimais = String.valueOf(centesimos % 100);
        if (decimais.length() == 1) decimais = "0" + decimais;
        return (centesimos / 100) + "," + decimais;
    }

    private static String direita(String texto, int largura) {
        return " ".repeat(Math.max(0, largura - texto.length())) + texto;
    }

    private static String esquerda(String texto, int largura) {
        return texto + " ".repeat(Math.max(0, largura - texto.length()));
    }

    // ----------------------------------------------------------- entrada

    private static Path obterCaminho(String[] args) {
        if (args.length >= 1) {
            Path path = Path.of(args[0]);
            if (Files.exists(path)) return path;
            System.out.println("Caminho inexistente: " + args[0]);
        }
        while (true) {
            String entrada = lerLinha("Arquivo .txt ou pasta (enter para sair): ");
            if (entrada.isEmpty()) return null;
            Path path = Path.of(entrada);
            if (Files.exists(path)) return path;
            System.out.println("Caminho inexistente: " + entrada);
        }
    }

    private static void carregar(ArvoreAVLStringCounter arvore, Path path) {
        if (Files.isDirectory(path)) {
            try (Stream<Path> arquivos = Files.walk(path)) {
                arquivos.filter(Files::isRegularFile)
                        .forEach(arquivo -> preencherArvore(arvore, arquivo));
            } catch (IOException e) {
                System.out.println("Erro ao ler pasta de arquivos: " + path);
            }
        } else {
            preencherArvore(arvore, path);
        }
    }

    private static void preencherArvore(ArvoreAVLStringCounter arvore, Path path) {
        try {
            String conteudo = Files.readString(path);
            String[] palavras = conteudo.trim().toLowerCase().split("[^\\p{L}]+");
            boolean inseriu = false;
            for (String p : palavras) {
                if (p.length() > 1) {
                    arvore.insereElemento(p, path.toString());
                    inseriu = true;
                }
            }
            if (inseriu) arquivosLidos.add(path.toString());
        } catch (IOException e) {
            System.out.println("Erro ao ler arquivo: " + path);
        }
    }

    private static String lerLinha(String rotulo) {
        System.out.print(rotulo);
        if (!scanner.hasNextLine()) return "";
        return scanner.nextLine().trim();
    }

    private static int lerInteiro(String rotulo, int padrao) {
        String entrada = lerLinha(rotulo);
        if (entrada.isEmpty()) return padrao;
        try {
            return Integer.parseInt(entrada);
        } catch (NumberFormatException e) {
            System.out.println("Número inválido: " + entrada);
            return 0;
        }
    }
}