package com.example.mygoReaction.model.form;

import com.example.mygoReaction.entity.SavedLineEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class GetSavedLineForm extends GenericForm{
    List<SavedLineEntity> savedLineEntityResp;

    public GetSavedLineForm() {
    }

    public GetSavedLineForm(Integer code, String message) {
        super(code, message);
    }

    public GetSavedLineForm(Integer code, String message, List<SavedLineEntity> savedLineEntityResp) {
        super(code, message);
        this.savedLineEntityResp = savedLineEntityResp;
    }
}
