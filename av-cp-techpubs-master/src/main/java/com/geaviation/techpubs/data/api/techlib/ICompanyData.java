package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICompanyData extends JpaRepository<CompanyEntity, String> {


}
