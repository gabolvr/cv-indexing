package cvindexing;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CvRepository extends ElasticsearchRepository<Cv, String> {
    
    // @Query("{\"bool\": {\"must\": [{\"fuzzy\": {\"data\": \"?0\"}}]}}")
    @Query("{\"bool\": {\"must\": [{\"match\": {\"content\": \"?0\"}}]}}")
    // @Query("{\"bool\": {\"must\": [{\"wildcard\": {\"data\": \"*?0*\"}}]}}")
    List<Cv> searchKeywordInCv(String keyword);
}
