package p2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        if (args.length != 1){
            System.out.println("Formato correto: java Main {arquivo.txt} ou {pastaComArquivos}");
            return;
        }
        ArvoreAVLStringCounter arvore = new ArvoreAVLStringCounter();
        Path path = Path.of(args[0]);
        if(Files.isDirectory(path)){
            try (Stream<Path> arquivos = Files.list(path)) {
                arquivos.filter(Files::isRegularFile)
                        .forEach(arquivo -> {
                            preencherArvore(arvore, arquivo);
                        });
            } catch (Exception e) {
                System.out.println("Erro ao ler pasta de arquivos");
            }
        } else if (Files.isRegularFile(path)) {
            preencherArvore(arvore, path);
        }
        if(arvore.vazia()){
            System.out.println("Arvore vazia, erro ao preencher dados.");
            return;
        }
        boolean encerrar = false;
        do{
            System.out.println("Procure por palavra ou aperte enter vazio para encerrar: ");
            String palavra = scanner.nextLine().trim().toLowerCase();
            if(palavra.isEmpty()) encerrar = true;
            else {
                arvore.infoPalavra(palavra);
            }
        } while (!encerrar);
    }

    private static void preencherArvore(ArvoreAVLStringCounter arvore, Path path) {
        try {
            String conteudo = Files.readString(path);
            String[] palavras = conteudo.trim().toLowerCase().split("[^\\p{L}]+");
            for(String p : palavras){
                if(!p.isEmpty()){
                    arvore.insereElemento(p, path.toString());
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler arquivo: " + path);
        }
    }
}
