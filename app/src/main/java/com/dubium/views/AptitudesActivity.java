package com.dubium.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;

import com.dubium.R;
import com.dubium.adapters.SetupAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AptitudesActivity extends Activity {

    @BindView(R.id.view_mensagem) public LinearLayout mViewMensagem;
    @BindView(R.id.sv_disciplinas) SearchView mSvDisciplinas;
    @BindView(R.id.lv_disciplinas) ListView mLvDisciplinas;
    @BindView(R.id.tv_prosseguir) public TextView mTvProsseguir;
    @BindView(R.id.tv_first) TextView mTvFirst;
    @BindView(R.id.tv_second) TextView mTvSecond;
    @BindView(R.id.iv_icon) ImageView mIvIcon;
    public final ArrayList<String> mDisciplinas = new ArrayList<>();
    // Lista para aptidões e dificuldades
    public final ArrayList<String> mMinhasDisciplinas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        ButterKnife.bind(this);

        mTvProsseguir.setVisibility(View.GONE);
        mLvDisciplinas.setVisibility(View.GONE);

        // Query do firebase para adicionar algumas disciplinas
        mDisciplinas.add("Matemática");
        mDisciplinas.add("Filosofia");
        mDisciplinas.add("Português");
        mDisciplinas.add("Inglês");

        final ArrayAdapter mAdapterSearch = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mDisciplinas);
        final SetupAdapter mAdapterSetup = new SetupAdapter(this, mMinhasDisciplinas);

        mLvDisciplinas.setAdapter(mAdapterSearch);

        mLvDisciplinas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapterSetup.add(mDisciplinas.get(position));  // Nova aptidão ou dificuldade
                mDisciplinas.remove(position); // Remove da listagem de busca
                mLvDisciplinas.setAdapter(mAdapterSetup); // Troca para listagem de aptidões ou dificuldades
                mSvDisciplinas.setIconified(true); // Fecha o SearchView
                mTvProsseguir.setVisibility(View.VISIBLE); // Botão para prosseguir
            }
        });

        mSvDisciplinas.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewMensagem.setVisibility(View.GONE); // Mensagens do setup
                mLvDisciplinas.setAdapter(mAdapterSearch); // Troca para listagem de busca
                mLvDisciplinas.setVisibility(View.VISIBLE);
            }
        });

        mSvDisciplinas.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Query no Firebase
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                // Query no firebase para buscar e adicionar disciplinas que contenha a string recebida
                mAdapterSearch.getFilter().filter(newText); // Filtragem
                return false;
            }
        });

        mSvDisciplinas.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // Verifica se o usuário possui disciplinas selecionadas ao fechar o SearchView
                if (mMinhasDisciplinas.size() > 0) {
                    mLvDisciplinas.setAdapter(mAdapterSetup);
                } else {
                    // Volta para o estado inicial
                    mLvDisciplinas.setVisibility(View.GONE);
                    mViewMensagem.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        mTvProsseguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DifficultiesActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {}
}