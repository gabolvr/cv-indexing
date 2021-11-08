package cvindexing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/cvs")
public class CvController {

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
        logger.info("Searching CVs with keyword \"{}\"", keyword);
        return ResponseEntity.ok(cvSearchService.searchCvs(keyword));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Cv> findCvById(@PathVariable("id") String id) {
        logger.info("Searching CVs with ID {}", id);
        Cv cv = cvSearchService.findCvById(id);
        return new ResponseEntity<>(cv, cv != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PostMapping(consumes = TYPE_PDF)
    public void addCvPdf(@RequestBody byte[] pdf) throws IOException {
        logger.info("Adding CV with PDF format");
        String pdfContent = getContentFromPdf(new ByteArrayInputStream(pdf));
        cvSearchService.createIndex(new Cv(pdfContent));
    }

    @PostMapping(consumes = TYPE_DOC)
    public void addCvDoc(@RequestBody byte[] doc) throws IOException {
        logger.info("Adding CV with DOC format");
        String docContent = getContentFromDoc(new ByteArrayInputStream(doc));
        cvSearchService.createIndex(new Cv(docContent));
    }

    @PostMapping(consumes = TYPE_DOCX)
    public void addCvDocx(@RequestBody byte[] docx) throws IOException {
        logger.info("Adding CV with DOCX format");
        String docxContent = getContentFromDocx(new ByteArrayInputStream(docx));
        cvSearchService.createIndex(new Cv(docxContent));
    }

    @PostMapping
    public void addCvFile(@RequestParam("cv") MultipartFile[] files) throws IOException {
        List<Cv> cvs = new ArrayList<>();
        for (MultipartFile file : files) {
            switch (file.getContentType()) {
                case TYPE_PDF:
                    logger.info("Adding CV {}", file.getOriginalFilename());
                    String pdfContent = getContentFromPdf(file.getInputStream());
                    cvs.add(new Cv(pdfContent));
                    break;
                case TYPE_DOC:
                    logger.info("Adding CV {}", file.getOriginalFilename());
                    String docContent = getContentFromDoc(file.getInputStream());
                    cvs.add(new Cv(docContent));
                    break;
                case TYPE_DOCX:
                    logger.info("Adding CV {}", file.getOriginalFilename());
                    String docxContent = getContentFromDocx(file.getInputStream());
                    cvs.add(new Cv(docxContent));
                    break;
                default:
                    logger.error("CV {} has invalid type {} and was not added", file.getOriginalFilename(), file.getContentType());
            }
        }
        if (!cvs.isEmpty()) {
            cvSearchService.createIndexBulk(cvs);
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
