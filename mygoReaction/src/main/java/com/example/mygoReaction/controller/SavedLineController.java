package com.example.mygoReaction.controller;

import com.example.mygoReaction.model.dto.SearchLineForm;
import com.example.mygoReaction.model.resp.GenericResp;
import com.example.mygoReaction.model.req.GetScreenCapFromVideoReq;
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
     * get the list of scene info which specified by users
     * @param searchLineForm form object for storing the searching parameters
     * @return GenericForm object which store the screen cap URL and other info
     */
    @RequestMapping(method = RequestMethod.POST, value="/getSavedLines")
    public ResponseEntity<GenericResp> getSavedLines(@RequestBody SearchLineForm searchLineForm){
        GenericResp form = null;
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
     * Retrieves a screen capture from a video based on the specified saved line ID
     * and style.
     * 
     * @param req Request object containing the saved line ID and style parameters.
     * @return A ResponseEntity containing a GenericForm object with the screen
     *         capture URL and related information.
     *         Returns an error log in case of exceptions.
     */
    @RequestMapping(method = RequestMethod.POST, value = "getScreenCapFromVideo")
    public ResponseEntity<GenericResp> getScreenCapFromVideo(@RequestBody GetScreenCapFromVideoReq req) {
        GenericResp hehe = null;
        try {
            hehe = savedLineServiceImpl.getScreenCapFromVideo(req.getSavedLineId(), req.getStyle());
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
