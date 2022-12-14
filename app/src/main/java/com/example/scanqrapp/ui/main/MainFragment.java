package com.example.scanqrapp.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.scanqrapp.models.SingleScannedRow;

import java.util.ArrayList;

public class MainFragment extends Fragment implements  MainFragmentsCallbacks  {

    private MainViewModel mViewModel;
    private FragmentMainBinding binding;
    ZoneAdapter zoneAdapter;
    private final ArrayList<SingleScannedRow> excelRowArrayList = new ArrayList<>();




   @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
       super.onViewCreated(view, savedInstanceState);
       mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

       //add set spinner method to view created
       setSpinner();
       //add zone recycler view method
       setZoneRecyclerView();
   }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onZoneClick(int i) {
       // set view model  to set and increament positon of zone adapter
        if (i == mViewModel.zoneList.size()){
            mViewModel.incrementZoneList();
            updateTotalZone();
            zoneAdapter.notifyDataSetChanged();
        } else if ( i < mViewModel.zoneList.size()){
            Bundle bundle = new Bundle();
            bundle.putInt("zone", mViewModel.zoneList.get(i));
            bundle.putInt("building", Integer.parseInt(binding.spinnerBuilding.getSelectedItem().toString()));
            bundle.putInt("floor", Integer.parseInt(binding.spinnerFloor.getSelectedItem().toString()));
            Navigation.findNavController(
                    requireActivity(),
                    binding.getRoot().getId()
            ).navigate(R.id.zoneDetailFragment, bundle);
        }



    }

    private void updateTotalZone() {
       SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
       int zone = sharedPreferences.getInt("zone", 9);
       SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("zone", zone + 3);
        editor.apply();
    }

    //set spinners building & floor
    private void setSpinner(){
       //initialize building spinner
        Spinner spinnerBuilding = binding.spinnerBuilding;
        ArrayAdapter<CharSequence> adapterBuilding = ArrayAdapter
                .createFromResource(
                        requireContext(),
                        R.array.floors_array,
                        android.R.layout.simple_spinner_item
                );

        //invoke building spinner
        adapterBuilding.setDropDownViewResource(R.layout.item_spinner);
        spinnerBuilding.setAdapter(adapterBuilding);


        //initialize building spinner instance
        Spinner spinnerFloor = binding.spinnerFloor;
        //initialize floor arrays
        ArrayAdapter<CharSequence> adapterFloor = ArrayAdapter
                .createFromResource(
                        requireContext(),
                        R.array.floors_array,
                        android.R.layout.simple_spinner_item
                );
        //initiate floor spinner
        adapterFloor.setDropDownViewResource(R.layout.item_spinner);
        spinnerFloor.setAdapter(adapterFloor);



    }
    private void setZoneRecyclerView() {
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        int zone =  sharedPreferences.getInt("zone", 9);
        mViewModel.incrementZoneList();
        zoneAdapter = new ZoneAdapter(
                mViewModel.zoneList,
                requireContext(),
                mViewModel.building,
                mViewModel.floor,
                excelRowArrayList,
                this
        );


        //binding recycler view ud instance to instantiate zoneAdapter data

        binding.rvZones.setAdapter(zoneAdapter);
        binding.rvZones.setLayoutManager(new GridLayoutManager(requireContext(), 3));



    }
}
