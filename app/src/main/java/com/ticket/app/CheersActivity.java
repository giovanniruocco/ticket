package com.ticket.app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.ticket.app.Config.Config;

import org.json.JSONException;

import java.math.BigDecimal;

public class CheersActivity extends AppCompatActivity {

    public static final int PAYPAL_REQUEST_CODE = 7171;

    private static PayPalConfiguration config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX).clientId(Config.PAYPAL_CLIENT_ID);

    String donation = "";
    Button btnPayNow;
    EditText edtAmout;
    String amount = "";

    @Override
    protected void onDestroy() {
        stopService(new Intent (this, PayPalService.class));
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        donation = intent.getExtras().getString("Donation");


        //Start paypal service
        Intent intento = new Intent(this, PayPalService.class);
        intento.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intento);
        processPayment();

    }
    private void processPayment() {
        amount = donation;
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), "EUR", "Donate for Ticket4Sale", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intento = new Intent(this, PaymentActivity.class);
        intento.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        intento.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intento, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PAYPAL_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null)
                {
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);

                        startActivity(new Intent(this, PaymentDetails.class).putExtra("PaymentDetails", paymentDetails).putExtra("PaymentAmount", amount));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(CheersActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CheersActivity.this,MainActivity.class));
            }
        }else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Toast.makeText(CheersActivity.this, "Invalid", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CheersActivity.this, MainActivity.class));
        }

    }

}