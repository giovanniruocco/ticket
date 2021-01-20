package com.ticket.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentDetails extends AppCompatActivity {

    TextView txtId, txtAmount, txtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);
        setTitle("Payment Details");

        //layout references
        txtId = findViewById(R.id.txtId);
        txtAmount = findViewById(R.id.txtAmount);
        txtStatus = findViewById(R.id.txtStatus);

        Intent intento = getIntent();

        try{
            JSONObject jsonObject = new JSONObject(intento.getStringExtra("PaymentDetails"));
            showDetails(jsonObject.getJSONObject("response"),intento.getStringExtra("PaymentAmount"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Show payment details
    private void showDetails(JSONObject response, String paymentAmount) {
        try {
            txtId.setText(response.getString("id"));
            txtStatus.setText(response.getString("state"));
            txtAmount.setText(response.getString(String.format("€%s",paymentAmount)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onBackPressed() {
        startActivity(new Intent(PaymentDetails.this,MainActivity.class));
    }

}