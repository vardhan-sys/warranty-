package com.geaviation.techpubs.services.util.admin;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.geaviation.techpubs.data.api.techlib.IEngineModelData;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.techlib.EngineModelEntity;
import com.geaviation.techpubs.services.util.AppConstants;

@Component
public class EngineModelTableUpdater {
	
    @Autowired
    private IEngineModelData engineModelData;
    
    @Autowired
    private AdminAppUtil adminAppUtil;

	public Set<EngineModelEntity> validateEngineModels(List<String> inputEngineModels, String ssoId)
			throws TechpubsException {
		
		List<EngineModelEntity> techlibModels = engineModelData.findByModelIn(inputEngineModels);
		List<String> techlibModelModels = techlibModels.stream().map(m -> m.getModel()).collect(Collectors.toList());
		if (techlibModels.size() != inputEngineModels.size()) {
			List<String> modelsNotInTechlib = inputEngineModels.stream().filter(m -> !techlibModelModels.contains(m))
					.collect(Collectors.toList());
			// Grab all engine family -> models with GEA org
			StringBuilder response = adminAppUtil.getCompanyEngineFamilyModels(ssoId, AppConstants.GEA_ORG);
			Map<String, List<String>> engineFamilyToModelListMap = adminAppUtil
					.parseMdmCompanyEngineModelResponse(response);
			Map<String, String> engineModelToFamilyMap = new HashMap<>();
			engineFamilyToModelListMap.forEach((key, value) -> value.forEach(m -> engineModelToFamilyMap.put(m, key)));
			Set<String> allModels = engineFamilyToModelListMap.values().stream().flatMap(Collection::stream)
					.collect(Collectors.toSet());
			for (String model : modelsNotInTechlib) {
				if (allModels.contains(model)) {
					// If model is a valid MDM model and is missing in techlib, insert it now
					EngineModelEntity engineModelEntity = new EngineModelEntity();
					engineModelEntity.setModel(model);
					engineModelEntity.setFamily(engineModelToFamilyMap.get(model));
					engineModelEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
					engineModelEntity.setLastUpdatedAt(new Timestamp(System.currentTimeMillis()));
					engineModelData.save(engineModelEntity);
				} else {
					throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER,
							"Invalid engine model: " + model);
				}
			}
		}
		return new HashSet<>(engineModelData.findByModelIn(inputEngineModels));
	}

}
