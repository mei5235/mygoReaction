package com.example.mygoReaction.model.form;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class GenericForm {
    Integer code = 0;
    String message = "";

    public GenericForm(Integer code) {
        this.code = code;
    }

    public GenericForm(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
