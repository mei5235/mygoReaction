package com.example.mygoReaction.controller;

import com.example.mygoReaction.model.dto.SearchLineForm;
import com.example.mygoReaction.model.dto.Test2Dto;
import com.example.mygoReaction.model.form.GenericForm;
import com.example.mygoReaction.model.form.GetSavedLineForm;
import com.example.mygoReaction.service.Impl.SavedLineServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("time2")
public class SavedLineController {

    private static final Logger log = LoggerFactory.getLogger(SavedLineController.class);

    private final SavedLineServiceImpl savedLineServiceImpl;
    public SavedLineController(
            SavedLineServiceImpl savedLineServiceImpl
    ) {
        this.savedLineServiceImpl = savedLineServiceImpl;
    }

    @RequestMapping(method = RequestMethod.POST, value="/findBySeriesIdAndKeyword")
    public ResponseEntity<String> findBySeriesIdAndKeyword(@RequestBody Test2Dto t2d){
        return savedLineServiceImpl.findBySeriesIdAndKeyword(t2d);
    }

    @RequestMapping(method = RequestMethod.POST, value="/getSavedLine")
    public ResponseEntity<GenericForm> getSavedLine(@RequestBody SearchLineForm searchLineForm){
        GenericForm form = null;
        try {
            form = savedLineServiceImpl.getSavedLine(searchLineForm);
            log.info("success");
        } catch (Exception e){
            log.error("error");
            log.error(e.getMessage(),e);
        }
        return ResponseEntity.ok(form);
    }

    @RequestMapping(method = RequestMethod.POST, value = "importFromSubtitle")
    public ResponseEntity<String> importFromSubtitle(@RequestBody String subtitleFileName){
        ResponseEntity<String> resp = null;
        try{
            resp = savedLineServiceImpl.importFromSubtitle(subtitleFileName);
        }catch (Exception e){
            log.error("error");
            log.error(e.getMessage(),e);
        }
        return resp;
    }

//    @RequestMapping("/saveImage2DB")
//    public ResponseEntity<String> saveImage2DB(@RequestParam("filename") String filename){
//        ResponseEntity<String> resp = null;
//        try {
//            test2ServiceImpl.saveImage2DB(filename);
//        } catch (Exception e) {
//            log.error("error");
//            log.error(e.getMessage(),e);
//        }
//        return resp;
//    }


}
