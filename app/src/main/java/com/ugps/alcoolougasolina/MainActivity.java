package com.ugps.alcoolougasolina;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText editPrecoAlcool, editPrecoGasolina;
    private TextView textResultado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializando os componentes da interface
        editPrecoAlcool = findViewById(R.id.editPrecoAlcool);
        editPrecoGasolina = findViewById(R.id.editPrecoGasolina);
        textResultado = findViewById(R.id.textResultado);
    }

    public void calcularPreco(View view){

        //coleta do que o usuário digitou
        String precoAlcool   = editPrecoAlcool.getText().toString();
        String precoGasolina = editPrecoGasolina.getText().toString();

        //validação dos campos digitados
        Boolean camposValidados = validarCampos(precoAlcool,precoGasolina);
        if(camposValidados){
            //Se a pessoa digitou tudo certo, vai cair aqui:

            //convertendo string para números
            Double valorAlcool   = Double.parseDouble(precoAlcool);
            Double valorGasolina = Double.parseDouble(precoGasolina);

            //validação lógica
            if (valorAlcool/valorGasolina >= 0.7){
                textResultado.setText("Melhor utilizar gasolina");
            } else {
                textResultado.setText("Melhor utilizar alcool");
            }

        } else {
            //Se a pessoa cometeu erro na digitação, vai cair aqui:
            textResultado.setText("Preencha os preços primeiro!");
        }
    }

    public Boolean validarCampos(String pAlcool, String pGasolina){

        Boolean camposValidados = true;

        if( pAlcool == null || pAlcool.equals("") ) { //nulo ou vazio
            camposValidados = false;
        } else if (pGasolina == null || pGasolina.equals("") ){
            camposValidados = false;
        }

        return camposValidados;
    }

}
