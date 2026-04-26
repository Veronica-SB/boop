package com.boop.app.data.model;

import com.google.firebase.firestore.PropertyName;
import java.util.Date;

/**
 * BOOP — Modelo de datos: Registro médico
 *
 * Representa un documento en la subcolección "mascotas/{petId}/historialMedico/{registroId}".
 *
 * Tanto el OWNER como los futuros roles con permiso pueden añadir registros.
 * El campo createdBy indica quién añadió cada entrada.
 *
 * Subcolección Firestore: mascotas/{petId}/historialMedico/{registroId}
 */
public class MedicalRecord {

    // ── Identificadores ──────────────────────────────────────────────────────

    // ID del documento (no se guarda en Firestore, se asigna al leer)
    private String id;

    // ID de la mascota a la que pertenece este registro
    @PropertyName("petId")
    private String petId;

    // ── Datos de la visita ────────────────────────────────────────────────────

    @PropertyName("type")
    private String type; // Usa RecordType.name() para guardar

    @PropertyName("title")
    private String title;

    @PropertyName("date")
    private Date date;

    // Fecha de la próxima visita o dosis (opcional)
    @PropertyName("nextDate")
    private Date nextDate;

    @PropertyName("clinic")
    private String clinic;

    // Nombre del doctor o veterinario que atendió
    @PropertyName("doctor")
    private String doctor;

    // Notas adicionales o descripción de la visita
    @PropertyName("notes")
    private String notes;

    // ── Campos de auditoría ───────────────────────────────────────────────────

    // UID del usuario que creó este registro
    @PropertyName("createdBy")
    private String createdBy;

    // ── Constructor vacío requerido por Firebase ──────────────────────────────

    public MedicalRecord() {
        this.type = RecordType.OTHER.name();
        this.date = new Date();
    }

    // ── Getters y Setters ─────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @PropertyName("petId")
    public String getPetId() { return petId; }
    @PropertyName("petId")
    public void setPetId(String petId) { this.petId = petId; }

    @PropertyName("type")
    public String getType() { return type; }
    @PropertyName("type")
    public void setType(String type) { this.type = type; }

    @PropertyName("title")
    public String getTitle() { return title; }
    @PropertyName("title")
    public void setTitle(String title) { this.title = title; }

    @PropertyName("date")
    public Date getDate() { return date; }
    @PropertyName("date")
    public void setDate(Date date) { this.date = date; }

    @PropertyName("nextDate")
    public Date getNextDate() { return nextDate; }
    @PropertyName("nextDate")
    public void setNextDate(Date nextDate) { this.nextDate = nextDate; }

    @PropertyName("clinic")
    public String getClinic() { return clinic; }
    @PropertyName("clinic")
    public void setClinic(String clinic) { this.clinic = clinic; }

    @PropertyName("doctor")
    public String getDoctor() { return doctor; }
    @PropertyName("doctor")
    public void setDoctor(String doctor) { this.doctor = doctor; }

    @PropertyName("notes")
    public String getNotes() { return notes; }
    @PropertyName("notes")
    public void setNotes(String notes) { this.notes = notes; }

    @PropertyName("createdBy")
    public String getCreatedBy() { return createdBy; }
    @PropertyName("createdBy")
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    // ── Enum ──────────────────────────────────────────────────────────────────

    /**
     * Tipos de registro médico disponibles.
     * Se guarda en Firestore como String usando .name() (ej: "VACCINE").
     */
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