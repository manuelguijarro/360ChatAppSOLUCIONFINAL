package com.example.a360chatapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.a360chatapp.R;

public class AjusteFragment extends Fragment {

    private Button btnColorAzul;
    private Button btnColorRosa;
    private Button btnColorMarron;
    private Button btnColorVerde;
    private Button btnColorAmarillo;

    public AjusteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ajuste, container, false);
        cargarRecursosFragmento(view);
        cargarEventoBtn();
        return view;
    }

    private void cargarEventoBtn() {
        btnColorAzul.setOnClickListener(v -> cargarColorPrimario(R.style.OverlayThemeAzul));
        btnColorRosa.setOnClickListener(v -> cargarColorPrimario(R.style.OverlayThemeRosaClaro));
        btnColorMarron.setOnClickListener(v -> cargarColorPrimario(R.style.OverlayThemeMarron));
        btnColorAmarillo.setOnClickListener(v -> cargarColorPrimario(R.style.OverlayThemeAmarilloClaro));
        btnColorVerde.setOnClickListener(v -> cargarColorPrimario(R.style.OverlayThemeVerdeClaro));
    }

    private void cargarColorPrimario(int themeId) {
        SharedPreferences preferences = requireActivity().getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("selected_theme", themeId);
        editor.apply();
        requireActivity().recreate();
    }
    private void cargarRecursosFragmento(View view) {
        btnColorAzul = view.findViewById(R.id.btn_color_primario_azul);
        btnColorRosa = view.findViewById(R.id.btn_color_primario_rosa);
        btnColorMarron = view.findViewById(R.id.btn_color_primario_marron);
        btnColorVerde = view.findViewById(R.id.btn_color_primario_verde);
        btnColorAmarillo = view.findViewById(R.id.btn_color_primario_amarillo);
    }
}
