package com.hahaxueche.model.util;

import java.io.Serializable;

/**
 * Created by gibxin on 2016/2/21.
 */
public class LinksModel implements Serializable {
    private String self;
    private String next;
    private String previous;

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }
}
