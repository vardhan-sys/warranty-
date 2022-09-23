package com.geaviation.techpubs.models.techlib.dto;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AddCompanyEngineModelDto {

        private List<String> engineModels;

        public AddCompanyEngineModelDto() { }

        public AddCompanyEngineModelDto(List<String> engineModels) { this.engineModels = engineModels; }

        public List<String> getEngineModels() {
                return engineModels;
        }

        public void setEngineModels(List<String> engineModels) {
                this.engineModels = engineModels;
        }
}
