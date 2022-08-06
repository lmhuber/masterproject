package masterthesis.conferences.server.rest.service;

import masterthesis.conferences.data.model.AdditionalMetric;
import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;
import masterthesis.conferences.data.model.IngestConfiguration;
import masterthesis.conferences.server.controller.storage.StorageController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ConferenceServiceImpl implements ConferenceService {

	private final StorageController controller;

	@Autowired
	public ConferenceServiceImpl() {
		this.controller = StorageController.getInstance();
	}

	@Override
	public int getMaxEditionId() {
		int max = 0;
		for (Conference conference : StorageController.getInstance().getConferences()) {
			for (ConferenceEdition e : conference.getConferenceEditions()) {
				max = Math.max(max, e.getId() + 1);
			}
		}
		return max;
	}

	@Override
	public int getMaxConfigId() {
		int max = 0;
		for (Conference conference : controller.getConferences()) {
			for (ConferenceEdition edition : conference.getConferenceEditions()) {
				for (AdditionalMetric metric : edition.getAdditionalMetrics()) {
					if (metric.getConfig() != null) {
						max = Math.max(max, metric.getConfig().getId() + 1);
					}
				}
			}
		}
		return max;
	}

	@Override
	public int getMaxAdditionalMetricId() {
		int max = 0;
		for (Conference conference : controller.getConferences()) {
			for (ConferenceEdition edition : conference.getConferenceEditions()) {
				for (AdditionalMetric metric : edition.getAdditionalMetrics()) {
						max = Math.max(max, metric.getId() + 1);
				}
			}
		}
		return max;
	}

	@Override
	public List<Conference> findAll() {
		return controller.getConferences();
	}

	@Override
	public Conference findById(String title) {
		Optional<Conference> result = controller.findById(title);
		if (result.isEmpty()) return null;
		return result.get();
	}

	@Override
	public void save(Conference conference) {
		try {
			controller.save(conference);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void save(AdditionalMetric metric, String title, int id) {
		try {
			controller.indexAdditionalMetric(findById(id), findById(title), metric);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void save(IngestConfiguration config, int metricId, String title, int id) {
		try {
			controller.indexIngestConfiguration(findById(id), findById(title), findByMetricId(metricId), config);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void deleteById(String title) {
		try {
			controller.deleteById(title);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public ConferenceEdition findEditionByMetricId(int id) {
		return controller.getEditonByMetric(id);
	}

	@Override
	public ConferenceEdition findById(int id) {
		return controller.getEdition(id);
	}

	@Override
	public void save(ConferenceEdition edition, String title) {
		try {
			controller.indexConferenceEdition(edition, findById(title));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Conference> searchBy(String name) {
		return List.of(controller.getConference(name));
	}

	@Override
	public AdditionalMetric findByMetricId(int id) {
		return controller.getMetric(id);
	}

	@Override
	public IngestConfiguration findConfigById(int ingestConfigId) {
		return controller.getIngestConfiguration(ingestConfigId);
	}

	@Override
	public List<String> fetchAllMetricsPerConference() {
		Set<String> metrics = new HashSet<>();
		for (Conference conference : controller.getConferences()) {
			for (ConferenceEdition edition: conference.getConferenceEditions()){
				for (AdditionalMetric metric : edition.getAdditionalMetrics()) {
					metrics.add(metric.getMetricIdentifier());
				}
			}
		}
		return List.copyOf(metrics);
	}

}






