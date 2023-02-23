package com.example.scanqrapp.ui.main;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.scanqrapp.R;
import com.example.scanqrapp.databinding.FragmentMainBinding;
import com.example.scanqrapp.models.SingleScannedRow;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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

public  class MainFragment extends Fragment implements MainFragmentCallbacks {

    private MainViewModel mViewModel;
    private FragmentMainBinding binding;
    ZoneAdapter zoneAdapter;
    private  ArrayList<SingleScannedRow> excelRowArrayList = new ArrayList<>();
    private FragmentTransaction fragmentTransaction;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    private static final int FILE_PICK_CODE = 1000;

    //uri path
   //private final Uri fileUri = Uri.parse("Internal storage/Android/data/com.example.scanqrapp/files/Building1.xls");
    //private Uri fileUri = null;
//private static String filePath = "/data/******/files/";

   // Uri fileUri = Uri.parse(Environment.getDataDirectory()+"/files");
//   Uri fileUri = Uri.fromFile(new File(String.valueOf(getContext().getFilesDir()), "/files"));

    //debug tag
    private  static final String TAG = "ADD_PDF_TAG";
    //create firebase reference
    //FirebaseStorage storage = getInstance();
   // StorageReference storageRef = storage.getReference();

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

       user = FirebaseAuth.getInstance().getCurrentUser();
       reference = FirebaseDatabase.getInstance().getReference("Users");
       userID = user.getUid();


       //save data to local storage
       binding.ivQr.setOnClickListener(view1 -> {
           //initiate alertDiolog
          // sendFle();
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
       binding.ivDelete.setOnClickListener(view1 -> {
           new AlertDialog.Builder(requireContext())
                   .setTitle("Reset App")
                   .setMessage("DO YOU WANT TO RESET")
                   .setIcon(android.R.drawable.ic_dialog_alert)
                   .setPositiveButton(android.R.string.yes, ((dialog, whichButton)  -> {
                       File[] fileArray = requireContext().getExternalFilesDir(null).listFiles();
                       if (fileArray != null)
                           for (File file : fileArray){
                               file.delete();
                           }
                       SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                       sharedPreferences.edit().clear().apply();
                       excelRowArrayList = new ArrayList<SingleScannedRow>();
                       setZoneRecyclerView();
                       Toast.makeText(requireContext(), "App reset completed.", Toast.LENGTH_LONG).show();

                   } ))
                   .setNegativeButton(android.R.string.no, null).show();
       });
       /// language change listner //////////////
       binding.langChange.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               changeLang();
               //Log.d("success", "this button works");
           }
       });

   }

        private void uploadDataToStorage() throws IOException {

            FirebaseStorage storage = FirebaseStorage.getInstance();

            Log.d(TAG, "sendTocloud: entering method");
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("SycraExcelFiles"); //storage ref
            File file = new File(String.valueOf(requireContext().getExternalFilesDir("building1.xls"))); //file path

            storageReference.putFile(Uri.fromFile(file)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "onSuccess: up successful");
                    Toast.makeText(getActivity(),"Sent", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: file failed due to " + e.getMessage());

                    Toast.makeText(getActivity(), "Failed to send, Something went wrong", Toast.LENGTH_LONG)
                            .show();
                }

            });
            // [START upload_create_reference]
            // Create a storage reference from our app
           /* StorageReference storageReference = storage.getReference("parentPath/childPath.xls");
            StorageReference AnotherRef = storage.getReference("antherpath");

            //StorageReference storageReference = FirebaseStorage.getInstance().getReference(); //storage ref
           // StorageReference newref = storageReference.child("testingRef");
            //just testing stuff
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("file/xls")
                    .build();
            StorageReference parentPath = storageReference.child("parentPath.xls/childPath.xls");
            StorageReference childPath = storageReference.child("childPath.xls");


           childPath.getName().equals(parentPath.getName());
           childPath.getPath().equals(parentPath.getPath());

            File file = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                file = new File(String.valueOf(requireContext().getExternalFilesDir(null).getParentFile()),"files");
            }*/
            /*Uri file = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                file = Uri.fromFile(new File(String.valueOf(new File(String.valueOf(requireContext().getDataDir())))));
            }*/
            // URI newpath = null;

            //file = newpath.getPath(getContext().getExternalFilesDir(file.getParent())).
           // storageReference.putFile(Uri.fromFile(new File(String.valueOf(Objects.requireNonNull(file.getParentFile())))));
/*
             storageReference.putFile(Uri.fromFile(file),metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "onSuccess: up successful");
                    Toast.makeText(getActivity(),"Sent", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: file failed due to " + e.getMessage());

                    Toast.makeText(getActivity(), "Failed to send, Something went wrong", Toast.LENGTH_LONG)
                            .show();
                }

            });*/


        }


    private void changeLang(){
        final String languages[] = {"English", "中文"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        mBuilder.setTitle(R.string.lang);


        mBuilder.setSingleChoiceItems(languages, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    setLocale("");
                    getActivity().recreate();
                }
                else if (which == 1){
                    setLocale("zh");
                    getActivity().recreate();
                }
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
        getActivity().getBaseContext().getResources().updateConfiguration(configuration, getActivity().
                getBaseContext().getResources().getDisplayMetrics());

        //initiate shared pref ///////
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("settings", MODE_PRIVATE).edit();
        editor.putString("app_lang", language);
        editor.apply();
    }
    private void loadLocale() {
        SharedPreferences preferences = getActivity().getSharedPreferences("settings", MODE_PRIVATE);
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
                        android.R.layout.simple_spinner_item
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
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
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
    private void SendFiles(){

    }
}
