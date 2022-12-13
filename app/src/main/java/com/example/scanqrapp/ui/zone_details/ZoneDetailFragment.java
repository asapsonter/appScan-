package com.example.scanqrapp.ui.zone_details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.scanqrapp.databinding.FragmentZoneDetailBinding;
import com.example.scanqrapp.models.SingleScannedRow;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;

public class ZoneDetailFragment extends Fragment implements DetailFragmentCallbacks {

    private ZoneDetailViewModel mViewModel;
    private FragmentZoneDetailBinding binding;
    private int zone, building, floor;
    private CellStyle headerCellStyle;
    private Workbook workbook;
    private ArrayList<SingleScannedRow> excelRowArrayList = new ArrayList<>();

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

        zone = getArguments().getInt("zone",1);
        building = getArguments().getInt("building");
        floor = getArguments().getInt("floor");
        fileName = "building"  + binding + "xls";


    }
    // method to set recycler view at scanned zone fragment
    private void  SetScannedQrRecyclerView() {
        ScannedQrAdapter scannedQrAdapter = new ScannedQrAdapter(excelRowArrayList, requireContext(), this);
        binding.rvScannedQrs.setAdapter(scannedQrAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        // linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        binding.rvScannedQrs.setLayoutManager(linearLayoutManager);

    }



    @Override
    public void onItemLongPress(SingleScannedRow position) {

    }


}