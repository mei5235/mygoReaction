package com.example.mygoReaction.model.resp;

import com.example.mygoReaction.entity.SavedSeriesEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetAllSavedSeriesResp extends GenericResp{
    List<SavedSeriesEntity> savedSeries;

    public GetAllSavedSeriesResp(){    }

    public GetAllSavedSeriesResp(Integer code, String message) {
        super(code, message);
    }

    public GetAllSavedSeriesResp(Integer code, String message, List<SavedSeriesEntity> savedSeries) {
        super(code, message);
        this.savedSeries = savedSeries;
    }
}
