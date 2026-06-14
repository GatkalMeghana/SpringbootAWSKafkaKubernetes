package com.forrester.index.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class TaxonomyRepository {

    @Autowired
    private EntityManager entityManager;

    public List<String> findTaxonomiesByType(String taxonomyID, String typeName){
        return (List<String>) entityManager.createNativeQuery("SELECT DISTINCT LABEL FROM  FORRBRANCHTAXONOMY " +
                "WHERE IS_ACTIVE = 1 AND TAXONOMY_ID = ? AND LABEL != ?" )
                .setParameter(1, taxonomyID)
                .setParameter(2, typeName)
                .getResultList();
    }
}
