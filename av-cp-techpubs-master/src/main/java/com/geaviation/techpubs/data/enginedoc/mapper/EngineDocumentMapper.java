package com.geaviation.techpubs.data.enginedoc.mapper;

public class EngineDocumentMapper {

	private EngineDocumentMapper() {
	}

	public static String sortMapper(String field) {
		String sortBy;

		switch (field) {
		case "documentType":
			sortBy = "engineDocumentTypeLookupEntity.value";
			break;
		case "documentTitle":
			sortBy = "documentTitle";
			break;
		case "lastUpdatedDate":
			sortBy = "lastUpdatedDate";
			break;
		case "issueDate":
			sortBy = "issueDate";
			break;
		default:
			sortBy = "";
		}

		return sortBy;
	}
}
