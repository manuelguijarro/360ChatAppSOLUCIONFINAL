package com.example.a360chatapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.a360chatapp.R;
import com.example.a360chatapp.adapters.ListaChatRecyclerViewAdapter;
import com.example.a360chatapp.db.models.Chat;
import com.example.a360chatapp.firebase.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private ListaChatRecyclerViewAdapter listaChatRecyclerViewAdapter;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        cargarRecursosVista(view);
        return view;
    }

    private void cargarRecursosVista(View view) {
        //RecyclerView
        recyclerView = view.findViewById(R.id.chat_recycler_view);
        actualizarResultadoRecyclerView();
    }

    private void actualizarResultadoRecyclerView() {
        Query query = FirebaseUtil.obtenerListadoChatCollectionReference()
                .whereArrayContains("idUsuarios", FirebaseUtil.obtenerUsuarioUid())
                .orderBy("ultimoMensajeTimestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Chat> opt = new FirestoreRecyclerOptions
                .Builder<Chat>()
                .setQuery(query, Chat.class).build();
        listaChatRecyclerViewAdapter = new ListaChatRecyclerViewAdapter(opt, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(listaChatRecyclerViewAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (null != listaChatRecyclerViewAdapter) {
            listaChatRecyclerViewAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (null != listaChatRecyclerViewAdapter) {
            listaChatRecyclerViewAdapter.stopListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != listaChatRecyclerViewAdapter){
            listaChatRecyclerViewAdapter.notifyDataSetChanged();
        }
    }
}
