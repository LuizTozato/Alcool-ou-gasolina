package com.ugps.alcoolougasolina;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editPrecoAlcool, editPrecoGasolina, editCaptcha;
    private TextView textResultado;

    private final ANPServiceProvider anp = new ANPServiceProvider();
    private ImageView captchaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializando os componentes da interface
        editPrecoAlcool = findViewById(R.id.editPrecoAlcool);
        editPrecoGasolina = findViewById(R.id.editPrecoGasolina);
        textResultado = findViewById(R.id.textResultado);
        captchaView = findViewById(R.id.imageCaptcha);
        editCaptcha = findViewById(R.id.editCaptcha);

        Button buttonResultado = findViewById(R.id.buttonResultado);

        buttonResultado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calcularPreco();
            }
        });

        final ImageButton captchaRefresh = findViewById(R.id.captchaRefresh);

        captchaRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCaptcha();
            }
        });

        getCaptcha();
    }

    private void getCaptcha() {
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

    @SuppressLint("SetTextI18n") // remove warning de string hardcoded
    private void calcularPreco() {

        editPrecoAlcool.setError(null);
        editPrecoGasolina.setError(null);

        //coleta do que o usuário digitou
        String precoAlcool = String.valueOf(editPrecoAlcool.getText()).trim();
        String precoGasolina = String.valueOf(editPrecoGasolina.getText()).trim();

        //validação dos campos digitados
        if (validarCampos(precoAlcool, precoGasolina)) {
            //Se a pessoa preencheu os campos, vai cair aqui:

            double valorAlcool = getValor(precoAlcool, new EditTextErrorListener(editPrecoAlcool));
            double valorGasolina = getValor(precoGasolina, new EditTextErrorListener(editPrecoGasolina));

            //Se a pessoa digitou tudo certo, vai cair aqui:

            if (valorAlcool > 0 && valorGasolina > 0) {
                //validação lógica
                if (valorAlcool / valorGasolina >= 0.7) {
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
