package masterthesis.conferences.server.controller;

import masterthesis.conferences.data.metrics.ApplicationType;
import masterthesis.conferences.data.model.AdditionalMetric;
import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;
import masterthesis.conferences.data.model.IngestConfiguration;
import masterthesis.conferences.data.model.dto.*;
import masterthesis.conferences.server.controller.storage.MapperService;
import masterthesis.conferences.server.controller.storage.StorageController;
import masterthesis.conferences.server.dashboarding.ChartType;
import masterthesis.conferences.server.dashboarding.DashboardingUtils;
import masterthesis.conferences.server.dashboarding.Operations;
import masterthesis.conferences.server.rest.service.ConferenceService;
import masterthesis.conferences.server.rest.service.ConferenceServiceImpl;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static masterthesis.conferences.data.metrics.ApplicationType.MANUAL;
import static masterthesis.conferences.data.metrics.zoom.AudioLatency.MEETING_ID;

@Controller
@RequestMapping("/conferences")
@ComponentScan("masterthesis/conferences/data")
public class ConferenceController {

	@Autowired
	private final ConferenceService conferenceService;

	@Autowired
	private final MapperService mapperService;

	public static final String JSON_EXPORT = "src/main/resources/templates/kibana/";

	public ConferenceController() {
		this.conferenceService = new ConferenceServiceImpl();
		this.mapperService = StorageController.getMapper();
	}

	// add mapping for "/list"

	@GetMapping("/list")
	public String listConferences(Model model) {
		
		// get conferences from db
		List<Conference> conferences = conferenceService.findAll();

		// add to the spring model
		model.addAttribute("conferences", conferences);

		return "conferences/conference-view";
	}

