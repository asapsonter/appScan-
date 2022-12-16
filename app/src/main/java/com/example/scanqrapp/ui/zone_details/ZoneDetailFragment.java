package com.example.scanqrapp.ui.zone_details;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.scanqrapp.R;
import com.example.scanqrapp.databinding.FragmentZoneDetailBinding;
import com.example.scanqrapp.models.SingleScannedRow;

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

public class ZoneDetailFragment extends Fragment implements DetailFragmentCallbacks {
    private ZoneDetailViewModel mViewModel;
    private FragmentZoneDetailBinding binding;
    private int zone, building, floor;
    private CellStyle headerCellStyle;
    private Workbook workbook;
    private ArrayList<SingleScannedRow> excelRowArrayList = new ArrayList<>();
    private ArrayList<SingleScannedRow> excelRowArrayListComplete = new ArrayList<>();
    private String fileName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentZoneDetailBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ZoneDetailViewModel.class);

        zone = getArguments().getInt("zone", 1);
        building = getArguments().getInt("building");
        floor = getArguments().getInt("floor");
        fileName = "building" + building + ".xls";

        setListeners();
//        createExcelFile(fileName);
    }



    @Override
    public void onResume() {
        super.onResume();
        readExcelFile();
        setScannedQrRecyclerView();
    }


    private void setScannedQrRecyclerView() {
        ScannedQrAdapter scannedQrAdapter = new ScannedQrAdapter(excelRowArrayList, requireContext(), this);
        binding.rvScannedQrs.setAdapter(scannedQrAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rvScannedQrs.setLayoutManager(linearLayoutManager);

    }

    private void readExcelFile() {
        File file = new File(requireContext().getExternalFilesDir(null), fileName);

        FileInputStream fileInputStream = null;
        excelRowArrayList = new ArrayList<>();
        excelRowArrayListComplete = new ArrayList<>();

        try {
            fileInputStream = new FileInputStream(file);

            // Create instance having reference to .xls file
            Workbook workbook = new HSSFWorkbook(fileInputStream);

            // Fetch sheet at position 'i' from the workbook
            Sheet sheet = workbook.getSheetAt(0);

            // Iterate through each row
            for (Row row : sheet) {
                Iterator<Cell> cellIterator = row.cellIterator();
                SingleScannedRow singleScannedRow = new SingleScannedRow();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    // Check cell type and format accordingly
                    switch (cell.getColumnIndex()) {
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
                if (
                        singleScannedRow.building.equals(building + "")
                                && singleScannedRow.floor.equals(floor + "")
                                && singleScannedRow.zone.equals(zone + "")
                ) {
                    excelRowArrayList.add(singleScannedRow);
                }
                excelRowArrayListComplete.add(singleScannedRow);

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

    private void setListeners() {

        binding.viewBack.setOnClickListener(view -> Navigation.findNavController(binding.getRoot()).popBackStack());

        binding.ivQr.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED) {
                ActivityCompat
                        .requestPermissions(
                                requireActivity(),
                                new String[]{Manifest.permission.CAMERA},
                                1
                        );
            } else {
                Bundle bundle = new Bundle();
                bundle.putInt("zone", zone);
                bundle.putInt("building", building);
                bundle.putInt("floor", floor);
                Navigation
                        .findNavController(binding.getRoot())
                        .navigate(R.id.qrScanFragment, bundle);
            }

        });


    }

    @Override
    public void onItemLongPress(SingleScannedRow singleScannedRow) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Device")
                .setMessage("Do you really want to delete this device?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    excelRowArrayListComplete.remove(singleScannedRow);
                    saveNewRecordIntoExcel();
                    readExcelFile();
                    setScannedQrRecyclerView();
                    Toast.makeText(requireContext(), "Device Deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.no, null).show();
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

            for (int i = 0; i < excelRowArrayListComplete.size(); i++) {
                Row rowData = sheet.createRow(i);
                Cell cell = rowData.createCell(0);
                cell.setCellValue(excelRowArrayListComplete.get(i).uuid);

                Cell cell1 = rowData.createCell(1);
                cell1.setCellValue(excelRowArrayListComplete.get(i).building);

                Cell cell2 = rowData.createCell(2);
                cell2.setCellValue(excelRowArrayListComplete.get(i).floor);

                Cell cell3 = rowData.createCell(3);
                cell3.setCellValue(excelRowArrayListComplete.get(i).zone);

                Cell cell4 = rowData.createCell(4);
                cell4.setCellValue(excelRowArrayListComplete.get(i).uniqueId);

                Cell cell5 = rowData.createCell(5);
                cell5.setCellValue(excelRowArrayListComplete.get(i).productName);

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
                    fileOutputStream.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

}
