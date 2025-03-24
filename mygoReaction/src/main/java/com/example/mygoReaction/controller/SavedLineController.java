package com.example.mygoReaction.controller;

import com.example.mygoReaction.model.dto.SearchLineForm;
import com.example.mygoReaction.model.dto.Test2Dto;
import com.example.mygoReaction.model.form.GenericForm;
import com.example.mygoReaction.service.Impl.SavedLineServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("line")
public class SavedLineController {

    private static final Logger log = LoggerFactory.getLogger(SavedLineController.class);

    private final SavedLineServiceImpl savedLineServiceImpl;
    public SavedLineController(
            SavedLineServiceImpl savedLineServiceImpl
    ) {
        this.savedLineServiceImpl = savedLineServiceImpl;
    }


    /**
     * List all saved_line record matching the searching criteria
     *
     * @param t2d data form object for searching the line
     * @return response form object with the records which matching the criteria
     */
    @RequestMapping(method = RequestMethod.POST, value="/findLine")
    public ResponseEntity<GenericForm> findLine(@RequestBody Test2Dto t2d){
        GenericForm form = savedLineServiceImpl.findByKeyword(t2d);
        return ResponseEntity.ok(form);
    }

    /**
     * get the list of scene info which specified by users
     * @param searchLineForm form object for storing the searching parameters
     * @return GenericForm object which store the screen cap URL and other info
     */
    @RequestMapping(method = RequestMethod.POST, value="/getSavedLines")
    public ResponseEntity<GenericForm> getSavedLines(@RequestBody SearchLineForm searchLineForm){
        GenericForm form = null;
        try {
            form = savedLineServiceImpl.getSavedLines(searchLineForm);
            log.info("success");
        } catch (Exception e){
            log.error("error");
            log.error(e.getMessage(),e);
        }
        return ResponseEntity.ok(form);
    }

    /**
     * get a screen cap for specific scene
     * @param savedLineId scene ID for specific scene, obtained from /getSavedLines
     * @return GenericForm object which store the screen cap URL and other info
     */
    @RequestMapping(method = RequestMethod.GET, value = "getScreenCapFromVideo")
    public ResponseEntity<GenericForm> getScreenCapFromVideo(@RequestParam Integer savedLineId){
        GenericForm hehe = null;
        try {
             hehe = savedLineServiceImpl.getScreenCapFromVideo(savedLineId);
        } catch (Exception e) {
            log.error("error");
            log.error(e.getMessage(),e);
        }
        return ResponseEntity.ok(hehe);
    }

    /**
     * import lines from the specified subtitle file to DB
     * @param subtitleFileName subtitle filename located in folder ./asset/subtitle
     * @return a string for indicating the import is success for not
     */
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

}
