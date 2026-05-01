package com.boop.app.data.model;

import com.google.firebase.firestore.PropertyName;
import java.util.Date;

/**
 * BOOP — Modelo de datos: Mascota
 *
 * Representa un documento en la colección raíz "pets/{petId}".
 *
 * IMPORTANTE: Este modelo solo contiene los campos básicos del documento.
 * Los cuidadores, historial médico, vacunas y recordatorios son subcolecciones
 * independientes y se cargan por separado cuando se necesitan.
 *
 * Colección Firestore: pets/{petId}
 */
public class Pet {

    // ── Identificadores ──────────────────────────────────────────────────────

    // El ID no se guarda como campo en Firestore (es el ID del documento),
    // pero lo almacenamos aquí para tenerlo disponible en la app
    private String id;

    // UID del usuario propietario (Firebase Auth)
    @PropertyName("ownerId")
    private String ownerId;

    // ── Datos básicos ─────────────────────────────────────────────────────────

    @PropertyName("name")
    private String name;

    @PropertyName("species")
    private String species; // Usa PetSpecies.name() para guardar, PetSpecies.valueOf() para leer

    @PropertyName("breed")
    private String breed;

    @PropertyName("sex")
    private String sex; // Usa PetSex.name() para guardar

    @PropertyName("birthDate")
    private Date birthDate;

    @PropertyName("weightKg")
    private Float weightKg;

    // ── Datos legales y médicos ───────────────────────────────────────────────

    // Obligatorio para CAT y DOG, opcional para RABBIT y BIRD
    @PropertyName("chipNumber")
    private String chipNumber;

    @PropertyName("licenseNumber")
    private String licenseNumber;

    @PropertyName("allergies")
    private String allergies;

    @PropertyName("vetClinic")
    private String vetClinic;

    // ── Media ─────────────────────────────────────────────────────────────────

    // URL de la foto almacenada en Firebase Storage
    @PropertyName("photoUrl")
    private String photoUrl;

    // ── Constructor vacío requerido por Firebase ──────────────────────────────

    public Pet() {}

    // ── Getters y Setters ─────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @PropertyName("ownerId")
    public String getOwnerId() { return ownerId; }
    @PropertyName("ownerId")
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    @PropertyName("name")
    public String getName() { return name; }
    @PropertyName("name")
    public void setName(String name) { this.name = name; }

    @PropertyName("species")
    public String getSpecies() { return species; }
    @PropertyName("species")
    public void setSpecies(String species) { this.species = species; }

    @PropertyName("breed")
    public String getBreed() { return breed; }
    @PropertyName("breed")
    public void setBreed(String breed) { this.breed = breed; }

    @PropertyName("sex")
    public String getSex() { return sex; }
    @PropertyName("sex")
    public void setSex(String sex) { this.sex = sex; }

    @PropertyName("birthDate")
    public Date getBirthDate() { return birthDate; }
    @PropertyName("birthDate")
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }

    @PropertyName("weightKg")
    public Float getWeightKg() { return weightKg; }
    @PropertyName("weightKg")
    public void setWeightKg(Float weightKg) { this.weightKg = weightKg; }

    @PropertyName("chipNumber")
    public String getChipNumber() { return chipNumber; }
    @PropertyName("chipNumber")
    public void setChipNumber(String chipNumber) { this.chipNumber = chipNumber; }

    @PropertyName("licenseNumber")
    public String getLicenseNumber() { return licenseNumber; }
    @PropertyName("licenseNumber")
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    @PropertyName("allergies")
    public String getAllergies() { return allergies; }
    @PropertyName("allergies")
    public void setAllergies(String allergies) { this.allergies = allergies; }

    @PropertyName("vetClinic")
    public String getVetClinic() { return vetClinic; }
    @PropertyName("vetClinic")
    public void setVetClinic(String vetClinic) { this.vetClinic = vetClinic; }

    @PropertyName("photoUrl")
    public String getPhotoUrl() { return photoUrl; }
    @PropertyName("photoUrl")
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    // ── Enums ─────────────────────────────────────────────────────────────────

    /**
     * Especies disponibles en la app.
     * Se guarda en Firestore como String usando .name() (ej: "CAT", "DOG").
     * Se recupera con PetSpecies.valueOf(string).
     */
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

    /**
     * Sexo de la mascota.
     * Se guarda en Firestore como String usando .name() (ej: "MALE").
     */
    public enum PetSex {
        MALE("Macho"),
        FEMALE("Hembra"),
        UNKNOWN("No especificado");

        private final String displayName;
        PetSex(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }
}
