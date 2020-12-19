package com.ugps.alcoolougasolina;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("SetTextI18n") // remove warning de string hardcoded
public class MainActivity extends AppCompatActivity {

    private EditText editPrecoEtanol, editPrecoGasolina, editCaptcha;
    private TextView textResultado;
    private ImageView captchaView;
    private View captchaContainer;
    private ImageButton captchaRefresh;
    private Button buttonResultado;

    private Spinner selectEstados, selectMunicipios;
    private ArrayAdapter<OptionModel<String>> adapterEstados;
    private ArrayAdapter<OptionModel<PrecosModel>> adapterMunicipios;

    private final ANPServiceProvider anp = new ANPServiceProvider();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializando os componentes da interface
        editPrecoEtanol = findViewById(R.id.editPrecoEtanol);
        editPrecoGasolina = findViewById(R.id.editPrecoGasolina);
        textResultado = findViewById(R.id.textResultado);
        captchaContainer = findViewById(R.id.captchaContainer);
        captchaView = findViewById(R.id.imageCaptcha);
        editCaptcha = findViewById(R.id.editCaptcha);
        captchaRefresh = findViewById(R.id.captchaRefresh);
        selectEstados = findViewById(R.id.selectEstados);
        selectMunicipios = findViewById(R.id.selectMunicipios);
        buttonResultado = findViewById(R.id.buttonResultado);

        adapterEstados = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapterEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectEstados.setAdapter(adapterEstados);

        adapterMunicipios = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapterMunicipios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectMunicipios.setAdapter(adapterMunicipios);

        bindListeners();

        loadEstados();
    }

    private void bindListeners() {

        editCaptcha.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                loadMunicipios();
            }
        });

        captchaRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // atualiza somente a imagem, se quisermos atualizar as letras
                // temos que chamar loadEstados() para mudar os cookies
                loadCaptcha();
            }
        });

        selectEstados.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadMunicipios();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // do nothing
            }
        });

        selectMunicipios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pos > 0) {
                    PrecosModel precos = adapterMunicipios.getItem(pos).getValue();

                    editPrecoGasolina.setText(String.valueOf(precos.getGasolina()));
                    editPrecoEtanol.setText(String.valueOf(precos.getEtanol()));

                    calcularPreco();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // do nothing
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
        anp.getEstados(new ANPServiceProvider.Callback<List<OptionModel<String>>>() {
            @Override
            public void onResult(List<OptionModel<String>> options) {
                adapterEstados.clear();
                adapterEstados.addAll(options);
                adapterEstados.notifyDataSetChanged();

                selectEstados.setVisibility(View.VISIBLE);
                captchaContainer.setVisibility(View.VISIBLE);

                loadCaptcha();
            }

            @Override
            public void onError(String error) {
                textResultado.setText(error);
            }
        });
    }

    private void loadMunicipios() {
        textResultado.setText("");

        int pos = selectEstados.getSelectedItemPosition();

        if (pos > 0 && editCaptcha.length() == 5) {

            textResultado.setText("Carregando municípios...");

            String estado = adapterEstados.getItem(pos).getValue();
            String captcha = editCaptcha.getText().toString().toUpperCase();

            anp.getMunicipios(estado, captcha, new ANPServiceProvider.Callback<List<OptionModel<PrecosModel>>>() {
                @Override
                public void onResult(List<OptionModel<PrecosModel>> options) {
                    adapterMunicipios.clear();
                    adapterMunicipios.addAll(options);
                    adapterMunicipios.notifyDataSetChanged();

                    selectMunicipios.setVisibility(View.VISIBLE);
                    textResultado.setText("");
                }

                @Override
                public void onError(String error) {
                    textResultado.setText(error);
                }
            });
        }
    }

    private void loadCaptcha() {
        editCaptcha.setText("");
        editCaptcha.setError(null);

        anp.getCaptcha(new ANPServiceProvider.Callback<Bitmap>() {
            @Override
            public void onResult(Bitmap data) {
                captchaView.setImageBitmap(data);
            }

            @Override
            public void onError(String error) {
                editCaptcha.setError(error);
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
