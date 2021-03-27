package com.inkneko.nekorecord.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(tableName = "daily_record")
public class DailyRecord {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Integer id;

    @ColumnInfo(name = "event")
    private String event;


    @ColumnInfo(name = "price")
    private Float price;

    @ColumnInfo(name = "event_type")
    private String eventType;

    @ColumnInfo(name = "timestamp")
    private Long timestamp;

    public DailyRecord(String event, Float price, String eventType, Long timestamp) {
        this.event = event;
        this.price = price;
        this.eventType = eventType;
        this.timestamp = timestamp;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

}
