package cvindexing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.elasticsearch.client.ml.inference.preprocessing.Multi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/cvs")
public class CvController {

//    @Autowired
//    private Environment environment;

    Logger logger = LoggerFactory.getLogger(CvController.class);
    
    private CvSearchService cvSearchService;

    private static final String TYPE_PDF = "application/pdf";
    private static final String TYPE_DOC = "application/msword";
    private static final String TYPE_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    @Autowired
    public CvController(CvSearchService cvSearchService) {
        this.cvSearchService = cvSearchService;
    }

    @GetMapping
    public ResponseEntity<List<Cv>> search(@RequestParam("search") String keyword) {
//        for (String profileName : environment.getActiveProfiles()) {
//            System.out.println("Currently active profile - " + profileName);
//        }
        logger.info("Log Test");
        return ResponseEntity.ok(cvSearchService.searchCvs(keyword));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Cv> findCvById(@PathVariable("id") String id) {
        Cv cv = cvSearchService.findCvById(id);
        return new ResponseEntity<>(cv, cv != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

//    @PostMapping
//    public void addCv(@RequestBody String data) {
//        cvSearchService.createProductIndex(new Cv(data));
//        System.out.println("addCv");
//    }

    @PostMapping(consumes = TYPE_PDF)
    public void addCvPdf(@RequestBody byte[] pdf) throws IOException {
        String pdfContent = getContentFromPdf(new ByteArrayInputStream(pdf));
        cvSearchService.createProductIndex(new Cv(pdfContent));
        System.out.println("addCvPdf");
        System.out.println(pdfContent);
    }

    @PostMapping(consumes = TYPE_DOC)
    public void addCvDoc(@RequestBody byte[] doc) throws IOException {
        String docContent = getContentFromDoc(new ByteArrayInputStream(doc));
        cvSearchService.createProductIndex(new Cv(docContent));
        System.out.println("addCvDoc");
        System.out.println(docContent);
    }

    @PostMapping(consumes = TYPE_DOCX)
    public void addCvDocx(@RequestBody byte[] docx) throws IOException {
        String docxContent = getContentFromDocx(new ByteArrayInputStream(docx));
        cvSearchService.createProductIndex(new Cv(docxContent));
        System.out.println("addCvDocx");
        System.out.println(docxContent);
    }

    @PostMapping
    public void addCvFile(@RequestParam("cv") MultipartFile[] files) throws IOException {
        System.out.println("addCvFile: " + files.length + " files");
        for (MultipartFile file : files) {
            System.out.println(file.getName());
            System.out.println(file.getContentType());
            System.out.println(file.getResource());
            System.out.println(file.getOriginalFilename());
            System.out.println(file.getContentType() == "application/pdf");
            switch (file.getContentType()) {
                case TYPE_PDF:
                    String pdfContent = getContentFromPdf(file.getInputStream());
                    cvSearchService.createProductIndex(new Cv(pdfContent));
                    break;
                case TYPE_DOC:
                    String docContent = getContentFromDoc(file.getInputStream());
                    cvSearchService.createProductIndex(new Cv(docContent));
                    System.out.println(docContent);
                    break;
                case TYPE_DOCX:
                    String docxContent = getContentFromDocx(file.getInputStream());
                    cvSearchService.createProductIndex(new Cv(docxContent));
                    System.out.println(docxContent);
                    break;
            }
        }
    }

    private String getContentFromPdf(InputStream pdf) throws IOException {
        PDDocument document = Loader.loadPDF(pdf);
        PDFTextStripper stripper = new PDFTextStripper();
        return stripper.getText(document);
    }

    private String getContentFromDoc(InputStream doc) throws IOException {
        HWPFDocument document = new HWPFDocument(doc);
        WordExtractor extractor = new WordExtractor(document);
        return extractor.getText();
    }

    private String getContentFromDocx(InputStream docx) throws IOException {
        XWPFDocument document = new XWPFDocument(docx);
        XWPFWordExtractor extractor = new XWPFWordExtractor(document);
        return extractor.getText();
    }
}
