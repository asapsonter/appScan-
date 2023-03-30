package com.example.scanqrapp.ui.main;

import static android.R.layout.simple_spinner_item;
import static android.content.Context.MODE_PRIVATE;
import static com.example.scanqrapp.R.id.langChange;
import static com.example.scanqrapp.R.id.sign_outBtn;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.scanqrapp.LoginActivity;
import com.example.scanqrapp.R;
import com.example.scanqrapp.databinding.FragmentMainBinding;
import com.example.scanqrapp.models.SingleScannedRow;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;

public  class MainFragment extends Fragment implements MainFragmentCallbacks {

    private MainViewModel mViewModel;
    private FragmentMainBinding binding;
    ZoneAdapter zoneAdapter;
    private  ArrayList<SingleScannedRow> excelRowArrayList = new ArrayList<>();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private  static final String TAG = "ADD_PDF_TAG";




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
       super.onViewCreated(view, savedInstanceState);
     // requireActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
       mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

       //add set spinner method to view created
       setSpinner();
       //add zone recycler view method
       setZoneRecyclerView();
       loadLocale();
       //remember set as local variable


             // user.getUid();
       //save data to local storage
       binding.ivQr.setOnClickListener(view1 -> {
           //initiate alertDialog
           new AlertDialog.Builder(requireContext())
                   .setTitle("Save")
                   .setMessage("Data has been saved in to local storage of the phone")
                   .setPositiveButton("OK",(dialog, whichButton) -> {
                       // TODO: send or do something with data

                       try {
                           uploadDataToStorage();
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                       //sendFile();


                   } ).show();
       });


       //implement hold and delete feature
       binding.ivDelete.setOnClickListener(view1 -> new AlertDialog.Builder(requireContext())
               .setTitle("Reset App")
               .setMessage("DO YOU WANT TO RESET")
               .setIcon(android.R.drawable.ic_dialog_alert)
               .setPositiveButton(android.R.string.yes, ((dialog, whichButton)  -> {
                   File[] fileArray = requireContext().getExternalFilesDir(null).listFiles();
                   if (fileArray != null)
                       for (File file : fileArray){
                           file.delete();
                       }
                   SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
                   sharedPreferences.edit().clear().apply();
                   excelRowArrayList = new ArrayList<>();
                   setZoneRecyclerView();
                   Toast.makeText(requireContext(), "App reset completed.", Toast.LENGTH_LONG).show();

               } ))
               .setNegativeButton(android.R.string.no, null).show());
       /// settings menu listener //////////////
       binding.settingsBtn.setOnClickListener(this::showPopup);

   }

   @SuppressLint("NonConstantResourceId")
   public  void showPopup(View v){
       PopupMenu popupmenu = new PopupMenu(requireContext(), v);
       MenuInflater inflater = popupmenu.getMenuInflater();
       inflater.inflate(R.menu.settings_menu,popupmenu.getMenu());
       popupmenu.show();

       popupmenu.setOnMenuItemClickListener(menuItem -> {

           switch (menuItem.getItemId()) {
               case sign_outBtn:
                   Logout();
                   break;
               case langChange:
                   changeLang();
                   break;


           }
           return false;
       });

   }

