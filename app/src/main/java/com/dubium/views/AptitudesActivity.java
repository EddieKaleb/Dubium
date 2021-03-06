package com.dubium.views;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import com.dubium.R;
import com.dubium.adapters.SetupAdapter;
import com.dubium.database.FirebaseDatabaseManager;
import com.dubium.fragments.ProfileFragment;
import com.dubium.model.Subject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import fisk.chipcloud.ChipCloud;

public class AptitudesActivity extends Activity {

    @BindView(R.id.view_mensagem) public LinearLayout mViewMensagem;
    @BindView(R.id.sv_disciplinas) SearchView mSvDisciplinas;
    @BindView(R.id.lv_disciplinas) ListView mLvDisciplinas;
    @BindView(R.id.tv_prosseguir) public TextView mTvProsseguir;
    @BindView(R.id.tv_first) TextView mTvFirst;
    @BindView(R.id.tv_second) TextView mTvSecond;
    @BindView(R.id.iv_icon) ImageView mIvIcon;

    public SetupAdapter mAdapterSetup;
    public ArrayAdapter mAdapterSearch;

    public FirebaseAuth mFirebaseAuth;
    public FirebaseDatabaseManager mFirebaseDatabaseManager;
    private DatabaseReference mDatabase;


    public ArrayList<Subject> mDisciplinas = new ArrayList<>();
    // Lista para aptidões e dificuldades
    public ArrayList<Subject> mMinhasDisciplinas = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        ButterKnife.bind(this);

        mTvProsseguir.setVisibility(View.GONE);
        mLvDisciplinas.setVisibility(View.GONE);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabaseManager = new FirebaseDatabaseManager();
        mFirebaseAuth = FirebaseAuth.getInstance();

        // Query do firebase para adicionar algumas disciplinas
        mDisciplinas = mFirebaseDatabaseManager.getSubjects(this);

        mAdapterSetup = new SetupAdapter(this, mMinhasDisciplinas);
        mAdapterSearch = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mDisciplinas);

        if (getIntent().getExtras().getString("calling-activity") != null){
            String request = getIntent().getExtras().getString("calling-activity");
            if (request.equals("AptitudesActivity")) {
                setUserSubjects(mFirebaseAuth.getCurrentUser().getUid(), "aptitudes", mAdapterSetup);
                mViewMensagem.setVisibility(View.GONE);
                mLvDisciplinas.setVisibility(View.VISIBLE);
                mLvDisciplinas.setAdapter(mAdapterSetup);
                mTvProsseguir.setText("SALVAR");
                mTvFirst.setText("Você está sem habilidades");
                mTvSecond.setText("Adicione pelo menos uma!");
                mTvProsseguir.setVisibility(View.VISIBLE);
            }
        } else {
            mLvDisciplinas.setAdapter(mAdapterSearch);

        }

        mLvDisciplinas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapterSetup.add(mDisciplinas.get(position));  // Nova aptidão ou dificuldade
                Log.i("OnItemClick", mMinhasDisciplinas.size() + "");
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

                if (getIntent().getExtras().getString("calling-activity") != null){
                    HashMap<String, Boolean> subjects = new HashMap<>();

                    for (Subject s: mMinhasDisciplinas) subjects.put(s.getId(), true);
                    mFirebaseDatabaseManager.addAptitudesToUser(mFirebaseAuth.getCurrentUser().getUid(), subjects);

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("AptitudesActivity", "New Data");
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();

                } else {
                    Intent intent = new Intent(v.getContext(), DifficultiesActivity.class);

                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("Aptitudes", (ArrayList<Subject>) mMinhasDisciplinas);
                    intent.putExtras(mBundle);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {}

    public void setUserSubjects(String uId, String subjectsType, final ArrayAdapter minhasDisciplinas) {

        Query query = mDatabase.child("users").child(uId).child(subjectsType);

        /***** Pega a lista de ids de subjects de um user e a insere em um Map *****/
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {

                    String key = objDataSnapshot.getKey();

                    Query query = mDatabase.child("subjects").child(key);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Subject subject = dataSnapshot.getValue(Subject.class);
                            minhasDisciplinas.add(subject);
                            minhasDisciplinas.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}