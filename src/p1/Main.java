package p1;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        int[] lista = {10,7,12,3,45,4,3,8,7,6,8,9,30};
        ArvoreAVL arvore = new ArvoreAVL(lista);
        arvore.percorrer(Ordem.PREORDEM);
        arvore.percorrer(Ordem.INORDEM);
        arvore.percorrer(Ordem.POSORDEM);
        arvore.remove(10);
        arvore.percorrer(Ordem.PREORDEM);
        arvore.percorrer(Ordem.INORDEM);
        arvore.percorrer(Ordem.POSORDEM);
        System.out.println(arvore.encontrarElemento(10));
        System.out.println(arvore.retornarMaior());
        System.out.println(arvore.retornarMenor());
        System.out.println(arvore.tamanho());
    }
}