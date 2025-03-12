package com.example.mygoReaction.model.form;

import java.net.URL;

public class HeheForm extends GenericForm{
    URL path;

    public HeheForm() {
    }

    public HeheForm(Integer code, String message) {
        super(code, message);
    }

    public HeheForm(Integer code, String message, URL path) {
        super(code, message);
        this.path = path;
    }
}
