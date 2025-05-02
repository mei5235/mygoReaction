package com.example.mygoReaction.model.resp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetScreenCapFromVideoResp extends GenericResp {
    String path;

    public GetScreenCapFromVideoResp() {
    }

    public GetScreenCapFromVideoResp(Integer code, String message) {
        super(code, message);
    }

    public GetScreenCapFromVideoResp(Integer code, String message, String path) {
        super(code, message);
        this.path = path;
    }
}
