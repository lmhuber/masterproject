package masterthesis.conferences.server.rest.service;

import masterthesis.conferences.data.model.AdditionalMetric;
import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;
import masterthesis.conferences.data.model.IngestConfiguration;

import java.util.List;

public interface ConferenceService {

	/**
	 * Searches the database for the highest id of conference editions
	 *
	 * @return max id of conference editions
	 */
	int getMaxEditionId();

	/**
	 * Searches the database for the highest id of ingest configurations
	 *
	 * @return max id of ingest configurations
	 */
	int getMaxConfigId();

	/**
	 * Searches the database for the highest id of additional metrics
	 *
	 * @return max id of additional metrics
	 */
	int getMaxAdditionalMetricId();

	/**
	 * Fetches all conferences that are stored
	 *
	 * @return list of all conferences in the database
	 */
	List<Conference> findAll();

	/**
	 * Finds a conference object from the database via its title
	 *
	 * @param title conference title
	 * @return corresponding conference object
	 */
	Conference findById(String title);

	/**
	 * Finds a conference edition from the database via its id
	 *
	 * @param id the database id of the edition
	 * @return corresponding conference edition object
	 */
	ConferenceEdition findById(int id);

	/**
	 * Saves a Conference edition object into the database
	 *
	 * @param edition conference edition to save
	 * @param title conference title as a reference
	 */
	void save(ConferenceEdition edition, String title);

	/**
	 * Saves a conference object into the database
	 *
	 * @param conferences conference object to save
	 */
	void save(Conference conferences);

	/**
	 * Saves an additional metric object into the database
	 *
	 * @param metric the metric object to save
	 * @param title conference title as a reference
	 * @param id conference edition id as a reference
	 */
	void save(AdditionalMetric metric, String title, int id);

	/**
	 * Saves an ingest configuration object into the database
	 *
	 * @param config ingest configuration object to save
	 * @param metricId metric id as a reference
	 * @param title conference title as a reference
	 * @param id conference edition id as a reference
	 */
	void save(IngestConfiguration config, int metricId, String title, int id);

	/**
	 * Deletes an conference from the database
	 *
	 * @param title title as a key for deletion
	 */
	void deleteById(String title);

	/**
	 * Finds a conference edition via the id of an additional metric
	 *
	 * @param id the id of an additional metric as a reference
	 * @return referenced conference edition
	 */
	ConferenceEdition findEditionByMetricId(int id);

	/**
	 * Searches all conferences via a search text
	 *
	 * @param name the search text for a title
	 * @return list of conferences, which contain the serarch text
	 */
	List<Conference> searchBy(String name);

	/**
	 * Finds a metric from the database
	 *
	 * @param id the id of the metric in the database
	 * @return corresponding additional metric object
	 */
    AdditionalMetric findByMetricId(int id);

	/**
	 * Finds an ingest configuration from the database
	 *
	 * @param ingestConfigId id of the ingest config in the database
	 * @return corresponding ingest config object
	 */
    IngestConfiguration findConfigById(int ingestConfigId);

	/**
	 * Fetches all metrics that are saved across the conferences
	 * @return list of all metrics that are found in the database
	 */
	List<String> fetchAllMetricsPerConference();
}
