package cvindexing;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/cvs")
public class CvController {
    
    private CvSearchService cvSearchService;

    @Autowired
    public CvController(CvSearchService cvSearchService) {
        this.cvSearchService = cvSearchService;
    }

    @GetMapping
    public ResponseEntity<List<Cv>> search(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(cvSearchService.searchCvs(keyword));
    }

    @PostMapping
    public void addCv(@RequestBody String data) {
        cvSearchService.createProductIndex(new Cv(data));
    }
}