	@PostMapping("/save")
	public String saveConference(
			@ModelAttribute("conference") Conference conference,
			BindingResult bindingResult,
			Model model,
			@ModelAttribute("conferenceFrontendDTO") ConferenceFrontendDTO dto,
			@ModelAttribute("additionalMetric") AdditionalMetricDTO metric,
			@ModelAttribute("config") IngestConfigurationDTO config)
	{

		if (bindingResult.hasErrors()) {
			return "conferences/conference-view";
		} else {
			try {
				if (config.getType() != null &&
						(ApplicationType.configExists(config.getType()) || !config.getParameters().get(MEETING_ID).equals(""))) {
					config.setType(config.getType().toLowerCase());
					conferenceService.save(IngestConfigurationDTO.convertToIngestConfiguration(config), metric.getMetId(),
							dto.getTitle(), dto.getId());
					return "redirect:/conferences/showFormForEditAdditionalMetrics?id=" + dto.getId()
							+ "&title=" + dto.getTitle() + "&additionalMetric=%3A+" + metric.getMetId() + "+%28"
							+ metric.getMetricIdentifier() + "%29";
				} else if (metric.getMetricIdentifier() != null) {
					AdditionalMetric metricObject = AdditionalMetricDTO.convertToAdditionalMetric(metric);
					metricObject.setConfig(conferenceService.findConfigById(metric.getIngestConfigId()));
					IngestConfiguration configObject = metricObject.getConfig();
					configObject.setType(ApplicationType.getFromString(metric.getConfigString()));
					conferenceService.save(configObject, metricObject.getId(), dto.getTitle(), dto.getId());
					conferenceService.save(metricObject, dto.getTitle(), dto.getId());
					return "redirect:/conferences/showFormForEditConferenceEdition?title=" + dto.getTitle()
							+ "&option=Edition%3A+" + dto.getId() + "+%28" + dto.getEdition() + "%29";
				} else if (dto.getCity() != null) {
					ConferenceEdition edition = ConferenceFrontendDTO
							.convertFrontendDTOToConferenceEdition(dto, conferenceService.findById(dto.getId()));
					conferenceService.save(edition, conference.getTitle());
					return "redirect:/conferences/showFormForEditConference?conferenceId=" + conference.getTitle();
				} else {
					Conference tempConference = conferenceService.findById(conference.getTitle());
					if (tempConference != null) {
						conference.setConferenceEditions(tempConference.getConferenceEditions());
						conferenceService.deleteById(tempConference.getTitle());
					}
					conferenceService.save(conference);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// use a redirect to prevent duplicate submissions
			return "redirect:/conferences/list";
		}
	}


	@GetMapping("/delete")
	public String delete(@RequestParam("conferenceId") String title) {

		// delete the conference
		conferenceService.deleteById(title);

		// redirect to /conferences/list
		return "redirect:/conferences/list";

	}

	@GetMapping("/search")
	public String search(@RequestParam("conference.name") String name,
						 Model model) {
		
		// check names, if empty then just give list of all conferences

		if (name.trim().isEmpty()) {
			return "redirect:/conferences/list";
		}
		else {
			// else, search by name
			List<Conference> conferences =
							conferenceService.searchBy(name);
			
			// add to the spring model
			model.addAttribute("conferences", conferences);
			
			// send to conferences-view
			return "conferences/conference-view";
		}
		
	}

	@GetMapping("/showFormForAdd")
	public String showFormForAdd(Model model) {

		// create model attribute to bind form data
		Conference conference = new Conference();

		model.addAttribute("conference", conference);

		return "conferences/conferences-form";
	}

	@GetMapping("/showFormForEditConference")
	public String showFormForEditConference(@RequestParam("conferenceId") String title,
									Model model) {

		// get the conference from the service
		Conference conference = conferenceService.findById(title);

		// set conference as a model attribute to pre-populate the form
		model.addAttribute("conference", conference);

		List<String> options = new ArrayList<>();

		if (conference != null && !conference.getConferenceEditions().isEmpty()) {
			for (ConferenceEdition edition : conference.getConferenceEditions()) {
				options.add("Edition: " + edition.getId() + " (" + edition.getEdition() + ")");
			}
		}
		options.add("Add new");
		model.addAttribute("options", options);
		SelectedMetricsDTO selectedMetricsDTO = new SelectedMetricsDTO();
		model.addAttribute("selectedMetricsDTO", selectedMetricsDTO);


		// send over to our form
		return "conferences/conferences-form-edit";
	}

	@GetMapping("/showFormForEditConferenceEdition")
	public String showFormForEditConferenceEdition(@RequestParam("title") String title, @RequestParam(value = "option") String option,
												   Model model) {

		// get the conference from the service
		Conference conference = conferenceService.findById(title);

		int confEditionId = -1;
		try {
			confEditionId = Integer.parseInt(option);
		} catch (Exception e) {
			try {
				if (!option.equals("Add new")) confEditionId = Integer.parseInt(option.split(" ")[1]);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		ConferenceFrontendDTO conferenceFrontendDTO = null;
		ConferenceEdition edition;
		// create new conference edition if no editions are present yet
		if (conferenceService.findById(confEditionId) == null) {
			edition = new ConferenceEdition(conferenceService.getMaxEditionId(),0, 0, 0, 0, 0,
					1.0f, 1.0f, 1.0f, "test", "test", "test");
			conferenceService.save(edition, title);
			conferenceFrontendDTO = ConferenceFrontendDTO.convertToFrontendDTO(conference, edition);
		}
		edition = conferenceService.findById(confEditionId);
		if (confEditionId != -1) conferenceFrontendDTO = ConferenceFrontendDTO
				.convertToFrontendDTO(conference, edition);

		// set conference as a model attribute to pre-populate the form
		model.addAttribute("conferenceFrontendDTO", conferenceFrontendDTO);

		List<String> additionalMetrics = new ArrayList<>();

		if (edition != null && !edition.getAdditionalMetrics().isEmpty()) {
			for (AdditionalMetric metric : edition.getAdditionalMetrics()) {
				additionalMetrics.add("Metric: " + metric.getId() + " (" + metric.getMetricIdentifier() + ")");
			}
		}
		additionalMetrics.add("Add new");

		model.addAttribute("additionalMetrics", additionalMetrics);

		// send over to our form
		return "conferences/conferences-form-edit-edition";
	}

	@GetMapping("/showFormForEditAdditionalMetrics")
	public String showFormForEditAdditionalMetrics(@ModelAttribute("conferenceFrontendDTO") ConferenceFrontendDTO dto,
												   @RequestParam(value = "additionalMetric") String additionalMetric,
												   Model model) {

		// get the conference from the service
		ConferenceEdition conferenceEdition = conferenceService.findById(dto.getEdition());

		int metricId = -1;
		try {
			metricId = Integer.parseInt(additionalMetric);
		} catch (Exception e) {
			try {
				if (!additionalMetric.equals("Add new")) metricId = Integer.parseInt(additionalMetric.split(" ")[1]);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		AdditionalMetricDTO additionalMetricDTO = null;

		// create new additionalMetric if no additionalMetrics are present yet
		if (conferenceService.findByMetricId(metricId) == null) {
			IngestConfiguration ingestConfiguration = new IngestConfiguration(conferenceService.getMaxConfigId(), MANUAL);
			AdditionalMetric metric = new AdditionalMetric(conferenceService.getMaxAdditionalMetricId(), ingestConfiguration, 0.0f, "test");
			metric.setConfig(ingestConfiguration);
			conferenceService.save(metric, dto.getTitle(), dto.getId());
			conferenceService.save(ingestConfiguration, metric.getId(), dto.getTitle(), dto.getId());
			additionalMetricDTO = mapperService.convertToAdditionalMetricDTO(metric.getId());
		}
		if (metricId != -1) additionalMetricDTO = mapperService.convertToAdditionalMetricDTO(metricId);

		// set conference as a model attribute to pre-populate the form
		List<String> types = ApplicationType.getTypes();
		types.forEach(s -> s = (s.substring(0, 1).toUpperCase() + s.substring(1)));
		model.addAttribute("conferenceFrontendDTO", dto);
		model.addAttribute("additionalMetric", additionalMetricDTO);
		model.addAttribute("types", types);

		assert additionalMetricDTO != null;
		String config = Integer.toString(additionalMetricDTO.getIngestConfigId());
		model.addAttribute("configs", config);

		// send over to our form
		return "conferences/conferences-form-additional-metrics";
	}

	@GetMapping("/showFormForEditConfigs")
	public String showFormForEditConfigs(@ModelAttribute("conferenceFrontendDTO") ConferenceFrontendDTO dto,
										 @ModelAttribute("additionalMetric") AdditionalMetricDTO additionalMetricDTO,
										 Model model) {

		// get the conference from the service
		ConferenceEdition conferenceEdition = conferenceService.findById(additionalMetricDTO.getConferenceEdition());

		IngestConfigurationDTO ingestConfigurationDTO;

		ingestConfigurationDTO = mapperService.convertToIngestConfigurationDTO(additionalMetricDTO.getIngestConfigId());

		// set conference as a model attribute to pre-populate the form
		model.addAttribute("config", ingestConfigurationDTO);
		model.addAttribute("parameters", ingestConfigurationDTO.getParameters());
		model.addAttribute("conferenceFrontendDTO", dto);

		// send over to our form
		return "conferences/conferences-form-config";
	}

	@GetMapping("/requestDashboard")
	public String requestDashboard(@RequestParam("conferenceId") String title, Model model) {

		Conference conference = conferenceService.findById(title);

		List<DashboardingMetricDTO> dashboardingMetricDTOs = new ArrayList<>();
		for (String metric : conferenceService.fetchAllMetricsPerConference()){
			dashboardingMetricDTOs.add(new DashboardingMetricDTO("", "", "", metric, false));
		}

		SelectedMetricsDTO selectedMetricsDTO = new SelectedMetricsDTO(dashboardingMetricDTOs);

		model.addAttribute("conference", conference);
		model.addAttribute("metrics", selectedMetricsDTO);
		model.addAttribute("types", ChartType.getChartTypes());
		model.addAttribute("operations", Operations.getOperations());

		return "conferences/select-metrics-dashboard-form";

	}

	@GetMapping("/getDashboard")
	public String getDashboard(@ModelAttribute Conference conference,
							   @ModelAttribute SelectedMetricsDTO metrics,
							   Model model) {

		if (metrics == null) metrics = new SelectedMetricsDTO();
		conference = conferenceService.findById(conference.getTitle());
		DashboardingUtils.convertToDashboard(conference, metrics.getOnlySelectedMetrics());

		try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
			final HttpPost httppost = new HttpPost("http://localhost:5601/api/saved_objects/_import?overwrite=true");
			final String apiKey = Files.lines(Paths.get("src/main/resources/properties.configuration"))
					.filter(s -> s.startsWith("elastic.kibana.apiKey")).findFirst().orElse("").split(" ")[1];
			httppost.setHeader(HttpHeaders.AUTHORIZATION, "ApiKey " + apiKey);
			httppost.setHeader("kbn-xsrf", true);


			final FileBody bin = new FileBody(new File(JSON_EXPORT + "export.ndjson"));

			final HttpEntity reqEntity = MultipartEntityBuilder.create()
					.addPart("file", bin)
					.build();


			httppost.setEntity(reqEntity);
			try (final CloseableHttpResponse response = httpclient.execute(httppost)) {
				System.out.println("----------------------------------------");
				System.out.println(response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String redirect = "http://localhost:5601/app/dashboards#/view/" + conference.getTitle() + "-dashboard";
		return "redirect:" + redirect;
	}

}


















