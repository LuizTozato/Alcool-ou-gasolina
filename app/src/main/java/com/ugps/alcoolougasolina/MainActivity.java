package com.ugps.alcoolougasolina;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ugps.alcoolougasolina.interfaces.Callback;
import com.ugps.alcoolougasolina.interfaces.ErrorListener;
import com.ugps.alcoolougasolina.models.EstadoModel;
import com.ugps.alcoolougasolina.models.PrecosModel;
import com.ugps.alcoolougasolina.services.ANPServiceProvider;
import com.ugps.alcoolougasolina.utils.EditTextErrorListener;
import com.ugps.alcoolougasolina.utils.OnItemSelectedListener;
import com.ugps.alcoolougasolina.utils.SpinnerAdapter;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("SetTextI18n") // remove warning de string hardcoded
public class MainActivity extends AppCompatActivity {

    private EditText editPrecoEtanol, editPrecoGasolina;
    private TextView textResultado;
    private Button buttonResultado;

    private Spinner selectEstados, selectMunicipios;
    private SpinnerAdapter<EstadoModel> adapterEstados;
    private SpinnerAdapter<PrecosModel> adapterMunicipios;

    private final ANPServiceProvider anp = new ANPServiceProvider();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializando os componentes da interface
        editPrecoEtanol = findViewById(R.id.editPrecoEtanol);
        editPrecoGasolina = findViewById(R.id.editPrecoGasolina);
        textResultado = findViewById(R.id.textResultado);
        selectEstados = findViewById(R.id.selectEstados);
        selectMunicipios = findViewById(R.id.selectMunicipios);
        buttonResultado = findViewById(R.id.buttonResultado);

        adapterEstados = new SpinnerAdapter<>(this);
        selectEstados.setAdapter(adapterEstados);

        adapterMunicipios = new SpinnerAdapter<>(this);
        selectMunicipios.setAdapter(adapterMunicipios);

        bindListeners();

        loadEstados();
    }

    private void bindListeners() {

        selectEstados.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                if (position > 0) {
                    loadMunicipios(adapterEstados.getItem(position).getId());
                }
            }
        });

        selectMunicipios.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                if (position > 0) {
                    PrecosModel precos = adapterMunicipios.getItem(position);

                    editPrecoGasolina.setText(String.valueOf(precos.getGasolina()));
                    editPrecoEtanol.setText(String.valueOf(precos.getEtanol()));

                    calcularPreco();
                }
            }
        });

        buttonResultado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calcularPreco();
            }
        });
    }

    private void loadEstados() {
        anp.getEstados(new Callback<List<EstadoModel>>() {
            @Override
            public void onResult(List<EstadoModel> options) {
                adapterEstados.setData(options);

                selectEstados.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String error) {
                textResultado.setText(error);
            }
        });
    }

    private void loadMunicipios(String estado) {
        textResultado.setText("");
        editPrecoEtanol.setText("");
        editPrecoGasolina.setText("");

        textResultado.setText("Carregando municípios...");

        anp.getMunicipios(estado, new Callback<List<PrecosModel>>() {
            @Override
            public void onResult(List<PrecosModel> options) {
                adapterMunicipios.setData(options);

                selectMunicipios.setSelection(0);
                selectMunicipios.setVisibility(View.VISIBLE);

                textResultado.setText("");
            }

            @Override
            public void onError(String error) {
                textResultado.setText(error);
            }
        });
    }

    private void calcularPreco() {

        editPrecoEtanol.setError(null);
        editPrecoGasolina.setError(null);

        //coleta do que o usuário digitou
        String precoEtanol = String.valueOf(editPrecoEtanol.getText()).trim();
        String precoGasolina = String.valueOf(editPrecoGasolina.getText()).trim();

        //validação dos campos digitados
        if (validarCampos(precoEtanol, precoGasolina)) {
            //Se a pessoa preencheu os campos, vai cair aqui:

            double valorEtanol = getValor(precoEtanol, new EditTextErrorListener(editPrecoEtanol));
            double valorGasolina = getValor(precoGasolina, new EditTextErrorListener(editPrecoGasolina));

            //Se a pessoa digitou tudo certo, vai cair aqui:

            if (valorEtanol > 0 && valorGasolina > 0) {
                //validação lógica
                if (valorEtanol / valorGasolina >= 0.7) {
                    textResultado.setText("Melhor utilizar gasolina");
                } else {
                    textResultado.setText("Melhor utilizar alcool");
                }
            }

        } else {
            //Se a pessoa cometeu erro na digitação, vai cair aqui:
            textResultado.setText("Preencha os preços primeiro!");
        }
    }

    private double getValor(String preco, ErrorListener errorListener) {
        double valor = 0;
        try {
            //convertendo string para números
            valor = Double.parseDouble(preco);
            if (valor <= 0) {
                errorListener.onError("Valor deve ser maior que zero!");
            }
        } catch (NumberFormatException ex) {
            errorListener.onError("Campo não preenchido corretamente!");
        }
        return valor;
    }

    private boolean validarCampos(String pAlcool, String pGasolina) {
        return !TextUtils.isEmpty(pAlcool) && !TextUtils.isEmpty(pGasolina); // valida nulo ou vazio
    }

}
