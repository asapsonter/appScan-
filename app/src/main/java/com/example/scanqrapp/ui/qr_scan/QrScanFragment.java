package com.example.scanqrapp.ui.qr_scan;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.budiyev.android.codescanner.CodeScanner;
import com.example.scanqrapp.databinding.FragmentQrScanBinding;
import com.example.scanqrapp.models.SingleScannedRow;
import com.google.zxing.Result;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QrScanFragment extends Fragment implements ZXingScannerView.ResultHandler {

    private CodeScanner mCodeScanner;
    private ZXingScannerView mScannerView;
    private int zone, building, floor;
    private FragmentQrScanBinding binding;
    private String fileName;
    private ArrayList<SingleScannedRow> excelRowArrayList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentQrScanBinding.inflate(getLayoutInflater());
        setScannerView();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) zone = getArguments().getInt("zone", 1);
        building = getArguments().getInt("building");
        floor = getArguments().getInt("floor");
        fileName = "building" + building + ".xls";

        //scannerView
        SingleScannedRow header = new SingleScannedRow();
        header.uuid = "uuid";
        header.building = "building";
        header.floor = "floor";
        header.zone = "zone";
        header.uniqueId = "unique_id";
        header.productName = "product_name";
        excelRowArrayList.add(header);
        setListeners();
        readExcelFile();
        mScannerView = binding.scannerView;


    }
    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }


    private void setScannerView() {

    }




    @Override
    public void handleResult(Result rawResult) {
        // ****** set result instance

        //replace old rawResult  with new result
        String result = rawResult.getText().replace(" ", "");
        //split string with reg expression
        String[] splitString = result.split("\\n"); // remove next line

        Log.d("read data is 0", splitString[0].substring(0,30)); // set first 30 strings
        Log.d("read data is 1", splitString[1]);

        if(1 == 1){
            Log.e("QR", rawResult.getText() );

            //create Scannedrow New instance
            SingleScannedRow singleScannedRow = new SingleScannedRow();
            singleScannedRow.uuid = splitString[0].substring(0,30); // set uuid substring from 0-30 index
            singleScannedRow.productName = splitString[1];
            singleScannedRow.building = building + "";
            singleScannedRow.floor = floor + "";
            singleScannedRow.zone = zone + "";
            singleScannedRow.uniqueId = building + "_" + floor + "_" + zone + "_"
                    + splitString[0].substring(0, 4) + "_"
                    + splitString[1].substring(0, 4);

            //throw exception if row already has UUID scanned
            if (excelRowArrayList.contains(singleScannedRow)){
                Toast.makeText(requireActivity(), "QR Already exists", Toast.LENGTH_LONG).show();
                Navigation.findNavController(binding.getRoot())
                        .popBackStack();
            } else {
                excelRowArrayList.add(singleScannedRow);
                saveNewRecordIntoExcel();
            }



        } else {
            Log.e("QR",rawResult.getText());
            Toast.makeText(requireActivity(), "Invailed QR", Toast.LENGTH_LONG).show();
            mScannerView.resumeCameraPreview(this);
        }


    }
    private void setListeners() {
        // set listner to navigate back

        binding.viewBack.setOnClickListener(view -> {
            Navigation.findNavController(binding.getRoot()).popBackStack();

                });

                // initiate scan seekbar
        binding.sbZoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.e("SeeKBar", "" + i);
                mScannerView.setCameraDistance(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });
        }

    private void readExcelFile(){

    }

    private void saveNewRecordIntoExcel() {
    }
}