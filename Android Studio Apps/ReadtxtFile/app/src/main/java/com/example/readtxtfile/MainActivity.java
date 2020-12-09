package com.example.readtxtfile;

//https://www.youtube.com/watch?v=wfucGSKngq4

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button scanBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanBtn = findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(this);


        Double add = 0.0;

        TextView textView = (TextView) findViewById(R.id.textViewer);
//        TextView tv_textView = (TextView) findViewById(R.id.tv_textView);

        String[] scanner_out = {"5000159503631", "5000431027985", "5000159500920","111", "99"};

        for (int i = 0; i <=3; i++) {
            try {
                add = Double.parseDouble(ReadfromFile(scanner_out[i])[1]) + add;
            }
            catch  (Exception e)
            {
                Toast.makeText(this, "scanner out array out of reach", Toast.LENGTH_SHORT).show();
            }

        }
//        txtFile.setText(ReadfromFile(scanner_out)[0].toString());//product
//        txtContent.setText(ReadfromFile(scanner_out)[1].toString());//price

//        add = Integer.parseInt(ReadfromFile(scanner_out)[1].toString());
//        add = add + 10;
//        Toast.makeText(this,  ,Toast.LENGTH_LONG).show();

        textView.setText(String.valueOf(add));

    }


    // Next method/function for scanner
    @Override
    public void onClick(View v) {
        scanCode();
    }


    private void scanCode()
    {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning code...");
        integrator.initiateScan();
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result!= null){
            if(result.getContents() != null){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(result.getContents());
                builder.setTitle("Scanned Result");
                builder.setPositiveButton("Scan agaain", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scanCode();
                    }
                }).setNegativeButton("finish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else{
                Toast.makeText(this, "No results", Toast.LENGTH_SHORT).show();
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }







    public String[] ReadfromFile(String inputBr){

            AssetManager assetManager = getAssets();
            InputStream input;
    //        String line = "";
            String cvsSplitBy = ",";
            String[] temp_ret = {"              ","              "};

            try {
    //            FileInputStream fileInputStream = openFileInput("file.text");
                input   = assetManager.open("file.txt");
                InputStreamReader inputStreamReader = new InputStreamReader(input);

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuffer stringBuffer = new StringBuffer();

                String[] brcode;

                String lines;
    //            String[] country = new String[10];

                while  ((lines = bufferedReader.readLine()) !=null)
                {
//                    temp_ret[0] ="              ";
//                    temp_ret[1] ="              ";
                    brcode = lines.split(cvsSplitBy);
                    if (brcode[2].equals(inputBr)) //brcode[2].toString() == inputBr.toString())
                    {
                        temp_ret[0]= brcode[3];
                        temp_ret[1]= brcode[5].replace("£","");
                    }
//                    barcodeBuffer.append(brcode[2]+ "\t" + brcode[3] + "\t" + brcode[5]+"\n");
    //                txtFile.setText("Barcode " + brcode[2] );
//                    stringBuffer.append(lines+"\n");
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return temp_ret;

    }


}
