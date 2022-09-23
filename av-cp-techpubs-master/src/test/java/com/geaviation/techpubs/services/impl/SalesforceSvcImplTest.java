package com.geaviation.techpubs.services.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.geaviation.techpubs.controllers.requests.EnableStatusBody;
import com.geaviation.techpubs.data.api.techlib.ISalesforceCompanyData;
import com.geaviation.techpubs.models.techlib.SalesforceCompanyLookupEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.geaviation.techpubs.data.api.techlib.IAirframeLookupData;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.techlib.dto.AirframeDto;
import com.geaviation.techpubs.services.api.ISalesforceSvc;

public class SalesforceSvcImplTest {
	
	@Mock
    	private IAirframeLookupData airframeLookupData;
	
	@Mock
	private ISalesforceSvc salesforceSvc;

	@Mock
	private ISalesforceCompanyData iSalesforceCompanyData;
	
	@InjectMocks
	private SalesforceSvcImpl salesforceSvcImpl;
	
	private List<AirframeDto> AirframeDTOListMock;
	
	@Before
	public void setup() {
	MockitoAnnotations.initMocks(this);
	}
	
	AirframeDto airframeDTO = new AirframeDto ();
	List<AirframeDto> airframeDTOList = new ArrayList<AirframeDto>();
	
	@Test
	public void getEntitledAirframesWithGEAEReturnsAirframeDtoList() throws TechpubsException {
		airframeDTOList.add(airframeDTO);
		String icaoCode = "GEAE";
		when(airframeLookupData.getAllAirframes()).thenReturn(airframeDTOList);
		List<AirframeDto> AirframeDTOListMock = salesforceSvcImpl.getEntitledAirframes(icaoCode);
		assertEquals(airframeDTOList.size(),AirframeDTOListMock.size());
		
	}
	
	@Test
	public void getEntitledAirframesWithoutGEAEReturnsAirframeDtoList() throws TechpubsException {
		airframeDTOList.add(airframeDTO);
		String icaoCode = "YDHH";
		when(airframeLookupData.getAllEntitledAirframes(icaoCode)).thenReturn(airframeDTOList);
		List<AirframeDto> AirframeDTOListMock = salesforceSvcImpl.getEntitledAirframes(icaoCode);
		assertEquals(airframeDTOList.size(),AirframeDTOListMock.size());
		
	}
	
	@Test
	public void getEntitledAirframesWithoutGEAEReturnsAirframeDtoListEmpty() throws TechpubsException {
		String icaoCode = "YDHH";
		when(airframeLookupData.getAllEntitledAirframes(icaoCode)).thenReturn(airframeDTOList);
		List<AirframeDto> AirframeDTOListMock = salesforceSvcImpl.getEntitledAirframes(icaoCode);
		assertEquals(airframeDTOList.size(),AirframeDTOListMock.size());
		
	}
	
	@Test
	public void getEntitledAirframesWithGEAEReturnsAirframeDtoListEmpty() throws TechpubsException {
		String icaoCode = "GEAE";
		when(airframeLookupData.getAllAirframes()).thenReturn(airframeDTOList);
		List<AirframeDto> AirframeDTOListMock = salesforceSvcImpl.getEntitledAirframes(icaoCode);
		assertEquals(airframeDTOList.size(),AirframeDTOListMock.size());
		
	}

	@Test
	public void updateEnableStatusValidRequest() {
		EnableStatusBody enableStatusBody = new EnableStatusBody();
		UUID mock = UUID.randomUUID();
		List <String> companyIdStringList = new ArrayList<>();
		companyIdStringList.add(String.valueOf(mock));
		List <UUID> companyIdUUIDList = new ArrayList<>();
		companyIdUUIDList.add(mock);
		enableStatusBody.setCompanyIds(companyIdStringList);
		enableStatusBody.setEnabled(true);
		SalesforceCompanyLookupEntity salesforceCompanyLookupEntity = new SalesforceCompanyLookupEntity();
		List <SalesforceCompanyLookupEntity> companyEntityList = new ArrayList<>();
		companyEntityList.add(0, salesforceCompanyLookupEntity);
		when(iSalesforceCompanyData.findByIdIn(companyIdUUIDList)).thenReturn(companyEntityList);
		salesforceSvcImpl.updateEnableStatus(enableStatusBody);
		verify(iSalesforceCompanyData).saveAll(companyEntityList);
	}

}
