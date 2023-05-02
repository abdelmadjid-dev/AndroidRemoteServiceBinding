package com.example.sbwaserviceapp;

parcelable CustomHashMap;

interface ITestServiceAIDL {
    int sumAndGetResults(int a, int b);

    CustomHashMap processCustomData(in CustomHashMap data);
}