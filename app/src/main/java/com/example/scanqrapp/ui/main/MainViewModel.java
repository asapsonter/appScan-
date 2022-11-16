package com.example.scanqrapp.ui.main;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class MainViewModel extends ViewModel {
    // TODO: Implement the

    ArrayList<Integer> zoneList = new ArrayList<>();

    int building = 1, floor = 1;

    public MainViewModel(){
        zoneList.add(1);
        zoneList.add(2);
        zoneList.add(3);
        zoneList.add(4);
        zoneList.add(5);
        zoneList.add(6);
        zoneList.add(7);
        zoneList.add(8);
        zoneList.add(9);


    }

    public void incrementZoneList(){
        zoneList.add(zoneList.size() + 1);
        zoneList.add(zoneList.size() + 1);
        zoneList.add(zoneList.size() + 1);
    }

}