package com.example.mygoReaction.model.form;

import com.example.mygoReaction.entity.SavedLineEntity;
import com.example.mygoReaction.model.dto.SavedLineDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class GetSavedLineForm extends GenericForm{
    List<SavedLineDto> savedLineEntityResp;

    public GetSavedLineForm() {
    }

    public GetSavedLineForm(Integer code, String message) {
        super(code, message);
    }

    public GetSavedLineForm(Integer code, String message, List<SavedLineDto> savedLineEntityResp) {
        super(code, message);
        this.savedLineEntityResp = savedLineEntityResp;
    }
}
