package eu.lod2.edcat.contextPlugin;

import eu.lod2.edcat.format.CompactedListFormatter;
import eu.lod2.edcat.format.ResponseFormatter;
import eu.lod2.edcat.utils.JsonLdContext;
import org.openrdf.model.Model;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by yyz on 7/30/14.
 */
public class test {
    public static void main(String[] args) {
        // catalog: http://tfvirt-lod2-dcat/catalogs/bbc29f1c-8876-41ef-8246-4fdf2fe49000/
        // test dataset: http://tfvirt-lod2-dcat/catalogs/bbc29f1c-8876-41ef-8246-4fdf2fe49000/datasets/5ed51c6d-6809-43ba-a828-fd86e4d1a27a
        // root segment: http://tfvirt-lod2-dcat/catalogs/bbc29f1c-8876-41ef-8246-4fdf2fe49000/datasets/d699274c-6be8-41c5-8e79-3eac3adb739c
        // segment 1: http://tfvirt-lod2-dcat/catalogs/bbc29f1c-8876-41ef-8246-4fdf2fe49000/datasets/c1043aab-03ee-40a3-8061-8272a064837d
/*
        try {
            String response= ClientBuilder.newClient()
                    .target("http://localhost:8080/edcat/catalogs/context/test,test1,test2")
                    .request(MediaType.APPLICATION_JSON)
                    .get()
                    .readEntity(String.class);
            System.out.print(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return;
    }
    */
        String tags[] = {"test"};
        ContextSearchService contextSearchService =new ContextSearchService();
        try {
            JsonLdContext jsonLdContext = new JsonLdContext(JsonLdContext.Kind.Dataset);
            Model statements= contextSearchService.getDatasetModel(tags);
            ResponseFormatter formatter=new CompactedListFormatter(jsonLdContext);
            Object body=formatter.format(statements);
            ResponseEntity<Object> response = new ResponseEntity<Object>(body, new HttpHeaders(), HttpStatus.OK);
            System.out.println(response);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
