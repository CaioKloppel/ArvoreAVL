package p2;

import java.util.ArrayList;
import java.util.List;


public class ArvoreAVLOrderByValue {
    private static class No {
        ArvoreAVLStringCounter.No dado;
        int altura;
        No esquerda;
        No direita;

        No(ArvoreAVLStringCounter.No dado) {
            this.dado = dado;
            this.altura = 1;
        }
    }

    private No raiz;

    // ---------------------------------------------------------------- inserção

    public void insereElemento(ArvoreAVLStringCounter.No no) {
        if (no == null) return;
        raiz = insereElemento(no, raiz);
    }

    private No insereElemento(ArvoreAVLStringCounter.No no, No atual) {
        if (atual == null) return new No(no);
        if (no.valor <= atual.dado.valor) {
            atual.esquerda = insereElemento(no, atual.esquerda);
        } else {
            atual.direita = insereElemento(no, atual.direita);
        }
        return balancear(atual);
    }

    // ------------------------------------------------ balanceamento e rotações

    private No balancear(No no) {
        no.altura = atualizarAltura(no);
        int fb = calcularFb(no);
        if(!(fb > 1 || fb < -1)) return no;

        No pesado = fb > 0 ? no.esquerda
                : no.direita;

        int fbFilho = calcularFb(pesado);

        return rotacionar(no, pesado, fb, fbFilho);
    }

    private int altura(No no) {
        return no == null ? 0 : no.altura;
    }

    private int atualizarAltura(No no) {
        return 1 + Math.max(altura(no.esquerda), altura(no.direita));
    }

    private int calcularFb(No no) {
        return no == null ? 0 : altura(no.esquerda) - altura(no.direita);
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

    private No rotacaoDireita(No aux) {
        No temp = aux.esquerda;
        aux.esquerda = temp.direita;
        temp.direita = aux;
        aux.altura = atualizarAltura(aux);
        temp.altura = atualizarAltura(temp);
        return temp;
    }

    private No rotacaoEsquerda(No aux) {
        No temp = aux.direita;
        aux.direita = temp.esquerda;
        temp.esquerda = aux;
        aux.altura = atualizarAltura(aux);
        temp.altura = atualizarAltura(temp);
        return temp;
    }

    // ---------------------------------------------------------------- consulta

    public ArvoreAVLStringCounter.No retornarMaior() {
        if (raiz == null) return null;
        No aux = raiz;
        while (aux.direita != null) aux = aux.direita;
        return aux.dado;
    }

    public ArvoreAVLStringCounter.No retornarMenor() {
        if (raiz == null) return null;
        No aux = raiz;
        while (aux.esquerda != null) aux = aux.esquerda;
        return aux.dado;
    }

    public List<ArvoreAVLStringCounter.No> buscarPorValor(int valor) {
        List<ArvoreAVLStringCounter.No> lista = new ArrayList<>();
        buscarPorValor(raiz, valor, lista);
        return lista;
    }

    private void buscarPorValor(No no, int valor, List<ArvoreAVLStringCounter.No> lista) {
        if (no == null) return;
        if (valor < no.dado.valor) {
            buscarPorValor(no.esquerda, valor, lista);
        } else if (valor > no.dado.valor) {
            buscarPorValor(no.direita, valor, lista);
        } else {
            lista.add(no.dado);
            buscarPorValor(no.esquerda, valor, lista);
            buscarPorValor(no.direita, valor, lista);
        }
    }

    // ---------------------------------------------------------------- listagem

    public List<ArvoreAVLStringCounter.No> listarCrescente(int limite) {
        List<ArvoreAVLStringCounter.No> lista = new ArrayList<>();
        coletarCrescente(raiz, lista, limite);
        return lista;
    }

    public List<ArvoreAVLStringCounter.No> listarDecrescente(int limite) {
        List<ArvoreAVLStringCounter.No> lista = new ArrayList<>();
        coletarDecrescente(raiz, lista, limite);
        return lista;
    }

    private void coletarCrescente(No no, List<ArvoreAVLStringCounter.No> lista, int limite) {
        if (no == null || atingiuLimite(lista, limite)) return;
        coletarCrescente(no.esquerda, lista, limite);
        if (atingiuLimite(lista, limite)) return;
        lista.add(no.dado);
        coletarCrescente(no.direita, lista, limite);
    }

    private void coletarDecrescente(No no, List<ArvoreAVLStringCounter.No> lista, int limite) {
        if (no == null || atingiuLimite(lista, limite)) return;
        coletarDecrescente(no.direita, lista, limite);
        if (atingiuLimite(lista, limite)) return;
        lista.add(no.dado);
        coletarDecrescente(no.esquerda, lista, limite);
    }

    private boolean atingiuLimite(List<?> lista, int limite) {
        return limite > 0 && lista.size() >= limite;
    }

    // ---------------------------------------------------------------- métricas

    public boolean vazia() {
        return raiz == null;
    }

    public int tamanho() {
        return tamanho(raiz);
    }

    private int tamanho(No no) {
        if (no == null) return 0;
        return 1 + tamanho(no.esquerda) + tamanho(no.direita);
    }

    public int altura() {
        return altura(raiz);
    }
}
