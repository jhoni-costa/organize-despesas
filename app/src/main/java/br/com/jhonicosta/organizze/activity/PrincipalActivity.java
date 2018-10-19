package br.com.jhonicosta.organizze.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.jhonicosta.organizze.R;
import br.com.jhonicosta.organizze.adapter.AdapterMovimentacao;
import br.com.jhonicosta.organizze.config.ConfigFirebase;
import br.com.jhonicosta.organizze.helper.Base64Custom;
import br.com.jhonicosta.organizze.model.Movimentacao;
import br.com.jhonicosta.organizze.model.Usuario;

public class PrincipalActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private TextView txtSaldacao, txtSaldo;
    private RecyclerView recyclerView;

    private Double despesaTotal = 0.00;
    private Double receitaTotal = 0.00;
    private Double resumo = 0.00;

    private FirebaseAuth auth = ConfigFirebase.getFirebaseAuth();
    private DatabaseReference reference = ConfigFirebase.getFirebaseDatabase();
    private DatabaseReference usuarioRef;
    private ValueEventListener vELUsuario;
    private AdapterMovimentacao adapterMov;
    private List<Movimentacao> movimentacoes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        calendarView = findViewById(R.id.calendarView);
        txtSaldacao = findViewById(R.id.txtSaudacao);
        txtSaldo = findViewById(R.id.txtSaldoPrincipal);
        recyclerView = findViewById(R.id.rvMovimentos);

        configCalendarView();

        adapterMov = new AdapterMovimentacao(movimentacoes, getApplicationContext());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterMov);

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarResumo();
    }

    public void recuperarResumo() {
        String emailUser = auth.getCurrentUser().getEmail();
        usuarioRef = reference.child("usuarios")
                .child(Base64Custom.encode64(emailUser));

        vELUsuario = usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                despesaTotal = usuario.getDespesaTotal();
                receitaTotal = usuario.getReceitaTotal();
                resumo = receitaTotal - despesaTotal;

                DecimalFormat dc = new DecimalFormat("0.##");

                txtSaldacao.setText("Olá, " + usuario.getNome());
                txtSaldo.setText("R$: " + dc.format(resumo));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuSair:
                auth.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addReceita(View view) {
        startActivity(new Intent(this, ReceitasActivity.class));
    }

    public void addDespesa(View view) {
        startActivity(new Intent(this, DespesasActivity.class));
    }

    private void configCalendarView() {

        CharSequence meses[] = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        calendarView.setTitleMonths(meses);
      /*  CharSequence dias[] = {"Domingo", "Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sabado"};
        calendarView.setWeekDayLabels(dias);*/
        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                Log.i("calendario:", "valor:" + (date.getMonth() + 1) + "/" + date.getYear());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuarioRef.removeEventListener(vELUsuario);///////
    }
}
