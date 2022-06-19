package masterthesis.conferences.server.controller;

import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.server.rest.service.ConferenceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/conferences")
public class ConferenceController {

	private ConferenceService conferenceService;

	public ConferenceController(ConferenceService conferenceService) {
		conferenceService = conferenceService;
	}
	
	// add mapping for "/list"

	@GetMapping("/list")
	public String listConferences(Model model) {
		
		// get conferences from db
		List<Conference> conferences = conferenceService.findAll();
		
		// add to the spring model
		model.addAttribute("conferences", conferences);
		
		return "conferences/conferences-view";
	}
	
	@PostMapping("/save")
	public String saveConference(
			@ModelAttribute("conference") Conference conference,
			BindingResult bindingResult) {
		
		if (bindingResult.hasErrors()) {
			return "conferences/conference-view";
		}
		else {		
			// save the conference
			conferenceService.save(conference);
			
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
			return "conferences/conferences-view";
		}
		
	}
}


















