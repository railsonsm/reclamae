package reclamae.com.br.reclamae.model;

/**
 * Created by guser on 26/05/2018.
 */

public class Urgencia {
    private String Nome;
    private String Numero;


    public Urgencia(String nome, String numero){
        this.Nome = nome;
        this.Numero = numero;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public String getNumero() {
        return Numero;
    }

    public void setNumero(String numero) {
        Numero = numero;
    }

    @Override
    public String toString() {
        return Nome + " " + Numero;
    }
}
