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
import android.widget.TextView;

import com.example.a360chatapp.R;

public class AjusteFragment extends Fragment {

    private Button btnColorAzul;
    private Button btnColorRosa;
    private Button btnColorMarron;
    private Button btnColorVerde;
    private Button btnColorAmarillo;
    private int numeroColorTheme;
    private Button btnEnviarAjustes;
    private TextView elevacionRosa;
    private TextView elevacionMarron;
    private TextView elevacionAmarillo;
    private TextView elevacionVerde;
    private TextView elevacionAzul;


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
        numeroColorTheme = -1;
        cargarEventoBtn();
        return view;
    }

    private void cargarEventoBtn() {
        btnColorAzul.setOnClickListener(v -> {
            anularElevaciones();
            elevacionAzul.setVisibility(View.VISIBLE);
            cargarColorPrimario(R.style.OverlayThemeAzul);
        });
        btnColorRosa.setOnClickListener(v -> {
            anularElevaciones();
            elevacionRosa.setVisibility(View.VISIBLE);
            cargarColorPrimario(R.style.OverlayThemeRosaClaro);
        });
        btnColorMarron.setOnClickListener(v -> {
            anularElevaciones();
            elevacionMarron.setVisibility(View.VISIBLE);
            cargarColorPrimario(R.style.OverlayThemeMarron);
        });
        btnColorAmarillo.setOnClickListener(v -> {
            anularElevaciones();
            elevacionAmarillo.setVisibility(View.VISIBLE);
            cargarColorPrimario(R.style.OverlayThemeAmarilloClaro);
        });
        btnColorVerde.setOnClickListener(v -> {
            anularElevaciones();
            elevacionVerde.setVisibility(View.VISIBLE);
            cargarColorPrimario(R.style.OverlayThemeVerdeClaro);
        });
        btnEnviarAjustes.setOnClickListener(v -> actualizarAjustes());
    }

    private void anularElevaciones() {
        elevacionRosa.setVisibility(View.GONE);
        elevacionAmarillo.setVisibility(View.GONE);
        elevacionMarron.setVisibility(View.GONE);
        elevacionVerde.setVisibility(View.GONE);
        elevacionAzul.setVisibility(View.GONE);
    }

    private void actualizarAjustes() {
        if (-1 != numeroColorTheme){
            SharedPreferences preferences = requireActivity().getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("selected_theme", numeroColorTheme);
            editor.putInt("selected_menu_item", R.id.menu_ajustes);
            editor.apply();
            requireActivity().recreate();
        }
    }


    private void cargarColorPrimario(int themeId) {
        numeroColorTheme = themeId;
    }
    private void cargarRecursosFragmento(View view) {
        elevacionRosa = view.findViewById(R.id.elevacion_color_rosa);
        elevacionAmarillo = view.findViewById(R.id.elevacion_color_amarillo);
        elevacionMarron = view.findViewById(R.id.elevacion_color_marron);
        elevacionVerde = view.findViewById(R.id.elevacion_color_verde);
        elevacionAzul = view.findViewById(R.id.elevacion_color_azul);



        btnColorAzul = view.findViewById(R.id.btn_color_primario_azul);
        btnColorRosa = view.findViewById(R.id.btn_color_primario_rosa);
        btnColorMarron = view.findViewById(R.id.btn_color_primario_marron);
        btnColorVerde = view.findViewById(R.id.btn_color_primario_verde);
        btnColorAmarillo = view.findViewById(R.id.btn_color_primario_amarillo);
        btnEnviarAjustes = view.findViewById(R.id.btn_actualizar_ajustes);
    }
}