    private void Logout() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        startActivity(new Intent(getContext(), LoginActivity.class ));
    }

    /* upload to firebase function
        ***************************************************************************************************************
         */
        private void uploadDataToStorage() throws IOException {


            String storagePath = "DeviceInfo/" + UUID.randomUUID() + ".xls"; // set path name to generate random UUID ref

            Log.d(TAG, "sendTocloud: entering method");
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(storagePath).child(String.valueOf(user)); //storage ref

            /*
            String externalDirPath = Environment.getExternalStorageDirectory().toString()+"/files";
            Log.d(TAG, "uploadDataToStorage: "+externalDirPath);
            File directory = new File(externalDirPath);
            File[] files = directory.listFiles();
            Log.d(TAG, "uploadDataToStorage: size" +files.length);

            for (int i = 0; i < files.length; i++ ) {
                Log.d(TAG, "uploadDataToStorage: filesName" + files[i].getName());

                storageReference.putFile(Uri.parse(Arrays.toString(files))).addOnSuccessListener(taskSnapshot -> {
                    Log.d(TAG, "onSuccess: up successful");
                }).addOnFailureListener(e -> {
                            Log.d(TAG, "onFailure: file failed due to " + e.getMessage());

                            Toast
.makeText(getActivity(), "Failed to send, Something went wrong", Toast.LENGTH_LONG)
                            .show();
                });
                Toast.makeText(getActivity(), "Sent", Toast.LENGTH_SHORT).show();

            }*/

               // String target = "building1.xls";
             File file = new File(String.valueOf(requireContext().getExternalFilesDir("/building1.xls"))); //file path
            //final File file = new File(String.valueOf(requireContext().getExternalFilesDir(null)), target);
            Log.d(TAG, "uploadDataToStorage: "+file);

            storageReference.putFile(Uri.fromFile(file)).addOnSuccessListener(taskSnapshot -> {
                Log.d(TAG, "onSuccess: up successful");
                Toast.makeText(getActivity(),"Sent", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Log.d(TAG, "onFailure: file failed due to " + e.getMessage());

                Toast.makeText(getActivity(), "Failed to send, Something went wrong", Toast.LENGTH_LONG)
                        .show();
            });


        }

    /*
    language switch function
     */
    private void changeLang(){
        final String[] languages = {"English", "中文"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        mBuilder.setTitle(R.string.lang);


        mBuilder.setSingleChoiceItems(languages, -1, (dialog, which) -> {
            if (which == 0) {
                setLocale("");
                requireActivity().recreate();
            }
            else if (which == 1){
                setLocale("zh");
                requireActivity().recreate();
            }
        });
        // create and show dialog
        mBuilder.create();
        mBuilder.show();
    }
    // //////////////get language strings
    private void setLocale(String language){
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        /// config the Base activity around app
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        requireActivity().getBaseContext().getResources().updateConfiguration(configuration, requireActivity().
                getBaseContext().getResources().getDisplayMetrics());

        //initiate shared pref ///////
        SharedPreferences.Editor editor = requireActivity().getSharedPreferences("settings", MODE_PRIVATE).edit();
        editor.putString("app_lang", language);
        editor.apply();
    }
    private void loadLocale() {
        SharedPreferences preferences = requireActivity().getSharedPreferences("settings", MODE_PRIVATE);
        String language = preferences.getString("app_lang", "");
        setLocale(language);
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
            //set navigation to zonedetails fragment
            Navigation.findNavController(
                    requireActivity(),
                    binding.getRoot().getId()
            ).navigate(R.id.zoneDetailFragment, bundle);
            requireActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }



    }

    private void updateTotalZone() {
       SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
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
                        simple_spinner_item
                );

        //initiate  building spinner
        adapterBuilding.setDropDownViewResource(R.layout.item_spinner);
        spinnerBuilding.setAdapter(adapterBuilding);
        spinnerBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mViewModel.building = Integer.parseInt((String) adapterView.getSelectedItem());
                readExcelFile("building" + mViewModel.building + ".xls");
                setZoneRecyclerView();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        //initialize building spinner instance
        Spinner spinnerFloor = binding.spinnerFloor;
        //initialize floor arrays
        ArrayAdapter<CharSequence> adapterFloor = ArrayAdapter
                .createFromResource(
                        requireContext(),
                        R.array.floors_array,
                        simple_spinner_item
                );
        //initiate floor spinner
        adapterFloor.setDropDownViewResource(R.layout.item_spinner);
        spinnerFloor.setAdapter(adapterFloor);
        spinnerFloor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mViewModel.floor = Integer.parseInt((String) adapterView.getSelectedItem());
                setZoneRecyclerView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




    }
/// data to external dir
    private void readExcelFile(String fileName) {
        File file = new File(requireContext().getExternalFilesDir(null), fileName);

        FileInputStream fileInputStream = null;
        excelRowArrayList = new ArrayList<>();

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
    private void setZoneRecyclerView() {
        SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        int zone =  sharedPreferences.getInt("zone", 9);
        mViewModel.initializeZoneList(zone);
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
