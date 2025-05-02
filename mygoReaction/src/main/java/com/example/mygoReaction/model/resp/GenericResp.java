package com.example.mygoReaction.model.resp;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class GenericResp {
    Integer code = 0;
    String message = "";

    public GenericResp(Integer code) {
        this.code = code;
    }

    public GenericResp(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
