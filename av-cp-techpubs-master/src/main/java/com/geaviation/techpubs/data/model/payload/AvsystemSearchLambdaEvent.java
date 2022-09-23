package com.geaviation.techpubs.data.model.payload;

public class AvsystemSearchLambdaEvent {
    private String id;

    public AvsystemSearchLambdaEvent() {
    }

    public AvsystemSearchLambdaEvent(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
