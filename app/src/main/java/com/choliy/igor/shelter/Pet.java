package com.choliy.igor.shelter;

public class Pet {

    private String mName;
    private String mBreed;
    private int mGender;
    private int mAge;
    private int mWeight;
    private boolean mBites;
    private boolean mSick;

    public Pet(String name, String breed, int gender, int age, int weight, boolean bites, boolean sick) {
        setName(name);
        setBreed(breed);
        setGender(gender);
        setAge(age);
        setWeight(weight);
        setBites(bites);
        setSick(sick);
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getBreed() {
        return mBreed;
    }

    public void setBreed(String breed) {
        mBreed = breed;
    }

    public int getGender() {
        return mGender;
    }

    public void setGender(int gender) {
        mGender = gender;
    }

    public int getAge() {
        return mAge;
    }

    public void setAge(int age) {
        mAge = age;
    }

    public int getWeight() {
        return mWeight;
    }

    public void setWeight(int weight) {
        mWeight = weight;
    }

    public boolean isBites() {
        return mBites;
    }

    public void setBites(boolean bites) {
        mBites = bites;
    }

    public boolean isSick() {
        return mSick;
    }

    public void setSick(boolean sick) {
        mSick = sick;
    }
}