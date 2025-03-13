package com.example.mygoReaction.model.form;

import lombok.Getter;
import lombok.Setter;

import java.net.URL;

@Getter
@Setter
public class HeheForm extends GenericForm{
    String path;

    public HeheForm() {
    }

    public HeheForm(Integer code, String message) {
        super(code, message);
    }

    public HeheForm(Integer code, String message, String path) {
        super(code, message);
        this.path = path;
    }
}
