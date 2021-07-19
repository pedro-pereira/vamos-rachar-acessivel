package br.ufc.smd.vamosrachar;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.util.Locale;

public class VamosRacharActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener, TextToSpeech.OnInitListener {

    private EditText edtValor;
    private EditText edtQuantidadePessoas;
    private TextView txtResultado;
    private FloatingActionButton fbtCompartilhar;
    private FloatingActionButton fbtAudio;
    private DecimalFormat formatador;
    private Double resultado;

    private TextToSpeech ttsPlayer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtValor             = (EditText) findViewById(R.id.edtValor);
        edtQuantidadePessoas = (EditText) findViewById(R.id.edtQuantidadePessoas);
        txtResultado         = (TextView) findViewById(R.id.txtResultado);
        fbtCompartilhar      = (FloatingActionButton) findViewById(R.id.fbtCompartilhar);
        fbtAudio             = (FloatingActionButton) findViewById(R.id.fbtAudio);

        edtValor.addTextChangedListener(this);
        edtQuantidadePessoas.addTextChangedListener(this);

        fbtAudio.setOnClickListener(this);
        fbtCompartilhar.setOnClickListener(this);

        formatador = new DecimalFormat("0,00");
        
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, 1122);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1122) {
            if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                ttsPlayer = new TextToSpeech(this, this);
            } else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    private boolean campoEstaVazio(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;
        return true;
    }

    @Override
    public void afterTextChanged(Editable s) {
        try {
            double valor = 0;
            if(!campoEstaVazio(edtValor) && !campoEstaVazio(edtQuantidadePessoas)) {
                Double resultado = calculaValor(edtValor.getText().toString(), edtQuantidadePessoas.getText().toString());
                // Log.i("TESTE - Resultado", formatador.format(resultado * 100));
                String moeda = getString(R.string.unidadeMoeda);
                txtResultado.setText(moeda + " " + formatador.format(resultado * 100));
            } else {
                if(campoEstaVazio(edtValor)) {
                    txtResultado.setText(R.string.txtValorConta);
                }
                if(campoEstaVazio(edtQuantidadePessoas)) {
                    txtResultado.setText(R.string.txtQtdPessoas);
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void onClick(View v) {

        if(v == fbtAudio) {
            //Log.i("TESTE TTS", "cliquei no botao de audio");
            String toSpeak = txtResultado.getText().toString();

            if(ttsPlayer != null) {
                ttsPlayer.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, "ID1");
            }
        }


        if (v == fbtCompartilhar) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, R.string.txtResultado + resultado);
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        }
    }

    public Double calculaValor(String valor, String quantidadePessoas) {
        double conta = Double.valueOf(valor);
        int pessoas = Integer.valueOf(quantidadePessoas);

        if(pessoas < 1)
            pessoas = 1;

        double resultado = conta / pessoas;
        this.resultado = resultado;
        return resultado;
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS) {
            Toast.makeText(this, R.string.toastTTSAtivado, Toast.LENGTH_LONG).show();
        } else if(status == TextToSpeech.ERROR) {
            Toast.makeText(this, R.string.toastTTSDesativado, Toast.LENGTH_LONG).show();
        }
    }
}