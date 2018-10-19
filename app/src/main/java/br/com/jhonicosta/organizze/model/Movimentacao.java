package br.com.jhonicosta.organizze.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import br.com.jhonicosta.organizze.config.ConfigFirebase;
import br.com.jhonicosta.organizze.helper.Base64Custom;
import br.com.jhonicosta.organizze.helper.DateCustom;

public class Movimentacao {

    private String data, categoria, descricao, tipo, chave;
    private double valor;

    public Movimentacao() {
    }

    public Movimentacao(String data, String categoria, String descricao, String tipo, double valor) {
        this.data = data;
        this.categoria = categoria;
        this.descricao = descricao;
        this.tipo = tipo;
        this.valor = valor;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public void salvar(String data) {
        FirebaseAuth auth = ConfigFirebase.getFirebaseAuth();
        String id = Base64Custom.encode64(auth.getCurrentUser().getEmail());

        DatabaseReference reference = ConfigFirebase.getFirebaseDatabase();
        reference.child("movimentacao")
                .child(id)
                .child(DateCustom.dataFirebase(this.data))
                .push()
                .setValue(this);
    }
}
