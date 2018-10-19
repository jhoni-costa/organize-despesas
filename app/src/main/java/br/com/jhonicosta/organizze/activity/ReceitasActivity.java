package br.com.jhonicosta.organizze.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
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

public class ReceitasActivity extends AppCompatActivity {

    private TextInputEditText campoCategoria, campoData, campoDescricao;
    private EditText campoValor;

    private Movimentacao movimentacao;

    private DatabaseReference reference = ConfigFirebase.getFirebaseDatabase();
    private FirebaseAuth auth = ConfigFirebase.getFirebaseAuth();

    private Double totalReceita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        campoCategoria = findViewById(R.id.receitaCategoria);
        campoData = findViewById(R.id.receitaData);
        campoDescricao = findViewById(R.id.receitaDescricao);
        campoValor = findViewById(R.id.receitaValor);

        campoData.setText(DateCustom.dataAtual());
        receitaTotal();
    }

    public void salvarReceita(View view) {
        if (validarCampos()) {
            Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());

            movimentacao = new Movimentacao();
            movimentacao.setValor(valorRecuperado);
            movimentacao.setCategoria(campoCategoria.getText().toString());
            movimentacao.setDescricao(campoDescricao.getText().toString());
            movimentacao.setData(campoData.getText().toString());
            movimentacao.setTipo("r");

            Double receitaAtualizada = totalReceita + valorRecuperado;
            atualizaReceita(receitaAtualizada);

            movimentacao.salvar(campoData.getText().toString());
            finish();
        }
    }

    private void atualizaReceita(Double receita) {
        String id = Base64Custom.encode64(auth.getCurrentUser().getEmail());
        DatabaseReference userReference = reference.child("usuarios").child(id);
        userReference.child("receitaTotal").setValue(receita);
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
                        Toast.makeText(ReceitasActivity.this, "Descrição não informada!", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else {
                    Toast.makeText(ReceitasActivity.this, "Categoria não informada!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                Toast.makeText(ReceitasActivity.this, "Data em branco!", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(ReceitasActivity.this, "Valor não informado!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void receitaTotal() {
        DatabaseReference userRef = reference.child("usuarios")
                .child(Base64Custom.encode64(auth.getCurrentUser().getEmail()));

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                totalReceita = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
