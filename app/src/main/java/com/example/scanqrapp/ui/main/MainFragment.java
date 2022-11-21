package com.example.scanqrapp.ui.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.scanqrapp.R;
import com.example.scanqrapp.databinding.FragmentMainBinding;

public class MainFragment extends Fragment implements MainFragmentCallbacks {
    //set mainFragment view model identifier
    private MainViewModel mViewModel;
    private FragmentMainBinding binding;

    //set zone adapter identify
    ZoneAdapter zoneAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        //call zone Recycler View
        setZoneRecyclerView();
        setSpinner();

    }

    private void setZoneRecyclerView() {
        zoneAdapter = new ZoneAdapter(
                mViewModel.zoneList,
                requireContext(),
                mViewModel.building,
                mViewModel.floor, this

                //call the callback from mainFragmentCallbacks with "this"
        );
        // set recycler view to zoneAdapter.
        binding.rvZones.setAdapter(zoneAdapter);
        //setting new layout view and span count
        binding.rvZones.setLayoutManager(new GridLayoutManager(requireContext(), 3
        ));
    }

    //Initiate on zone click action from mainFragmentsCallbacks
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onZoneClick(int i) {
        if (i == mViewModel.zoneList.size()) {
            mViewModel.incrementZoneList();
            //notifies the data has changed. view should refresh itself
            zoneAdapter.notifyDataSetChanged();
        } else if (i < mViewModel.zoneList.size()) {
            Bundle bundle = new Bundle();
            bundle.putInt("zone", mViewModel.zoneList.get(i));
            bundle.putInt("building", Integer.parseInt(binding.spinnerBuilding.getSelectedItem().toString()));
            bundle.putInt("floor", Integer.parseInt(binding.spinnerFloor.getSelectedItem().toString()));

            Navigation.findNavController(
                    requireActivity(),
                    binding.getRoot().getId()
            ).navigate(R.id.zone_detail_fragment, bundle);

        }
    }

    private void setSpinner() {
        Spinner spinnerBuilding = binding.spinnerBuilding;
        ArrayAdapter<CharSequence> adapterBuilding = ArrayAdapter.
                createFromResource(requireContext(),
                        R.array.floors_array,
                        android.R.layout.simple_spinner_item
                );
        adapterBuilding.setDropDownViewResource(R.layout.item_spinner);
        spinnerBuilding.setAdapter(adapterBuilding);
        spinnerBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long id) {
                mViewModel.building = Integer.parseInt((String) adapterView.getSelectedItem());
                setZoneRecyclerView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner spinnerFloor = binding.spinnerFloor;
        ArrayAdapter<CharSequence> adapterFloor =
                ArrayAdapter.createFromResource(
                        requireContext(),
                        R.array.floors_array,
                        android.R.layout.simple_spinner_item
                );
        adapterFloor.setDropDownViewResource(R.layout.item_spinner);
        spinnerFloor.setAdapter(adapterFloor);
        spinnerFloor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                mViewModel.floor = Integer.parseInt((String) adapterView.getSelectedItem());
                setZoneRecyclerView();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
}