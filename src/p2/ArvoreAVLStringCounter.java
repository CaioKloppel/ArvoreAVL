package p2;

import p1.Ordem;

import java.util.HashMap;
import java.util.Map;

public class ArvoreAVLStringCounter {
    private static class No{
        String palavra;
        int valor;
        Map<String, Integer> files;
        int altura;
        No esquerda;
        No direita;

        No(String palavra, String file){
            this.palavra = palavra;
            this.files = new HashMap<>(Map.of(file, 1));
            this.valor = 1;
            this.altura = 1;
        }
    }

    private No raiz;

    public ArvoreAVLStringCounter(){};
    public ArvoreAVLStringCounter(String palavra, String file){
        insereElemento(palavra, file);
    };
    public ArvoreAVLStringCounter(Map<String, String> palavra){
        insereElemento(palavra);
    }

    public void insereElemento(String palavra, String file){
        if(raiz == null) {raiz = new No(palavra, file); return;}
        raiz = insereElemento(palavra, file, raiz);
    }

    public void insereElemento(Map<String, String> palavra){
        palavra.forEach(((chave, valor) -> insereElemento(valor, chave)));
    }

    private No insereElemento(String palavra, String file, No no){
        int comparacao = palavra.compareTo(no.palavra);
        if(comparacao < 0){
            if(no.esquerda == null){
                no.esquerda = new No(palavra, file);
            } else {
                no.esquerda = insereElemento(palavra, file, no.esquerda);
            }
        } else if (comparacao > 0){
            if(no.direita == null){
                no.direita = new No(palavra, file);
            } else {
                no.direita = insereElemento(palavra, file, no.direita);
            }
        } else {
            no.valor++;
            no.files.merge(file, 1, Integer::sum);
        }
        return balancear(no);
    }

    public void remove(String elemento){
        if(raiz == null) return;
        remove(elemento, raiz);
    }

    private int atualizarAltura(No no){
        return 1 + Math.max(
                no.esquerda != null ? no.esquerda.altura : 0
                ,
                no.direita != null ? no.direita.altura : 0
        );
    }

    private void remove(String elemento, No no){
        int comparacao = elemento.compareTo(no.palavra);
        if(comparacao == 0){
            no = removerNo(no);
        }
        else if(comparacao < 0){
            if(no.esquerda == null) return;
            else if(no.esquerda.palavra.compareTo(elemento) == 0) {
                no.esquerda = removerNo(no.esquerda);
            }
            else remove(elemento, no.esquerda);
        } else {
            if(no.direita == null) return;
            else if(no.direita.palavra.compareTo(elemento) == 0) {
                no.direita = removerNo(no.direita);
            }
            else remove(elemento, no.direita);
        }
        balancear(no);
    }

    private No balancear(No no) {
        no.altura = atualizarAltura(no);
        int fb = calcularFb(no);
        if(!(fb > 1 || fb < -1)) return no;

        No pesado = no.esquerda == null ? no.direita
                : no.direita == null ? no.esquerda
                : no.esquerda.altura > no.direita.altura ? no.esquerda
                : no.direita;

        int fbFilho = calcularFb(pesado);

        return rotacionar(no, pesado, fb, fbFilho);
    }

    private int calcularFb(No aux){
        int alturaEsquerda, alturaDireita;
        alturaEsquerda = aux.esquerda != null ? aux.esquerda.altura : 0;
        alturaDireita = aux.direita != null ? aux.direita.altura : 0;
        return alturaEsquerda - alturaDireita;
    }

    private No rotacionar(No no, No filho, int fbRaiz, int fbFilho){
        if (fbRaiz == 2){
            if(fbFilho == -1) no.esquerda = rotacaoEsquerda(filho);
            no = rotacaoDireita(no);
        } else if(fbRaiz == -2){
            if(fbFilho == 1) no.direita = rotacaoDireita(filho);
            no = rotacaoEsquerda(no);
        }return no;
    }

    private No rotacaoDireita(No aux){
        No temp = aux.esquerda;
        aux.esquerda = temp.direita;
        temp.direita = aux;
        aux.altura = atualizarAltura(aux);
        temp.altura = atualizarAltura(temp);
        return temp;
    }

