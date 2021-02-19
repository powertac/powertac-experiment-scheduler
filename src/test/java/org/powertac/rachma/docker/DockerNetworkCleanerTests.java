package org.powertac.rachma.docker;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powertac.rachma.docker.network.DockerNetwork;
import org.powertac.rachma.docker.network.DockerNetworkCleaner;
import org.powertac.rachma.docker.network.DockerNetworkCleanerImpl;
import org.powertac.rachma.docker.network.DockerNetworkRepository;
import org.powertac.rachma.job.Job;
import org.powertac.rachma.job.JobRepository;
import org.powertac.rachma.job.JobStatus;
import org.powertac.rachma.job.JobStatusImpl;
import org.powertac.rachma.job.exception.JobNotFoundException;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class DockerNetworkCleanerTests {

    @Test
    void completedJobNetworkRemovedTest() throws JobNotFoundException {
        DockerNetworkRepository networks = Mockito.mock(DockerNetworkRepository.class);
        JobRepository jobs = Mockito.mock(JobRepository.class);
        DockerNetworkCleaner networkCleaner = new DockerNetworkCleanerImpl(networks, jobs);

        DockerNetwork network = new DockerNetwork("c6f6395de5c459b3544a47225a7b5a6a", "sim.x1yz56746simz");
        Set<DockerNetwork> managedNetworks = Stream.of(network).collect(Collectors.toSet());
        Mockito.when(networks.findAll()).thenReturn(managedNetworks);

        JobStatus jobStatus = new JobStatusImpl();
        jobStatus.setFailed();
        Job job = Mockito.mock(Job.class);
        Mockito.when(job.getStatus()).thenReturn(jobStatus);
        Mockito.when(jobs.find("x1yz56746simz")).thenReturn(job);

        networkCleaner.removeOrphanedNetworks();
        Mockito.verify(networks).removeNetwork(network);
    }

    @Test
    void failedJobNetworkRemovedTest() throws JobNotFoundException {
        DockerNetworkRepository networks = Mockito.mock(DockerNetworkRepository.class);
        JobRepository jobs = Mockito.mock(JobRepository.class);
        DockerNetworkCleaner networkCleaner = new DockerNetworkCleanerImpl(networks, jobs);

        DockerNetwork network = new DockerNetwork("9e107d9d372bb6826bd81d3542a419d6", "sim.abcdefg123456");
        Set<DockerNetwork> managedNetworks = Stream.of(network).collect(Collectors.toSet());
        Mockito.when(networks.findAll()).thenReturn(managedNetworks);

        JobStatus jobStatus = new JobStatusImpl();
        jobStatus.setFailed();
        Job job = Mockito.mock(Job.class);
        Mockito.when(job.getStatus()).thenReturn(jobStatus);
        Mockito.when(jobs.find("abcdefg123456")).thenReturn(job);

        networkCleaner.removeOrphanedNetworks();
        Mockito.verify(networks).removeNetwork(network);
    }

    @Test
    void runningNetworkNotRemovedTest() throws JobNotFoundException {
        DockerNetworkRepository networks = Mockito.mock(DockerNetworkRepository.class);
        JobRepository jobs = Mockito.mock(JobRepository.class);
        DockerNetworkCleaner networkCleaner = new DockerNetworkCleanerImpl(networks, jobs);

        DockerNetwork network = new DockerNetwork("d77bd8b472e97d21b4bb6026e217eaed", "sim.1burp596");
        Set<DockerNetwork> managedNetworks = Stream.of(network).collect(Collectors.toSet());
        Mockito.when(networks.findAll()).thenReturn(managedNetworks);

        JobStatus jobStatus = new JobStatusImpl();
        jobStatus.setRunning();
        Job job = Mockito.mock(Job.class);
        Mockito.when(job.getStatus()).thenReturn(jobStatus);
        Mockito.when(jobs.find("1burp596")).thenReturn(job);

        networkCleaner.removeOrphanedNetworks();
        Mockito.verify(networks, Mockito.never()).removeNetwork(network);
    }

}
