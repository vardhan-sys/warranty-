package com.geaviation.techpubs.models.techlib.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AddEngineSMMDocsDto {

        private Map<String, List<UUID>> engineSMMDocuments;

        public AddEngineSMMDocsDto() {}

        public Map<String, List<UUID>> getEngineSMMDocuments() {
                return engineSMMDocuments;
        }

        public void setEngineSMMDocuments(Map<String, List<UUID>> engineSMMDocuments) {
                this.engineSMMDocuments = engineSMMDocuments;
        }
}
