package cvindexing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import org.apache.pdfbox.Loader;
import org.elasticsearch.client.ml.inference.preprocessing.Multi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;


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

//    @PostMapping
//    public void addCv(@RequestBody String data) {
//        cvSearchService.createProductIndex(new Cv(data));
//        System.out.println("addCv");
//    }

    @PostMapping(consumes = "application/pdf")
    public void addCvPdf(@RequestBody byte[] pdf) throws IOException {
        String pdfContent = getContentFromPdf(new ByteArrayInputStream(pdf));
        cvSearchService.createProductIndex(new Cv(pdfContent));
        System.out.println("addCvPdf");
        System.out.println(pdfContent);
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
            if (file.getContentType().equals("application/pdf")) {
                System.out.println("Loading PDF...");
                PDDocument document = Loader.loadPDF(file.getInputStream());
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                System.out.println(text);
            }
        }
    }

    private String getContentFromPdf(InputStream pdf) throws IOException {
        PDDocument document = Loader.loadPDF(pdf);
        PDFTextStripper stripper = new PDFTextStripper();
        return stripper.getText(document);
    }
}
