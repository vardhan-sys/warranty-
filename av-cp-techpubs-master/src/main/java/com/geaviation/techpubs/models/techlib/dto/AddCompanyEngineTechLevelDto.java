package com.geaviation.techpubs.models.techlib.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AddCompanyEngineTechLevelDto {

        private Map<String, Map<String, List<UUID>>> engineTechnologyLevels;

        public AddCompanyEngineTechLevelDto() { }

        public Map<String, Map<String, List<UUID>>> getEngineTechnologyLevels() {
                return engineTechnologyLevels;
        }

        public void setEngineTechnologyLevels(Map<String, Map<String, List<UUID>>> engineTechnologyLevels) {
                this.engineTechnologyLevels = engineTechnologyLevels;
        }
}
