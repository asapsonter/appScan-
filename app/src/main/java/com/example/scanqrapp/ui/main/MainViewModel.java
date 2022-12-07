package com.example.scanqrapp.ui.main;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class MainViewModel extends ViewModel {
   //state zonelist array instance
    ArrayList<Integer> zoneList = new ArrayList<>();
    int building = 1, floor = 1;

   public void incrementZoneList(){
       zoneList.add(zoneList.size() + 1 );
       zoneList.add(zoneList.size() + 1 );
       zoneList.add(zoneList.size() + 1 );
   }


   public void initializeZoneList(int zoneTotal){

       zoneList = new ArrayList<>(); // new zoneList array instance
   }
}