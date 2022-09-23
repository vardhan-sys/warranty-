package com.geaviation.techpubs.services.impl;

import com.geaviation.techpubs.data.api.IDocTDData;
import com.geaviation.techpubs.data.api.IProgramData;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.ProgramItemModel;
import com.geaviation.techpubs.services.api.IDocSubSystemApp;
import com.geaviation.techpubs.services.api.IProgramApp;
import com.geaviation.techpubs.services.util.AppConstants;
import com.geaviation.techpubs.services.util.TechpubsAppUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
@Component
public abstract class AbstractTDAppImpl extends AbstractDocSubSystemAppImpl implements
    IDocSubSystemApp {

    private static final Logger log = LogManager.getLogger(AbstractTDAppImpl.class);

    @Autowired
    private IProgramData iProgramData;

    @Autowired
    private IProgramApp iProgramApp;

    @Override
    @LogExecutionTime
    public List<DocumentItemModel> getDocuments(String ssoId, String portalId,
        Map<String, String> searchFilter,
        Map<String, String> queryParams) throws TechpubsException {

        List<DocumentItemModel> docItemList = new ArrayList<>();

        String family = searchFilter.get(AppConstants.FAMILY);
        String model = searchFilter.get(AppConstants.MODEL);
        String aircraft = searchFilter.get(AppConstants.AIRCRAFT);
        String tail = searchFilter.get(AppConstants.TAIL);
        List<String> esnList = new ArrayList<>();
        if (TechpubsAppUtil.isNotNullandEmpty(searchFilter.get(AppConstants.ESN))) {
            esnList = Arrays.asList(searchFilter.get(AppConstants.ESN).split("\\|"));
        }

        log.debug("Input ESN list size-" + esnList.size());

        List<String> authorizedProgramsList = iProgramApp
            .getAuthorizedPrograms(ssoId, portalId, getSubSystem());
        if (!authorizedProgramsList.isEmpty()) {

            List<ProgramItemModel> programList = null;
            try {
                programList = iProgramApp
                    .getProgramItemListForRequest(ssoId, portalId, family,
                        model, aircraft, tail, esnList, getSubSystem(), authorizedProgramsList);
            } catch (IOException e) {
                log.info(e.getMessage());
            } catch (DocumentException e) {
                log.info(e.getMessage());
            }

            if (!(programList.isEmpty() || programList == null)) {

                // Add SPM Manuals if other manuals are found (and
                // authorized)
                ProgramItemModel spmProgram = iProgramData.getSpmProgramItem();
                if (spmProgram != null && authorizedProgramsList
                    .contains(spmProgram.getProgramDocnbr())
                    && !programList.contains(spmProgram)) {
                    programList.add(spmProgram);
                }

                if (portalId.equalsIgnoreCase(AppConstants.GEHONDA)) {
                    // Add Honda-SPM Manuals if other honda manuals are found (and
                    // authorized)
                    ProgramItemModel hondaSpmProgram = iProgramData.getHondaSpmProgramItem();
                    if (hondaSpmProgram != null && authorizedProgramsList
                        .contains(hondaSpmProgram.getProgramDocnbr())
                        && !programList.contains(hondaSpmProgram)) {
                        programList.add(hondaSpmProgram);
                    }
                }

                IDocTDData iDocSubSystemData = (IDocTDData) docDataRegServices
                    .getSubSystemService(getSubSystem());

                for (ProgramItemModel programItemModel : programList) {
                    docItemList.addAll(iDocSubSystemData.getDocumentsByProgramType(programItemModel,
                        queryParams.get(AppConstants.TYPE)));
                }
            }
        }

        return docItemList;
    }
}