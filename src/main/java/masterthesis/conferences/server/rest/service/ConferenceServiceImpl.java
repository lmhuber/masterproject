package masterthesis.conferences.server.rest.service;

import masterthesis.conferences.data.ConferenceRepository;
import masterthesis.conferences.data.metrics.ApplicationType;
import masterthesis.conferences.data.model.AdditionalMetric;
import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;
import masterthesis.conferences.data.model.IngestConfiguration;
import masterthesis.conferences.server.controller.StorageController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static masterthesis.conferences.data.metrics.zoom.AudioLatency.MEETING_ID;

@Service
public class ConferenceServiceImpl implements ConferenceService {

	private final ConferenceRepository conferenceRepository;

	@Autowired
	public ConferenceServiceImpl() {
		this.conferenceRepository = StorageController.getRepository();
	}

	@Override
	public int getMaxConfigId() {
		int max = 0;
		for (Conference conference : conferenceRepository.getConferences()) {
			for (ConferenceEdition edition : conference.getConferenceEditions()) {
				for (AdditionalMetric metric : edition.getAdditionalMetrics()) {
					if (metric.getConfig() != null) {
						Math.max(max, metric.getConfig().getId() + 1);
					}
				}
			}
		}
		return max;
	}

	@Override
	public int getMaxAdditionalMetricId() {
		int max = 0;
		for (Conference conference : conferenceRepository.getConferences()) {
			for (ConferenceEdition edition : conference.getConferenceEditions()) {
				for (AdditionalMetric metric : edition.getAdditionalMetrics()) {
						Math.max(max, metric.getId() + 1);
				}
			}
		}
		return max;
	}

	@Override
	public List<Conference> findAll() {
		return conferenceRepository.getConferences();
	}

	@Override
	public Conference findById(String title) {
		Optional<Conference> result = conferenceRepository.findById(title);
		if (result == null) return null;
		Conference conference;

		if (result.isPresent()) {
			conference = result.get();
		} else {
			return null;
		}

		return conference;
	}

	@Override
	public void save(Conference conference) {
		try {
			conferenceRepository.save(conference);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void save(AdditionalMetric metric, String title, int id) {
		try {
			conferenceRepository.addMetric(findById(title), findById(id), metric);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void save(IngestConfiguration config, int metricId, String title, int id) {
		try {
			conferenceRepository.addConfig(findById(title), findById(id), findByMetricId(metricId), config);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void deleteById(String title) {
		try {
			conferenceRepository.deleteById(title);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public ConferenceEdition findEditionByMetricId(int id) {
		return conferenceRepository.getEditonByMetric(id);
	}

	@Override
	public ConferenceEdition findById(int id) {
		return conferenceRepository.getEdition(id);
	}

	@Override
	public void save(ConferenceEdition edition, String title) {
		try {
			conferenceRepository.addEdition(findById(title), edition);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Conference> searchBy(String name) {
		return List.of(conferenceRepository.getConference(name));
	}

	@Override
	public AdditionalMetric findByMetricId(int id) {
		return conferenceRepository.getMetric(id);
	}

	@Override
	public IngestConfiguration findConfigById(int ingestConfigId) {
		return conferenceRepository.getIngestConfiguration(ingestConfigId);
	}

	@Override
	public List<String> fetchAllMeetingIds() {
		List<String> meetingIds = new ArrayList<>();
		for (Conference conference : conferenceRepository.getConferences()) {
			for (ConferenceEdition edition: conference.getConferenceEditions()){
				for (AdditionalMetric metric : edition.getAdditionalMetrics()) {
					if (metric.getConfig().getType() == ApplicationType.ZOOM) {
						meetingIds.add(metric.getConfig().getParameters().get(MEETING_ID));
					}
				}
			}
		}
		return meetingIds;
	}

	@Override
	public List<Integer> fetchAllMetricIdsPerConference(String title) {
		Set<Integer> metrics = new HashSet<>();
		for (Conference conference : conferenceRepository.getConferences()) {
			for (ConferenceEdition edition: conference.getConferenceEditions()){
				metrics.addAll(edition.getAdditionalMetricIds());
			}
		}
		return List.copyOf(metrics);
	}

	@Override
	public List<String> fetchAllMetricsPerConference(String title) {
		Set<String> metrics = new HashSet<>();
		for (Conference conference : conferenceRepository.getConferences()) {
			for (ConferenceEdition edition: conference.getConferenceEditions()){
				for (AdditionalMetric metric : edition.getAdditionalMetrics()) {
					metrics.add(metric.getMetricIdentifier());
				}
			}
		}
		return List.copyOf(metrics);
	}

}






