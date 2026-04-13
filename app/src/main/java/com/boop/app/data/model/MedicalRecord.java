package com.boop.app.data.model;

import java.util.Date;

/**
 * BOOP — Modelo de datos: Registro médico (Java)
 * Elemento del historial veterinario de una mascota.
 */
public class MedicalRecord {

    private String id;
    private String petId;
    private RecordType type;
    private String title;
    private Date date;
    private Date nextDate;   // Próxima fecha (ej: próxima vacuna)
    private String clinic;
    private String notes;

    // Constructor vacío requerido por Firebase
    public MedicalRecord() {
        this.type = RecordType.OTHER;
        this.date = new Date();
    }

    // ── Getters y Setters ──

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPetId() { return petId; }
    public void setPetId(String petId) { this.petId = petId; }

    public RecordType getType() { return type; }
    public void setType(RecordType type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public Date getNextDate() { return nextDate; }
    public void setNextDate(Date nextDate) { this.nextDate = nextDate; }

    public String getClinic() { return clinic; }
    public void setClinic(String clinic) { this.clinic = clinic; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // ── Enum ──

    public enum RecordType {
        VACCINE("Vacuna"),
        DEWORMING("Desparasitación"),
        MEDICATION("Medicación"),
        VET_VISIT("Visita veterinaria"),
        HYGIENE("Higiene"),
        OTHER("Otro");

        private final String displayName;
        RecordType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }
}
