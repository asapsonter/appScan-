package com.example.scanqrapp.ui.main;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.scanqrapp.R;
import com.example.scanqrapp.databinding.FragmentMainBinding;

public class MainFragment extends Fragment implements MainFragmentCallbacks{

    private MainViewModel mViewModel;
    private FragmentMainBinding binding;
    ZoneAdapter zoneAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        //call zone Recycler View
        setZoneRecyclerView();
        setSpinner();

    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    private void setZoneRecyclerView() {
        zoneAdapter = new ZoneAdapter(
                mViewModel.zoneList,
                requireContext(),
                mViewModel.building,
                mViewModel.floor,
                this

        );
        binding.rvZones.setAdapter(zoneAdapter);
        binding.rvZones.setLayoutManager(new GridLayoutManager(requireContext(), 1
        ));

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onZoneClick(int i) {
        if (i == mViewModel.zoneList.size()){
            mViewModel.incrementZoneList();
            zoneAdapter.notifyDataSetChanged();
        } else if ( i < mViewModel.zoneList.size()){
            Bundle bundle = new Bundle();
            bundle.putInt("zone", mViewModel.zoneList.get(i));
            bundle.putInt("building", Integer.parseInt(binding.spinnerBuilding.getSelectedItem().toString()));
            bundle.putInt("floor", Integer.parseInt(binding.spinnerFloor.getSelectedItem().toString()));

            Navigation.findNavController(
                    requireActivity(),
                    binding.getRoot().getId()
            );
                    //.navigate(R.id.zone_detail_fragment, bundle);
        }
    }
    private void setSpinner(){
        Spinner spinnerBuilding = binding.spinnerBuilding;
        ArrayAdapter<CharSequence> adapterBuilding = ArrayAdapter.
                createFromResource(requireContext(),
                        R.array.floors_array,
                        android.R.layout.simple_spinner_item
                );
        adapterBuilding.setDropDownViewResource(R.layout.item_spinner);
        spinnerBuilding.setAdapter(adapterBuilding);

    }

}