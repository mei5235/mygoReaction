package com.example.mygoReaction.model.resp;

import com.example.mygoReaction.model.dto.SavedLineDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class GetSavedLineResp extends GenericResp {
    List<SavedLineDto> savedLineEntityResp;

    public GetSavedLineResp() {
    }

    public GetSavedLineResp(Integer code, String message) {
        super(code, message);
    }

    public GetSavedLineResp(Integer code, String message, List<SavedLineDto> savedLineEntityResp) {
        super(code, message);
        this.savedLineEntityResp = savedLineEntityResp;
    }
}
