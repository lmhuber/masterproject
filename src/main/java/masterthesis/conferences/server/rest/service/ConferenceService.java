package masterthesis.conferences.server.rest.service;

import masterthesis.conferences.data.model.AdditionalMetric;
import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;
import masterthesis.conferences.data.model.IngestConfiguration;

import java.util.List;

public interface ConferenceService {

	int getMaxEditionId();

	int getMaxConfigId();

	int getMaxAdditionalMetricId();

	List<Conference> findAll();

	Conference findById(String title);

	ConferenceEdition findById(int id);

	void save(ConferenceEdition edition, String title);

	void save(Conference conferences);

	void save(AdditionalMetric metric, String title, int id);

	void save(IngestConfiguration config, int metricId, String title, int id);

	void deleteById(String title);

	ConferenceEdition findEditionByMetricId(int id);

	List<Conference> searchBy(String name);

    AdditionalMetric findByMetricId(int id);

    IngestConfiguration findConfigById(int ingestConfigId);

	List<String> fetchAllMetricsPerConference();
}
