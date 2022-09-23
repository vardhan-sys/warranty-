package com.geaviation.techpubs.services.api.admin;

import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.techlib.BookEntity;
import com.geaviation.techpubs.models.techlib.CompanyEngineModelEntity;
import com.geaviation.techpubs.models.techlib.dto.*;
import com.geaviation.techpubs.models.techlib.response.BookcaseKeyListResponse;
import com.geaviation.techpubs.models.techlib.response.CompanyMdmEngineModelResponse;
import java.util.List;

public interface IEngineApp {

     /**
      * Get all Aircraft Engines (that the user has access to) from the Asset Service
      * owned or operated by the given company.
      *
      * @param ssoId sso of the admin user performing the api action
      * @param company The company name to retrieve engines for
      * @return Engine Model Hierarchy retrieved from MDM database
      * @throws TechpubsException if there was a problem hitting the Asset Service endpoint.
      */
     CompanyMdmEngineModelResponse getCompanyMdmEngineModels(String ssoId, String company) throws TechpubsException;

     /**
      * Get all Engine Models (that the user has access to) currently listable for the company.
      *
      * @param ssoId sso of the admin user performing the api action
      * @param icaoCode company code to retrieve the saved engine models
      * @return Engine models currently listable
      */
     List<CompanyEngineModelEntity> getSavedCompanyEngineModels(String ssoId, String icaoCode);

     /**
      * Save Engine Models passed in to make them listable within the system for
      * the specified company.
      *
      * @param ssoId sso of the admin user performing the api action
      * @param icaoCode company code to save engine models to
      * @param addCompanyEngineModelDto dto containing the engine models to save to the company
      * @throws TechpubsException if the user does not have access to the posted Engine Models or
      * empty DTO
      */
     void saveCompanyEngineModels(String ssoId, String icaoCode, AddCompanyEngineModelDto addCompanyEngineModelDto) throws TechpubsException;

     /**
      * Get all Books associated to a company - engine model pair.
      *
      * @param ssoId sso of the admin user performing the api action
      * @param icaoCode company code to get books associated
      * @param engineModel engine to get books associated
      * @return List of book entities
      * @throws TechpubsException if the user does not have access to the engine model
      */
     List<BookEntity> getCompanyEngineModelBooks(String ssoId, String icaoCode, String engineModel) throws TechpubsException;

     /**
      * Delete listable Engine Model and cascade remove all enablements for the
      * company - engine model pair.
      *
      * @param ssoId sso of the admin user performing the api action
      * @param icaoCode company code to remove engine model enablements
      * @param engineModel engine model to be removed
      * @throws TechpubsException Invalid engine model Icao Code pair
      */
     void deleteCompanyEngineModel(String ssoId, String icaoCode, String engineModel) throws TechpubsException;

     /**
      * Get all SMM Documents for a company - engine model Pair.
      *
      * @param ssoId sso of the admin user performing the api action
      * @param icaoCode company code to retrieve SMM Documents for
      * @param engineModel engine model to retrieve SMM Documents for
      * @return List of SMM Documents
      * @throws TechpubsException Invalid permissions or internal error
      */
     SMMDocsDto getCompanyEngineModelSMMDocuments(String ssoId, String icaoCode, String engineModel) throws TechpubsException;

     /**
      * Get all technology levels with a mapped Boolean if the company - engine model pair
      * had the level previously enabled.
      *
      * @param ssoId sso of the admin user performing the api action
      * @param icaoCode company code to check technology level enablements
      * @param engineModel engine model t
      * @return All technology levels for each bookcase with boolean flag
      * @throws TechpubsException Invalid permissions
      */
     List<TechLevelEngineResponse> getCompanyEngineModelTechLevel(String ssoId, String icaoCode, String engineModel) throws TechpubsException;

     /**
      * Enable or Disable all posted SMM Documents for the company - engine model pair.
      *
      * @param ssoId sso of the admin user performing the api action
      * @param icaoCode company code to save SMM Documents for
      * @param addEngineSMMDocsDto List of SMM Documents for enable or disable
      * @param enable if we are enabling the documents
      * @throws TechpubsException Invalid permissions or empty DTO
      */
     void saveCompanyEngineModelSMMDocuments(String ssoId, String icaoCode, AddEngineSMMDocsDto addEngineSMMDocsDto, Boolean enable) throws TechpubsException;


     /**
      * Get all Engine Model Programs for the given engine model.
      *
      * @param engineModel engine model to retrieve Engine Model Programs for
      * @return List of Engine Model Programs
      */
     BookcaseKeyListResponse getBookcaseKeyMappings(String engineModel);

     /**
      * Save Engine Model -> Technology Level enablements pairs for a given company.
      * @param ssoId sso of the admin user performing the api action
      * @param icaoCode company code to save technology level enablements for
      * @param addCompanyEngineTechLevelDto List of Engine Model -> Technology Level enablements
      * @throws TechpubsException Invalid permissions or empty DTO
      */
     void saveCompanyEngineModelTechnologyLevels(String ssoId, String icaoCode, AddCompanyEngineTechLevelDto addCompanyEngineTechLevelDto) throws TechpubsException;
}
