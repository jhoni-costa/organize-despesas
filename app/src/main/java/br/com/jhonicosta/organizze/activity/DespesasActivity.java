package br.com.jhonicosta.organizze.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import br.com.jhonicosta.organizze.R;
import br.com.jhonicosta.organizze.config.ConfigFirebase;
import br.com.jhonicosta.organizze.helper.Base64Custom;
import br.com.jhonicosta.organizze.helper.DateCustom;
import br.com.jhonicosta.organizze.model.Movimentacao;
import br.com.jhonicosta.organizze.model.Usuario;

public class DespesasActivity extends AppCompatActivity {

    private TextInputEditText campoCategoria, campoData, campoDescricao;
    private EditText campoValor;
    private FloatingActionButton fabSalvar;

    private Movimentacao movimentacao;

    private DatabaseReference reference = ConfigFirebase.getFirebaseDatabase();
    private FirebaseAuth auth = ConfigFirebase.getFirebaseAuth();

    private Double totalDespesa;

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
        despesaTotal();
    }

    public void salvarDespesa(View view) {
        if (validarCampos()) {
            Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());

            movimentacao = new Movimentacao();
            movimentacao.setValor(valorRecuperado);
            movimentacao.setCategoria(campoCategoria.getText().toString());
            movimentacao.setDescricao(campoDescricao.getText().toString());
            movimentacao.setData(campoData.getText().toString());
            movimentacao.setTipo("D");

            Double despesaAtualizada = totalDespesa + valorRecuperado;
            atualizarDespesa(despesaAtualizada);

            movimentacao.salvar(campoData.getText().toString());
            finish();
        }
    }

    private Boolean validarCampos() {

        String txtValor = campoValor.getText().toString();
        String txtData = campoData.getText().toString();
        String txtCategoria = campoCategoria.getText().toString();
        String txtDescricao = campoDescricao.getText().toString();

        if (!txtValor.isEmpty()) {
            if (!txtData.isEmpty()) {
                if (!txtCategoria.isEmpty()) {
                    if (!txtDescricao.isEmpty()) {
                        return true;
                    } else {
                        Toast.makeText(DespesasActivity.this, "Descrição não informada!", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else {
                    Toast.makeText(DespesasActivity.this, "Categoria não informada!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                Toast.makeText(DespesasActivity.this, "Data em branco!", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(DespesasActivity.this, "Valor não informado!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void despesaTotal() {
        String emailUser = auth.getCurrentUser().getEmail();
        DatabaseReference usuarioRef = reference.child("usuarios")
                .child(Base64Custom.encode64(emailUser));

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                totalDespesa = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void atualizarDespesa(Double despesa) {
        String emailUser = auth.getCurrentUser().getEmail();
        String id = Base64Custom.encode64(emailUser);
        DatabaseReference usuarioRef = reference.child("usuarios")
                .child(id);

        usuarioRef.child("despesaTotal").setValue(despesa);
    }
}
