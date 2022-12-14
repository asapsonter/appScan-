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

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

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

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
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
    // read excel file method
    private void readExcelFile(){
        File file = new File(requireContext().getExternalFilesDir(null), fileName);
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
            excelRowArrayList = new ArrayList<>();
            // Create instance having reference to .xls file
            Workbook workbook = new HSSFWorkbook(fileInputStream);

            // Fetch sheet at position 'i' from the workbook
            Sheet sheet = workbook.getSheetAt(0);

            // Iterate through each row
            for (Row row: sheet){
                Iterator<Cell> cellIterator = row.cellIterator();
                SingleScannedRow singleScannedRow = new SingleScannedRow();

                while (cellIterator.hasNext()){ // returns true if cell has token
                    //find and return the next token
                    Cell cell = cellIterator.next();

                    // Check cell type and format accordingly
                    switch (cell.getColumnIndex()){
                        case 0:
                            singleScannedRow.uuid = cell.getStringCellValue();
                            break;
                        case 1:
                            singleScannedRow.building = cell.getStringCellValue();
                            break;
                        case 2:
                            singleScannedRow.floor = cell.getStringCellValue();
                            break;
                        case 3:
                            singleScannedRow.zone = cell.getStringCellValue();
                            break;

                        case 4:
                            singleScannedRow.uniqueId = cell.getStringCellValue();
                            break;
                        case 5:
                            singleScannedRow.productName = cell.getStringCellValue();
                            break;
                    }
                }
                excelRowArrayList.add(singleScannedRow);
            }

        } catch (IOException e) {
            Log.e("File IOException", "Error Reading Exception: ", e);
        } catch (Exception e) {
            Log.e("File Exception", "Failed to read file due to Exception: ", e);
        } finally {
            try {
                if (null != fileInputStream) {
                    fileInputStream.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }


    }

    private void saveNewRecordIntoExcel() {

        File file = new File(requireContext().getExternalFilesDir(null), fileName);
        FileOutputStream fileOutputStream = null;
        try {
            if (!file.exists())
                file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            Workbook workbook = new HSSFWorkbook();
            Sheet sheet;
            if (workbook.getNumberOfSheets() > 0) {
                sheet = workbook.getSheetAt(0);
            } else {
                sheet = workbook.createSheet("Sheet 1");
            }
            sheet.setColumnWidth(0, (15 * 700));
            sheet.setColumnWidth(1, (15 * 200));
            sheet.setColumnWidth(2, (15 * 200));
            sheet.setColumnWidth(3, (15 * 200));
            sheet.setColumnWidth(4, (15 * 400));
            sheet.setColumnWidth(5, (15 * 700));

            for (int i = 0; i < excelRowArrayList.size(); i++) {
                Row rowData = sheet.createRow(i);
                Cell cell = rowData.createCell(0);
                cell.setCellValue(excelRowArrayList.get(i).uuid);

                Cell cell1 = rowData.createCell(1);
                cell1.setCellValue(excelRowArrayList.get(i).building);

                Cell cell2 = rowData.createCell(2);
                cell2.setCellValue(excelRowArrayList.get(i).floor);

                Cell cell3 = rowData.createCell(3);
                cell3.setCellValue(excelRowArrayList.get(i).zone);

                Cell cell4 = rowData.createCell(4);
                cell4.setCellValue(excelRowArrayList.get(i).uniqueId);

                Cell cell5 = rowData.createCell(5);
                cell5.setCellValue(excelRowArrayList.get(i).productName);

                Font font = workbook.createFont();
                font.setColor(IndexedColors.BLACK.getIndex());
                if (i == 0) {
                    font.setBoldweight(Short.parseShort("600"));
                    CellStyle headerCellStyle;
                    headerCellStyle = workbook.createCellStyle();
                    headerCellStyle.setFont(font);
                    headerCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
                    cell.setCellStyle(headerCellStyle);
                    cell1.setCellStyle(headerCellStyle);
                    cell2.setCellStyle(headerCellStyle);
                    cell3.setCellStyle(headerCellStyle);
                    cell4.setCellStyle(headerCellStyle);
                    cell5.setCellStyle(headerCellStyle);
                } else {
                    CellStyle headerCellStyle;
                    headerCellStyle = workbook.createCellStyle();
                    headerCellStyle.setFont(font);
                    headerCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
                    cell.setCellStyle(headerCellStyle);
                    cell1.setCellStyle(headerCellStyle);
                    cell2.setCellStyle(headerCellStyle);
                    cell3.setCellStyle(headerCellStyle);
                    cell4.setCellStyle(headerCellStyle);
                    cell5.setCellStyle(headerCellStyle);
                }

            }

            workbook.write(fileOutputStream);

        } catch (IOException e) {
            Log.e("File IOException", "Error Reading Exception: ", e);
        } catch (Exception e) {
            Log.e("File Exception", "Failed to read file due to Exception: ", e);
        } finally {
            try {
                if (null != fileOutputStream) {
                    Toast.makeText(
                            requireContext(),
                            "Entry saved into excel file.",
                            Toast.LENGTH_LONG
                    ).show();
                    fileOutputStream.close();
                    Navigation
                            .findNavController(binding.getRoot())
                            .popBackStack();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}