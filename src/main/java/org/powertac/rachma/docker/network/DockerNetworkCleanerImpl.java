package org.powertac.rachma.docker.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.job.Job;
import org.powertac.rachma.job.JobRepository;
import org.powertac.rachma.job.exception.JobNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DockerNetworkCleanerImpl implements DockerNetworkCleaner {

    private final Logger logger;
    private final DockerNetworkRepository networks;
    private final JobRepository jobs;

    @Autowired
    public DockerNetworkCleanerImpl(DockerNetworkRepository networks, JobRepository jobs) {
        logger = LogManager.getLogger(DockerNetworkCleaner.class);
        this.networks = networks;
        this.jobs = jobs;
    }

    @Override
    public void removeOrphanedNetworks() {
        for (DockerNetwork network : networks.findAll()) {
            try {
                String jobId = getJobId(network.getName());
                Job job = jobs.find(jobId);
                if (job.getStatus().isFinished()) {
                    networks.removeNetwork(network);
                    logger.info(String.format("removed orphaned network with name='%s'", network.getName()));
                }
            } catch (JobNotFoundException e) {
                logger.warn(String.format("could not find job for network with name='%s'", network.getName()));
            }
        }
    }

    private String getJobId(String networkName) {
        return networkName.replaceFirst("^sim\\.", "");
    }

}