    private No rotacaoEsquerda(No aux){
        No temp = aux.direita;
        aux.direita = temp.esquerda;
        temp.esquerda = aux;
        aux.altura = atualizarAltura(aux);
        temp.altura = atualizarAltura(temp);
        return temp;
    }

    private No removerNo(No no){
        if (no.esquerda == null) return no.direita;
        if (no.direita == null) return no.esquerda;

        No aux = no.direita;
        while (aux.esquerda != null) aux = aux.esquerda;

        no.palavra = aux.palavra;
        remove(aux.palavra, no.direita);
        return no;
    }

    public void percorrer(Ordem tipo){
        System.out.println("===SEQUÊNCIA " + tipo + "===");
        auxPercorre(raiz, tipo);
        System.out.println("===FIM DA ÁRVORE===");
    }

    private void auxPercorre(No atual, Ordem tipo){
        switch (tipo){
            case PREORDEM -> {
                System.out.println(atual.valor);
                if(atual.esquerda != null) auxPercorre(atual.esquerda, tipo);
                if(atual.direita != null) auxPercorre(atual.direita, tipo);
            }
            case INORDEM -> {
                if(atual.esquerda != null) auxPercorre(atual.esquerda, tipo);
                System.out.println(atual.valor);
                if(atual.direita != null) auxPercorre(atual.direita, tipo);
            }
            case POSORDEM -> {
                if(atual.direita != null) auxPercorre(atual.direita, tipo);
                System.out.println(atual.valor);
                if(atual.esquerda != null) auxPercorre(atual.esquerda, tipo);
            }
        }
    }

    public boolean encontrarPalavra(String palavra){
        No aux = raiz;
        while (aux != null){
            int comparacao = palavra.compareTo(aux.palavra);
            if(comparacao == 0) return true;
            else if(comparacao < 0) aux = aux.esquerda;
            else aux = aux.direita;
        } return false;
    }

    public void infoPalavra(String palavra){
        No aux = raiz;
        while (aux != null){
            int comparacao = palavra.compareTo(aux.palavra);
            if(aux.palavra.compareTo(palavra) == 0) {
                System.out.println("Total de ocorrencias de " + aux.palavra + ": " + aux.valor);
                aux.files.forEach((file, ocorrencia) ->
                    System.out.println("Arquivo " + file + ": " + ocorrencia)
                );
                return;
            }
            else if(comparacao < 0) aux = aux.esquerda;
            else aux = aux.direita;
        }
        System.out.println("Palavra não encontrada");;
    }

    public Integer retornarMaior(){
        if(raiz == null) return null;
        No aux = raiz;
        while (aux.direita != null){
            aux = aux.direita;
        } return aux.valor;
    }

    public Integer retornarMenor(){
        if(raiz == null) return null;
        No aux = raiz;
        while (aux.esquerda != null){
            aux = aux.esquerda;
        } return aux.valor;
    }

    public boolean vazia(){
        return raiz == null;
    }


    public void imprimirArvore() {
        if (raiz == null) {
            System.out.println("(árvore vazia)");
            return;
        }
        System.out.println(descrever(raiz));
        imprimirFilhos(raiz, "");
    }

    private void imprimirFilhos(No no, String prefixo) {
        if (no.esquerda == null && no.direita == null) return; // folha: nada abaixo
        imprimirRamo(no.esquerda, prefixo, "E", false);
        imprimirRamo(no.direita, prefixo, "D", true);
    }

    private void imprimirRamo(No no, String prefixo, String lado, boolean ultimo) {
        String conector = ultimo ? "└── " : "├── ";
        System.out.println(prefixo + conector + lado + ": " + (no == null ? "·" : descrever(no)));

        if (no != null) {
            // a linha vertical só continua se ainda houver irmão abaixo
            String novoPrefixo = prefixo + (ultimo ? "    " : "│   ");
            imprimirFilhos(no, novoPrefixo);
        }
    }

    private String descrever(No no) {
        return no.valor + " (h=" + no.altura + ", fb=" + calcularFb(no) + ")";
    }
}
