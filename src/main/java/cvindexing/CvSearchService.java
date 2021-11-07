package cvindexing;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CvSearchService {

    @Autowired
    private CvRepository cvRepository;

    public List<Cv> searchCvs(String keyword) {
        return cvRepository.searchKeywordInCv(keyword);
    }

    public void createProductIndexBulk(final List<Cv> cvs) {
        cvRepository.saveAll(cvs);
    }
    
    public void createProductIndex(final Cv cv) {
        cvRepository.save(cv);
    }

    public Cv findCvById(final String id) {
        return cvRepository.findById(id).orElse(null);
    }
    
}
