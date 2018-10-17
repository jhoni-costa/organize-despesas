package br.com.jhonicosta.organizze.activity;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import br.com.jhonicosta.organizze.R;
import br.com.jhonicosta.organizze.helper.DateCustom;

public class DespesasActivity extends AppCompatActivity {

    private TextInputEditText campoCategoria, campoData, campoDescricao;
    private EditText campoValor;
    private FloatingActionButton fabSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        campoCategoria = findViewById(R.id.despesaCategoria);
        campoData = findViewById(R.id.despesaData);
        campoDescricao = findViewById(R.id.despesaDescricao);
        campoValor = findViewById(R.id.despesaValor);
        fabSalvar = findViewById(R.id.fabSalvarDespesa);

        campoData.setText(DateCustom.dataAtual());
    }
}
