package com.boop.app.data.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * BOOP — Modelo de datos: Mascota (Java)
 * Corresponde a la pantalla "Crear perfil de mascota"
 */
public class Pet {

    private String id;
    private String name;
    private PetSpecies species;
    private String breed;
    private PetSex sex;
    private Date birthDate;
    private Float weightKg;
    private String chipNumber;
    private String allergies;
    private String vetClinic;
    private String photoUrl;
    private String licenseNumber;
    private String ownerId;
    private List<String> caregiverIds;
    private List<MedicalRecord> medicalHistory;

    // Constructor vacío requerido por Firebase
    public Pet() {
        this.caregiverIds = new ArrayList<>();
        this.medicalHistory = new ArrayList<>();
        this.species = PetSpecies.OTHER;
        this.sex = PetSex.UNKNOWN;
    }

    public Pet(String id, String name, PetSpecies species) {
        this();
        this.id = id;
        this.name = name;
        this.species = species;
    }

    // ── Getters y Setters ──

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public PetSpecies getSpecies() { return species; }
    public void setSpecies(PetSpecies species) { this.species = species; }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    public PetSex getSex() { return sex; }
    public void setSex(PetSex sex) { this.sex = sex; }

    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }

    public Float getWeightKg() { return weightKg; }
    public void setWeightKg(Float weightKg) { this.weightKg = weightKg; }

    public String getChipNumber() { return chipNumber; }
    public void setChipNumber(String chipNumber) { this.chipNumber = chipNumber; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public String getVetClinic() { return vetClinic; }
    public void setVetClinic(String vetClinic) { this.vetClinic = vetClinic; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public List<String> getCaregiverIds() { return caregiverIds; }
    public void setCaregiverIds(List<String> caregiverIds) { this.caregiverIds = caregiverIds; }

    public List<MedicalRecord> getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(List<MedicalRecord> medicalHistory) { this.medicalHistory = medicalHistory; }


    // ── Enums ──

    public enum PetSpecies {
        CAT("Gato"),
        DOG("Perro"),
        RABBIT("Conejo"),
        BIRD("Ave"),
        OTHER("Otro");

        private final String displayName;
        PetSpecies(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum PetSex {
        MALE("Macho"),
        FEMALE("Hembra"),
        UNKNOWN("No especificado");

        private final String displayName;
        PetSex(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }
}
