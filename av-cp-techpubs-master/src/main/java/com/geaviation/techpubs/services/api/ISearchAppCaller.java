package com.geaviation.techpubs.services.api;

import com.geaviation.techpubs.exceptions.TechpubsException;

public interface ISearchAppCaller {

	String callSearchEndpoint(String ssoId, String portalId, String payload) throws TechpubsException;
}
