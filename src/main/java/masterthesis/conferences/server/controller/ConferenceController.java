package masterthesis.conferences.server.controller;

import masterthesis.conferences.data.MapperService;
import masterthesis.conferences.data.dto.ConferenceFrontendDTO;
import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;
import masterthesis.conferences.server.dashboarding.DashboardingUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
		this.mapperService = ServerController.getMapper();
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
			@ModelAttribute("conferenceFrontendDTO") ConferenceFrontendDTO dto
	) {

		if (bindingResult.hasErrors()) {
			return "conferences/conference-view";
		} else {
			if (dto.getCity() == null) conferenceService.save(conference);
			else {
				try {
					final Conference oldConference = mapperService.convertFrontendDTOToConference(dto);
					ConferenceEdition edition = mapperService.convertFrontendDTOToConferenceEdition(dto);
					conferenceService.save(edition, conference.getTitle());
				} catch (Exception e) {
					e.printStackTrace();
				}
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
			for (var id : conference.getConferenceEditionEditionNames()) {
				options.add("Edition: " + id);
			}
		}
		options.add("Add new");
		model.addAttribute("options", options);

		// send over to our form
		return "conferences/conferences-form-edit";
	}

	@GetMapping("/showFormForEditConferenceEdition")
	public String showFormForEditConferenceEdition(@RequestParam("title") String title, @RequestParam(value = "option") String option,
												   Model model) throws ExecutionException, InterruptedException {

		// get the conference from the service
		Conference conference = conferenceService.findById(title);

		int confEditionId = -1;
		try {
			if (!option.equals("Add new")) confEditionId = Integer.parseInt(option.split(" ")[1]);
			confEditionId = conference.convertEditionToId(confEditionId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ConferenceFrontendDTO conferenceFrontendDTO = null;

		// create new conference edition if no editions are present yet
		if (conferenceService.findById(confEditionId) == null) {
			ConferenceEdition edition = new ConferenceEdition(0, 0, 0, 0, 0,
					1.0f, 1.0f, 1.0f, "test", "test", "test");
			conferenceService.save(edition, title);
			conferenceFrontendDTO = mapperService.convertToFrontendDTO(title, edition.getId());
		}
		if (confEditionId != -1) conferenceFrontendDTO = mapperService.convertToFrontendDTO(title, confEditionId);

		// set conference as a model attribute to pre-populate the form
		model.addAttribute("conferenceFrontendDTO", conferenceFrontendDTO);

		// send over to our form
		return "conferences/conferences-form-edit-edition";
	}

	@GetMapping("/requestDashboard")
	public String requestDashboard(@RequestParam("conferenceId") String title, Model model) {

		Conference conference = conferenceService.findById(title);

		// TODO: list of DashboardingMetricDTO from UI
		DashboardingUtils.convertToDashboard(conference, new ArrayList<>());

		try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
			final HttpPost httppost = new HttpPost("http://localhost:5601/api/saved_objects/_import?overwrite=true");
			httppost.setHeader(HttpHeaders.AUTHORIZATION, "ApiKey WWR1cXBZRUJycWxiZWEzTjFUd2E6TF9Ra0cycXlSdkNqQWFUR0s2ck1CQQ==");
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

		String redirect = "http://localhost:5601/app/dashboards#/view/" + title + "-dashboard";
		return "redirect:" + redirect;
	}

}


















