package com.qboxus.gograbdriver.models;

import java.io.Serializable;

public class PublishDocumentModel implements Serializable {
    String name, encodedStr;

    public PublishDocumentModel() {
    }

    public String getEncodedStr() {
        return encodedStr;
    }

    public void setEncodedStr(String encodedStr) {
        this.encodedStr = encodedStr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
