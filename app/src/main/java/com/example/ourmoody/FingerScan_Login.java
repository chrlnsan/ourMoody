
package com.example.ourmoody;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class FingerScan_Login extends AppCompatActivity {
    private static final String TAG = "FingerScan_Login";

    private TextView resultDisplay;
    private ImageButton fingerScanButton;

    private BiometricManager biometricManager;
    private BiometricPrompt.AuthenticationCallback authenticationCallback;

    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private Executor executor;


    //Fingerprint biometric authentication
    public void fingerScanButton(View resultDisplay){
        BiometricManager biometricManager = BiometricManager.from(this);
        if(biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS){
            resultDisplay.setContentDescription("Biometric not supported");
            return;
        }

        biometricPrompt.authenticate(promptInfo);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        resultDisplay = findViewById(R.id.resultDisplay);
        fingerScanButton = findViewById(R.id.fingerScanButton);

        final int fingerprintAuthDrawable;

        //checkt ob Fingerscan auf dem Geraet moeglich ist
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()){
            //hier werden constante geswitched um die möglichen Outputs zu checken
            case BiometricManager.BIOMETRIC_SUCCESS: //bedeutet, dass biometrischer Sensor eingebaut ist
                resultDisplay.setText("Fingerprint-Sensor ist zum Einloggen verfügbar");
                resultDisplay.setTextColor(Color.parseColor("#000"));
                fingerScanButton.setVisibility(View.GONE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                resultDisplay.setText("Fingerprint-Sensor wird nicht unterstützt");
                resultDisplay.setTextColor(Color.parseColor("#000"));
                fingerScanButton.setVisibility(View.GONE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                resultDisplay.setText("Fingerprint-Sensor ist derzeit nicht verfügbar");
                resultDisplay.setTextColor(Color.parseColor("#000"));
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                resultDisplay.setText("Es ist kein Fingerabdruck auf diesem Fingerprint-Sensor gespeichert");
                resultDisplay.setTextColor(Color.parseColor("#000"));
                fingerScanButton.setVisibility(View.GONE);
                break;
        }
        /* Urspruenglicher Code zum Checken ob biometrischer Sensor eingebaut ist, wurde durch obigen Code ersetzt
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            biometricManager = (BiometricManager) getSystemService(BIOMETRIC_SERVICE);
        }*/

        //das ist unser biometric executor
        executor = ContextCompat.getMainExecutor(this);

        //die folgenden Zeilen geben uns mit dem authenticationCallback bekannt, ob ein Login moeglich ist oder nicht

        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override //diese Methode wird aufgerufen, wenn es einen Error bei der Authentication gibt
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                resultDisplay.setText("Error");
                fingerScanButton.setImageResource(R.drawable.neutral);
                super.onAuthenticationError(errorCode, errString);
            }


            @Override //diese Methode gibt an, dass die Authentication erfolgreich war
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                resultDisplay.setText("Login erfolgreich!");
                fingerScanButton.setImageResource(R.drawable.happy);
                super.onAuthenticationSucceeded(result);
            }

            @Override //diese Methode wird angezeigt, wenn die Authentication fehlgeschlagen ist
            public void onAuthenticationFailed() {
                resultDisplay.setText("Bitte versuche es erneut!");
                fingerScanButton.setImageResource(R.drawable.sad);
                super.onAuthenticationFailed();
        }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login")
                .setDescription("Zum Entsperren lege  deinen Finger auf das Symbol")
                .setNegativeButtonText("Abbrechen")
                .setConfirmationRequired(false)
                .build();

        fingerScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                biometricPrompt.authenticate(promptInfo);
            }
        });

/* Überflüssig, wird durch obigen Code ersetzt
    public void fingerScanButton(View resultDisplay){
        BiometricManager biometricManager = BiometricManager.from(this);
        if(biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS){
            resultDisplay.setText("Biometric not supported");
            return;
        }

            biometricPrompt.authenticate(promptInfo);
        } */
    };

};

