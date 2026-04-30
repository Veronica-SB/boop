package com.boop.app.data.model;

import com.google.firebase.firestore.PropertyName;
import java.util.Date;

public class Vaccine {

    private String id;

    @PropertyName("petId")
    private String petId;

    @PropertyName("name")
    private String name;

    @PropertyName("administeredDate")
    private Date administeredDate;

    @PropertyName("nextDoseDate")
    private Date nextDoseDate;

    @PropertyName("status")
    private String status; // UP_TO_DATE / UPCOMING / EXPIRED

    public Vaccine() {}

    public String getId()                           { return id; }
    public void setId(String id)                    { this.id = id; }

    @PropertyName("petId")
    public String getPetId()                        { return petId; }
    @PropertyName("petId")
    public void setPetId(String petId)              { this.petId = petId; }

    @PropertyName("name")
    public String getName()                         { return name; }
    @PropertyName("name")
    public void setName(String name)                { this.name = name; }

    @PropertyName("administeredDate")
    public Date getAdministeredDate()               { return administeredDate; }
    @PropertyName("administeredDate")
    public void setAdministeredDate(Date date)      { this.administeredDate = date; }

    @PropertyName("nextDoseDate")
    public Date getNextDoseDate()                   { return nextDoseDate; }
    @PropertyName("nextDoseDate")
    public void setNextDoseDate(Date date)          { this.nextDoseDate = date; }

    @PropertyName("status")
    public String getStatus()                       { return status; }
    @PropertyName("status")
    public void setStatus(String status)            { this.status = status; }

    @PropertyName("createdBy")
    private String createdBy;

    @PropertyName("createdBy")
    public String getCreatedBy() { return createdBy; }
    @PropertyName("createdBy")
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public enum VaccineStatus {
        UP_TO_DATE("Al día"),
        UPCOMING("Próxima"),
        EXPIRED("Vencida");

        private final String displayName;
        VaccineStatus(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }
}
