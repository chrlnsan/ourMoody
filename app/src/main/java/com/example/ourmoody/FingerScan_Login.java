/*
package com.example.ourmoody;

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


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        resultDisplay = findViewById(R.id.resultDisplay);
        fingerScanButton = findViewById(R.id.fingerScanButton);

        final int fingerprintAuthDrawable;

        executor = ContextCompat.getMainExecutor(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            biometricManager = (BiometricManager) getSystemService(BIOMETRIC_SERVICE);
        }

        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                resultDisplay.setText("Error");
                fingerScanButton.setImageResource(R.drawable.neutral);
                super.onAuthenticationError(errorCode, errString);
            }


            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                resultDisplay.setText("Danke!");
                fingerScanButton.setImageResource(R.drawable.happy);
                super.onAuthenticationSucceeded(result);
            }

            @Override
            public void onAuthenticationFailed() {
                resultDisplay.setText("Bitte versuche es erneut!");
                fingerScanButton.setImageResource(R.drawable.sad);
                super.onAuthenticationFailed();
        }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("FingerScan Authentication")
                .setConfirmationRequired(false)
                .build();


    public void fingerScanButton(View resultDisplay){
        BiometricManager biometricManager = BiometricManager.from(this);
        if(biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS){
            resultDisplay.setText("Biometric not supported");
            return;
        }

            biometricPrompt.authenticate(promptInfo);
        }
    };

};

*/