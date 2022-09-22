package org.powertac.rachma.hotfix;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.file.GameFileExporter;
import org.powertac.rachma.treatment.Treatment;
import org.powertac.rachma.treatment.TreatmentRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class EvChargerTreatmentExporter {

    private final GameFileExporter fileExporter;
    private final TreatmentRepository treatmentRepository;
    private final Logger logger;

    public EvChargerTreatmentExporter(GameFileExporter fileExporter, TreatmentRepository treatmentRepository) {
        this.fileExporter = fileExporter;
        this.treatmentRepository = treatmentRepository;
        logger = LogManager.getLogger(EvChargerTreatmentExporter.class);
    }

    public void export() {
        try {
            String hostUri = "https://uni-koeln.sciebo.de/s/UdjLGnOxllr7l7l/download?path=%2f&files=";
            /*
            Optional<Treatment> treatment10000 = treatmentRepository.findById("4c7751ff-77c7-4162-bdc2-e8f69e7eb877");
            if (treatment10000.isPresent()) {
                logger.info("exporting " + treatment10000.get().getName());
                fileExporter.exportGames(treatment10000.get().getGames(), "evcharger-01-treatment-10000", hostUri);
            }*/
            Optional<Treatment> treatment20000 = treatmentRepository.findById("2e3a0537-1f43-4c9c-8365-0644e36dcb6c");
            if (treatment20000.isPresent()) {
                logger.info("exporting " + treatment20000.get().getName());
                fileExporter.exportGames(treatment20000.get().getGames(), "evcharger-01-treatment-20000", hostUri);
            }
        } catch (IOException e) {
            logger.error(e);
        }

    }



}
