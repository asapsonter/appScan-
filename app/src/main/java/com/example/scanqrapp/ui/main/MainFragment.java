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
import android.os.Environment;
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

public  class MainFragment extends Fragment implements MainFragmentCallbacks {

    private MainViewModel mViewModel;
    private FragmentMainBinding binding;
    ZoneAdapter zoneAdapter;
    private  ArrayList<SingleScannedRow> excelRowArrayList = new ArrayList<>();
    private FragmentTransaction fragmentTransaction;

    private static final int FILE_PICK_CODE = 1000;

    //uri path
   //private final Uri fileUri = Uri.parse("Internal storage/Android/data/com.example.scanqrapp/files/Building1.xls");
    //private Uri fileUri = null;
private static String filePath = "/data/******/files/";

    //debug tag
    private  static final String TAG = "ADD_PDF_TAG";
    //create firebase reference
    FirebaseStorage storage = FirebaseStorage.getInstance();
    //StorageReference storageRef = storage.getReference();

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




       //save data to local storage
       binding.ivQr.setOnClickListener(view1 -> {
           //initiate alertDiolog
           new AlertDialog.Builder(requireContext())
                   .setTitle("Save")
                   .setMessage("Data has been saved in to local storage of the phone")
                   .setPositiveButton("OK",(dialog, whichButton) -> {
                       // TODO: send or do something with data

                   uploadDataToStorage();
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
   /* private void sendFile() {
        Log.d(TAG, "sendFileIntent");
        Intent intent = new Intent();
        intent.setType("com.example.scanqrapp/files");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select file"), FILE_PICK_CODE);
    }
    public void onActivityResult(int requestCode, int resultCode, @com.google.firebase.database.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode == RESULT_OK){
            if (requestCode == FILE_PICK_CODE){
                Log.d(TAG, "onActivityResult: FILE picked");

                fileUri = data.getData();

                Log.d(TAG, "onActivityResult: Uri" + fileUri );
            }
        }
        else {
            Log.d(TAG, "onActivityResult: cancelled picking file");
            Toast.makeText(getActivity(), "cancelled", Toast.LENGTH_SHORT).show();
        }


    }*/
   //upload data to storage
   private void uploadDataToStorage(){
       StorageReference storageRef = storage.getReference();
       /*Log.d(TAG, "uploadDataToStorage: ");
       Log.e(TAG, "uploadDataToStorage: error");
       Uri file = Uri. fromFile(new File().getPath()) //("This PC/Galaxy A13 5G/Internal storage/Android/data/com.example.scanqrapp/files"));
       Log.d(TAG, "uploadDataToStorage: ");*/
       //"Internal storage/Android/data/com.example.scanqrapp/files"
//       This PC\Galaxy A13 5G\Internal storage\DCIM\Screenshots
      // Uri file = Uri.fromFile(new File(String.valueOf(requireContext().getFilesDir())));
       //file.getPath();

       File file = new File(Environment.getDataDirectory(),"*****/files");

       Uri uri = Uri.fromFile(file);
       File newFile = new File(uri.getPath());

       StorageReference excelFiles = storageRef.child("files"); // try get full path
       excelFiles.putFile(Uri.fromFile(newFile));

       /*Log.d(TAG, "uploadDataToStorage: uploading data");
       StorageReference storageRef = storage.getReference(); //maybe add path inside the ref

       StorageReference excelFiles = storageRef.child("files/building1.xls");

       StorageReference pathToExcelFiles = storageRef.child("files/" + fileUri.getLastPathSegment());

       excelFiles.getName().equals(pathToExcelFiles.getName());

       excelFiles.getPath().equals(pathToExcelFiles.getPath());

       excelFiles.putFile(fileUri);
*/


       /*//time stamp
       long timestamp = System.currentTimeMillis();
       //path of file in firebase storage
       String filePathAndname = "excelFiles/" +timestamp;
       StorageReference storageRef = storage.getReference();
       //This PC\Galaxy A13 5G\Internal storage\Android\data\com.example.scanqrapp\files *** path for reference
       //This PC\Galaxy A13 5G\Internal storage\Pictures\.thumbnails
       Uri file = Uri.fromFile(new File("/Pictures/thumbnails/32.jpg" ));
       StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndname);

       StorageReference excelFiles = storageRef.child("32.jpg/" +file.getLastPathSegment());
       //uploadTask = excelFiles.putFile(file);
       excelFiles.putFile(file)
               .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       Log.d(TAG, "onSuccess: file uploaded succesfully");
                       Toast.makeText(getActivity(),"data has been saved ", Toast.LENGTH_LONG).show();

                       Task<Uri> uriTask =taskSnapshot.getStorage().getDownloadUrl();
                       while (!uriTask.isSuccessful());
                       String uploadedFileUrl = "" +uriTask.getResult();

                   }
               })
               .addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       // add progress dialog dismiss

                       Log.d(TAG, "onFailure: file failed due to " + e.getMessage());
                       Toast.makeText(getActivity(), "failed to upload" +e.getMessage(), Toast.LENGTH_SHORT).show();
                   }
               });*/
       /*long timestamp = System.currentTimeMillis();

       //path to file firebase storage
       String filePathAndName = "files" + timestamp;
       //storage reference

       StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
       storageReference.putFile(fileUri)
               .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       Log.d(TAG, "onSuccess: file uploaded succesfully");

                       Task<Uri> uriTask =taskSnapshot.getStorage().getDownloadUrl();
                       while (!uriTask.isSuccessful());
                       String uploadedFileUrl = "" +uriTask.getResult();
                       uploadDataToStorageDB(uploadedFileUrl, timestamp);
                   }
               })
               .addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       // add progress dialog dismiss

                       Log.d(TAG, "onFailure: file failed due to " + e.getMessage());
                       Toast.makeText(getActivity(), "failed to upload" +e.getMessage(), Toast.LENGTH_SHORT).show();
                   }
               });
*/
   }
  /* private void uploadDataToStorageDB(String uploadDataToStorage, long timestamp){
       DatabaseReference ref = FirebaseDatabase.getInstance().getReference("files");
       ref.child(""+timestamp );
   }*/
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

    //// inis
//    Fragment fragment = new FragmentB();
//    getSupportFragmentManager().beginTransaction()
//    .setCustomAnimations(
//            R.anim.slide_in,  // enter
//            R.anim.fade_out,  // exit
//            R.anim.fade_in,   // popEnter
//            R.anim.slide_out  // popExit
//    )
//    .replace(R.id.fragment_container, fragment)
//    .addToBackStack(null)
//    .commit();



//    public void slideAnimation(){
//        MainFragment mainFragment = new MainFragment();
//        getSupportFragmentManager(AppCompatActivity).beginTransaction().setCustomAnimations(R.anim.in_from_left)
//                .replace(R.id.mainFragment, mainFragment)
//                .addToBackStack(null)
//                .commit();
//
//
//    }

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
